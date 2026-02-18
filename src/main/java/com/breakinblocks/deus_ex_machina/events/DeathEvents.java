package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import com.breakinblocks.deus_ex_machina.data.DeusExMobConfigManager;
import com.breakinblocks.deus_ex_machina.data.DeusExMobData;
import com.breakinblocks.deus_ex_machina.data.DeusExMobDataProvider;
import com.breakinblocks.deus_ex_machina.enums.TypeEnum;
import com.breakinblocks.deus_ex_machina.network.DeathBuffPacket;
import com.breakinblocks.deus_ex_machina.network.NetworkHandler;
import com.breakinblocks.deus_ex_machina.registry.EffectRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

import static com.breakinblocks.deus_ex_machina.DeusExMachina.debug;
import static com.breakinblocks.deus_ex_machina.registry.EffectRegistry.DEUS_EX_MACHINA_EFFECT;


@Mod.EventBusSubscriber(modid = DeusExMachina.MODID)
public class DeathEvents {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (entity instanceof Player) {
            handlePlayerDeath((Player) entity, source);
        } else  {
            handleMobDeath(entity, source);
        }
    }

    private static void handlePlayerDeath(Player player, DamageSource source) {
        LivingEntity killer = source.getEntity() instanceof LivingEntity ? (LivingEntity) source.getEntity() : null;
        if (player.getServer().isHardcore()) return;
        if (killer == null) {
            DeusExMachina.LOGGER.info("[DeathEvents] Early return: killer is null");
            return;
        }
        if (!Config.isDeusExMob(source.getEntity().getType())) {
            DeusExMachina.LOGGER.info("[DeathEvents] Early return: {} is not a Deus Ex mob", source.getEntity().getType());
            return;
        }
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) {
            DeusExMachina.LOGGER.info("[DeathEvents] Early return: player doesn't have Deus Ex Machina effect");
            return;
        }

        ResourceLocation killerTypeId = ForgeRegistries.ENTITY_TYPES.getKey(killer.getType());

        DeusExBuffsHelper.withBuffsForMob(player, killer, (buff, storageKey, configKey) -> {
            TypeEnum trackingType = DeusExMobConfigManager.getType(configKey);

            // Track buff changes for network sync
            Map<ResourceLocation, int[]> buffChanges = new HashMap<>(); // buffId -> [gain, newValue]

            // Iterate over all registered buff types
            for (BuffType buffType : BuffRegistry.getAll()) {
                if (!DeusExMobConfigManager.isBuffEnabled(configKey, buffType.getId())) {
                    continue;
                }

                int increase = DeusExMobConfigManager.getBuffIncrease(configKey, buffType);
                int max = DeusExMobConfigManager.getBuffMax(configKey, buffType);
                int newValue;

                if (trackingType == TypeEnum.INSTANCE) {
                    // Instance mode: store buff on the mob's capability
                    DeusExMobData mobData = killer.getCapability(DeusExMobDataProvider.DEUS_EX_MOB_DATA)
                            .orElseThrow(() -> new IllegalStateException("Missing mob data capability on " + killer));
                    mobData.addBuff(player.getUUID(), buffType.getId(), increase, max);
                    newValue = mobData.getBuff(player.getUUID(), buffType.getId());
                } else {
                    // Entity type mode: store buff on the player using storage key
                    int currentValue = buff.getBuff(storageKey, buffType.getId());
                    newValue = Math.min(max, currentValue + increase);
                    buff.setBuff(storageKey, buffType.getId(), newValue);
                }

                buffChanges.put(buffType.getId(), new int[]{increase, newValue});
                debug("Buff " + buffType.getId() + ": +" + increase + " (now " + newValue + ")");
            }

            debug("Player " + player.getName().getString() + " killed by Deus Ex Machina mob " + killer.getType() + ". Buffs updated.");

            // Send network packet with all buff changes
            if (player instanceof ServerPlayer serverPlayer && !buffChanges.isEmpty()) {
                DeusExMachina.LOGGER.info("[DeathEvents] Sending DeathBuffPacket to {} for mob {} (storage: {}, config: {})",
                        serverPlayer.getName().getString(), killerTypeId, storageKey, configKey);
                NetworkHandler.sendToPlayer(serverPlayer, new DeathBuffPacket(killerTypeId, buffChanges));
            }
        });
    }

    private static void handleMobDeath(LivingEntity entity, DamageSource source) {
        if (!Config.isDeusExMob(entity.getType())) return;
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;

        String storageKey = DeusExBuffsHelper.getGroupKey(entity.getType()).orElse(null);
        String configKey = DeusExBuffsHelper.getConfigKey(entity.getType()).orElse(null);
        if (storageKey == null || configKey == null) return;

        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

        DeusExBuffsHelper.withBuffs(player, buff -> {
            // Iterate over all registered buff types
            for (BuffType buffType : BuffRegistry.getAll()) {
                if (!DeusExMobConfigManager.isBuffEnabled(configKey, buffType.getId())) {
                    continue;
                }

                int min = DeusExMobConfigManager.getBuffMin(configKey, buffType);
                int increase = DeusExMobConfigManager.getBuffIncrease(configKey, buffType);
                int currentValue = buff.getBuff(storageKey, buffType.getId());

                switch (DeusExMobConfigManager.getBuffReset(configKey, buffType)) {
                    case FULL -> buff.setBuff(storageKey, buffType.getId(), min);
                    case PARTIAL -> buff.setBuff(storageKey, buffType.getId(), Math.max(min, currentValue - increase));
                    case NONE -> {} // Keep current value
                }
            }

            debug("Player " + player.getName().getString() + " killed Deus Ex Machina mob " + entity.getType());
        });
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        DeusExBuffsHelper.copyBuffs(event.getOriginal(), event.getEntity());
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (DeusExBuffsHelper.isEnabled(player)) {
                player.addEffect(new MobEffectInstance(
                        DEUS_EX_MACHINA_EFFECT.get(), -1, 0, false, false, Config.showIcon
                ));
            } else {
                player.removeEffect(DEUS_EX_MACHINA_EFFECT.get());
            }
        }
    }

}
