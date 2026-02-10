package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
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

            boolean resistanceEnabled = DeusExMobConfigManager.isResistanceEnabled(groupKey);
            boolean attackEnabled = DeusExMobConfigManager.isAttackEnabled(groupKey);
            int resistanceGain = 0;
            int attackBoostGain = 0;
            int newResistance = 0;
            int newAttackBoost = 0;

            if (trackingType == TypeEnum.INSTANCE) {
                // Instance mode: store buff on the mob's attachment
                DeusExMobData mobData = killer.getData(DeusExBuffsAttachment.DEUS_EX_MOB_DATA);

                if (resistanceEnabled) {
                    resistanceGain = DeusExMobConfigManager.getResistanceIncrease(groupKey);
                    int maxRes = DeusExMobConfigManager.getResistanceMax(groupKey);
                    mobData.addResistance(player.getUUID(), resistanceGain, maxRes);
                    newResistance = mobData.getResistance(player.getUUID());
                }

                if (attackEnabled) {
                    attackBoostGain = DeusExMobConfigManager.getAttackIncrease(groupKey);
                    int maxAtk = DeusExMobConfigManager.getAttackMax(groupKey);
                    mobData.addStrength(player.getUUID(), attackBoostGain, maxAtk);
                    newAttackBoost = mobData.getStrength(player.getUUID());
                }

                debug("Instance mode: Stored buff on mob " + killer.getUUID() + " for player " + player.getName().getString());
            } else {
                // Entity type mode: store buff on the player
                if (resistanceEnabled) {
                    resistanceGain = DeusExMobConfigManager.getResistanceIncrease(groupKey);
                    buff.addResistance(groupKey, resistanceGain);
                    newResistance = buff.getResistance(groupKey);
                }

                if (attackEnabled) {
                    attackBoostGain = DeusExMobConfigManager.getAttackIncrease(groupKey);
                    buff.addStrength(groupKey, attackBoostGain);
                    newAttackBoost = buff.getStrength(groupKey);
                }
            }

            debug("Player " + player.getName().getString() + " killed by Deus Ex Machina mob " + killer.getType() + ". Buffs updated.");
            debug("Resistance: " + newResistance + " (+" + resistanceGain + ")");
            debug("Attack Boost: " + newAttackBoost + " (+" + attackBoostGain + ")");

            if (player instanceof ServerPlayer serverPlayer) {
                DeusExMachina.LOGGER.info("[DeathEvents] Sending DeathBuffPayload to {} for mob {} (group: {})",
                        serverPlayer.getName().getString(), killerTypeId, groupKey);
                NetworkHandler.sendToPlayer(serverPlayer, new DeathBuffPayload(
                        killerTypeId, resistanceEnabled, attackEnabled,
                        resistanceGain, attackBoostGain, newResistance, newAttackBoost
                ));
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
            if (DeusExMobConfigManager.isResistanceEnabled(groupKey)) {
                int resMin = DeusExMobConfigManager.getResistanceMin(groupKey);
                int resIncrease = DeusExMobConfigManager.getResistanceIncrease(groupKey);
                switch (DeusExMobConfigManager.getResistanceReset(groupKey)) {
                    case FULL -> buff.setResistance(groupKey, resMin);
                    case PARTIAL -> buff.setResistance(groupKey, Math.max(resMin, buff.getResistance(groupKey) - resIncrease));
                    case NONE -> {} // Keep current value
                }
            }

            if (DeusExMobConfigManager.isAttackEnabled(groupKey)) {
                int atkMin = DeusExMobConfigManager.getAttackMin(groupKey);
                int atkIncrease = DeusExMobConfigManager.getAttackIncrease(groupKey);
                switch (DeusExMobConfigManager.getAttackReset(groupKey)) {
                    case FULL -> buff.setStrength(groupKey, atkMin);
                    case PARTIAL -> buff.setStrength(groupKey, Math.max(atkMin, buff.getStrength(groupKey) - atkIncrease));
                    case NONE -> {} // Keep current value
                }
            }

            debug("Player " + player.getName().getString() + " killed Deus Ex Machina mob " + entity.getType() +
                    ". Resistance: " + buff.getResistance(groupKey) + ", Attack Boost: " + buff.getStrength(groupKey));
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
