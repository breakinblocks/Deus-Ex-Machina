package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsAttachment;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import com.breakinblocks.deus_ex_machina.data.DeusExMobConfigManager;
import com.breakinblocks.deus_ex_machina.data.DeusExMobData;
import com.breakinblocks.deus_ex_machina.enums.TypeEnum;
import com.breakinblocks.deus_ex_machina.network.DeathBuffPayload;
import com.breakinblocks.deus_ex_machina.network.NetworkHandler;
import com.breakinblocks.deus_ex_machina.registry.EffectRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

import static com.breakinblocks.deus_ex_machina.DeusExMachina.debug;
import static com.breakinblocks.deus_ex_machina.registry.EffectRegistry.DEUS_EX_MACHINA_EFFECT;

@EventBusSubscriber(modid = DeusExMachina.MODID)
public class DeathEvents {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (entity instanceof Player player) {
            handlePlayerDeath(player, source);
        } else {
            handleMobDeath(entity, source);
        }
    }

    private static void handlePlayerDeath(Player player, DamageSource source) {
        LivingEntity killer = source.getEntity() instanceof LivingEntity ? (LivingEntity) source.getEntity() : null;
        if (killer == null) {
            DeusExMachina.LOGGER.info("[DeathEvents] Early return: killer is null");
            return;
        }
        if (!Config.isDeusExMob(source.getEntity().getType())) {
            DeusExMachina.LOGGER.info("[DeathEvents] Early return: {} is not a Deus Ex mob", source.getEntity().getType());
            return;
        }
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT)) {
            DeusExMachina.LOGGER.info("[DeathEvents] Early return: player doesn't have Deus Ex Machina effect");
            return;
        }

        ResourceLocation killerTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(killer.getType());

        DeusExBuffsHelper.withBuffsForMob(player, killer, (buff, groupKey) -> {
            TypeEnum trackingType = DeusExMobConfigManager.getType(groupKey);

            // Track buff changes for network sync
            Map<ResourceLocation, int[]> buffChanges = new HashMap<>(); // buffId -> [gain, newValue]

            // Iterate over all registered buff types
            for (BuffType buffType : BuffRegistry.getAll()) {
                if (!DeusExMobConfigManager.isBuffEnabled(groupKey, buffType.getId())) {
                    continue;
                }

                int increase = DeusExMobConfigManager.getBuffIncrease(groupKey, buffType);
                int max = DeusExMobConfigManager.getBuffMax(groupKey, buffType);
                int newValue;

                if (trackingType == TypeEnum.INSTANCE) {
                    DeusExMobData mobData = killer.getData(DeusExBuffsAttachment.DEUS_EX_MOB_DATA);
                    mobData.addBuff(player.getUUID(), buffType.getId(), increase, max);
                    newValue = mobData.getBuff(player.getUUID(), buffType.getId());
                } else {
                    int currentValue = buff.getBuff(groupKey, buffType.getId());
                    newValue = Math.min(max, currentValue + increase);
                    buff.setBuff(groupKey, buffType.getId(), newValue);
                }

                buffChanges.put(buffType.getId(), new int[]{increase, newValue});
                debug("Buff " + buffType.getId() + ": +" + increase + " (now " + newValue + ")");
            }

            debug("Player " + player.getName().getString() + " killed by Deus Ex Machina mob " + killer.getType() + ". Buffs updated.");

            // Send network payload with all buff changes
            if (player instanceof ServerPlayer serverPlayer && !buffChanges.isEmpty()) {
                DeusExMachina.LOGGER.info("[DeathEvents] Sending DeathBuffPayload to {} for mob {} (group: {})",
                        serverPlayer.getName().getString(), killerTypeId, groupKey);
                NetworkHandler.sendToPlayer(serverPlayer, new DeathBuffPayload(killerTypeId, buffChanges));
            }
        });
    }

    private static void handleMobDeath(LivingEntity entity, DamageSource source) {
        if (!Config.isDeusExMob(entity.getType())) return;

        DeusExBuffsHelper.getGroupKey(entity).ifPresent(groupKey -> {
            TypeEnum trackingType = DeusExMobConfigManager.getType(groupKey);

            if (trackingType == TypeEnum.INSTANCE) {
                // Instance mode: mob dies, attachment dies with it - nothing to do!
                debug("Instance mode: Mob " + entity.getUUID() + " died, buffs removed automatically");
            } else {
                // Entity type mode: reset buffs for the killing player
                handleEntityTypeModeMobDeath(entity, source, groupKey);
            }
        });
    }

    private static void handleEntityTypeModeMobDeath(LivingEntity entity, DamageSource source, String groupKey) {
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT)) return;

        DeusExBuffsHelper.withBuffs(player, buff -> {
            // Iterate over all registered buff types
            for (BuffType buffType : BuffRegistry.getAll()) {
                if (!DeusExMobConfigManager.isBuffEnabled(groupKey, buffType.getId())) {
                    continue;
                }

                int min = DeusExMobConfigManager.getBuffMin(groupKey, buffType);
                int increase = DeusExMobConfigManager.getBuffIncrease(groupKey, buffType);
                int currentValue = buff.getBuff(groupKey, buffType.getId());

                switch (DeusExMobConfigManager.getBuffReset(groupKey, buffType)) {
                    case FULL -> buff.setBuff(groupKey, buffType.getId(), min);
                    case PARTIAL -> buff.setBuff(groupKey, buffType.getId(), Math.max(min, currentValue - increase));
                    case NONE -> {} // Keep current value
                }
            }

            debug("Player " + player.getName().getString() + " killed Deus Ex Machina mob " + entity.getType());
        });
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        // With data attachments, data is automatically copied on death if the attachment is serializable
        // But we still need to handle the manual copy for edge cases
        DeusExBuffsHelper.copyBuffs(event.getOriginal(), event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (DeusExBuffsHelper.isEnabled(player)) {
                player.addEffect(new MobEffectInstance(
                        DEUS_EX_MACHINA_EFFECT, -1, 0, false, false, Config.showIcon
                ));
            } else {
                player.removeEffect(DEUS_EX_MACHINA_EFFECT);
            }
        }
    }
}
