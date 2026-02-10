package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsAttachment;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import com.breakinblocks.deus_ex_machina.data.DeusExMobConfigManager;
import com.breakinblocks.deus_ex_machina.data.DeusExMobData;
import com.breakinblocks.deus_ex_machina.enums.TypeEnum;
import com.breakinblocks.deus_ex_machina.registry.EffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import static com.breakinblocks.deus_ex_machina.DeusExMachina.debug;

@EventBusSubscriber(modid = DeusExMachina.MODID)
public class HurtEvents {

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (entity instanceof Player player) {
            handlePlayerHurt(event, player, source);
        } else if (Config.isDeusExMob(entity.getType())) {
            handleMobHurt(event, entity, source);
        }
    }

    private static void handlePlayerHurt(LivingDamageEvent.Pre event, Player player, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null) return;
        if (!(attacker instanceof LivingEntity livingAttacker)) return;
        if (!Config.isDeusExMob(attacker.getType())) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT)) return;

        DeusExBuffsHelper.getGroupKey(attacker.getType()).ifPresent(groupKey -> {
            if (!DeusExMobConfigManager.isResistanceEnabled(groupKey)) return;

            int resistance;
            if (DeusExMobConfigManager.getType(groupKey) == TypeEnum.INSTANCE) {
                // Instance mode: get resistance from mob's attachment
                DeusExMobData mobData = livingAttacker.getData(DeusExBuffsAttachment.DEUS_EX_MOB_DATA);
                resistance = mobData.getResistance(player.getUUID());
            } else {
                // Entity type mode: get resistance from player's attachment
                resistance = DeusExBuffsHelper.getBuffs(player).getResistance(groupKey);
            }

            if (resistance > 0) {
                float originalDamage = event.getNewDamage();
                float newDamage = Math.max(0, originalDamage * (1 - resistance / 100f));
                event.setNewDamage(newDamage);
                debug("Player " + player.getName().getString() + " hurt by " + groupKey + ". Damage: " + originalDamage + " -> " + newDamage + " (" + resistance + "% resistance)");
            }
        });
    }

    private static void handleMobHurt(LivingDamageEvent.Pre event, LivingEntity entity, DamageSource source) {
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT)) return;

        DeusExBuffsHelper.getGroupKey(entity).ifPresent(groupKey -> {
            if (!DeusExMobConfigManager.isAttackEnabled(groupKey)) return;

            int attackBoost;
            if (DeusExMobConfigManager.getType(groupKey) == TypeEnum.INSTANCE) {
                // Instance mode: get strength from mob's attachment
                DeusExMobData mobData = entity.getData(DeusExBuffsAttachment.DEUS_EX_MOB_DATA);
                attackBoost = mobData.getStrength(player.getUUID());
            } else {
                // Entity type mode: get strength from player's attachment
                attackBoost = DeusExBuffsHelper.getBuffs(player).getStrength(groupKey);
            }

            if (attackBoost > 0) {
                float originalDamage = event.getNewDamage();
                float newDamage = originalDamage * (1 + attackBoost / 100f);
                event.setNewDamage(newDamage);
                debug("Player " + player.getName().getString() + " hurting " + groupKey + ". Damage: " + originalDamage + " -> " + newDamage + " (" + attackBoost + "% boost)");
            }
        });
    }
}
