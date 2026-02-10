package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import com.breakinblocks.deus_ex_machina.data.DeusExMobConfigManager;
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

        DeusExBuffsHelper.withBuffsForMob(player, killer, (buff, key) -> {
            boolean resistanceEnabled = DeusExMobConfigManager.isResistanceEnabled(key);
            boolean attackEnabled = DeusExMobConfigManager.isAttackEnabled(key);
            int resistanceGain = 0;
            int attackBoostGain = 0;

            if (resistanceEnabled) {
                resistanceGain = DeusExMobConfigManager.getResistanceIncrease(key);
                buff.addResistance(key, resistanceGain);
            }

            if (attackEnabled) {
                attackBoostGain = DeusExMobConfigManager.getAttackIncrease(key);
                buff.addStrength(key, attackBoostGain);
            }

            int newResistance = buff.getResistance(key);
            int newAttackBoost = buff.getStrength(key);

            debug("Player " + player.getName().getString() + " killed by Deus Ex Machina mob " + killer.getType() + ". Buffs updated.");
            debug("Resistance for " + key + ": " + newResistance + " (+" + resistanceGain + ")");
            debug("Attack Boost for " + key + ": " + newAttackBoost + " (+" + attackBoostGain + ")");

            if (player instanceof ServerPlayer serverPlayer) {
                DeusExMachina.LOGGER.info("[DeathEvents] Sending DeathBuffPayload to {} for mob {} (group: {})",
                        serverPlayer.getName().getString(), killerTypeId, key);
                NetworkHandler.sendToPlayer(serverPlayer, new DeathBuffPayload(
                        killerTypeId, resistanceEnabled, attackEnabled,
                        resistanceGain, attackBoostGain, newResistance, newAttackBoost
                ));
            }
        });
    }

    private static void handleMobDeath(LivingEntity entity, DamageSource source) {
        if (!Config.isDeusExMob(entity.getType())) return;
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;

        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT)) return;

        DeusExBuffsHelper.withBuffsForMob(player, entity, (buff, key) -> {
            if (DeusExMobConfigManager.isResistanceEnabled(key)) {
                int resMin = DeusExMobConfigManager.getResistanceMin(key);
                int resIncrease = DeusExMobConfigManager.getResistanceIncrease(key);
                switch(DeusExMobConfigManager.getResistanceReset(key)) {
                    case FULL -> buff.setResistance(key, resMin);
                    case PARTIAL -> buff.setResistance(key, Math.max(resMin, buff.getResistance(key) - resIncrease));
                    case NONE -> {} // Keep current value
                }
            }

            if (DeusExMobConfigManager.isAttackEnabled(key)) {
                int atkMin = DeusExMobConfigManager.getAttackMin(key);
                int atkIncrease = DeusExMobConfigManager.getAttackIncrease(key);
                switch(DeusExMobConfigManager.getAttackReset(key)) {
                    case FULL -> buff.setStrength(key, atkMin);
                    case PARTIAL -> buff.setStrength(key, Math.max(atkMin, buff.getStrength(key) - atkIncrease));
                    case NONE -> {} // Keep current value
                }
            }

            debug("Player " + player.getName().getString() + " killed Deus Ex Machina mob " + entity.getType() + ". Resistance: " + buff.getResistance(key) + ", Attack Boost: " + buff.getStrength(key));
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
