package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
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

        String storageKey = DeusExBuffsHelper.getGroupKey(attacker.getType()).orElse(null);
        String configKey = DeusExBuffsHelper.getConfigKey(attacker.getType()).orElse(null);
        if (storageKey == null || configKey == null) return;

        float damage = event.getNewDamage();
        BuffContext context = BuffContext.playerHurt(player, livingAttacker, event);

        // Apply all buffs that work on player hurt
        for (BuffType buffType : BuffRegistry.getAll()) {
            if (!buffType.appliesOnPlayerHurt()) continue;
            if (!DeusExMobConfigManager.isBuffEnabled(configKey, buffType.getId())) continue;

            int buffValue;
            if (DeusExMobConfigManager.getType(configKey) == TypeEnum.INSTANCE) {
                // Instance mode: get buff from mob's attachment
                DeusExMobData mobData = livingAttacker.getData(DeusExBuffsAttachment.DEUS_EX_MOB_DATA);
                buffValue = mobData.getBuff(player.getUUID(), buffType.getId());
            } else {
                // Entity type mode: get buff from player's attachment
                buffValue = DeusExBuffsHelper.getBuffs(player).getBuff(storageKey, buffType.getId());
            }

            if (buffValue != 0) {
                float newDamage = buffType.apply(buffValue, damage, context);
                debug("Applied " + buffType.getId() + " (value=" + buffValue + "): " + damage + " -> " + newDamage);
                damage = newDamage;
            }
        }

        if (damage != event.getNewDamage()) {
            event.setNewDamage(damage);
            debug("Player " + player.getName().getString() + " hurt by " + storageKey + ". Final damage: " + damage);
        }
    }

    private static void handleMobHurt(LivingDamageEvent.Pre event, LivingEntity entity, DamageSource source) {
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT)) return;

        String storageKey = DeusExBuffsHelper.getGroupKey(entity).orElse(null);
        String configKey = DeusExBuffsHelper.getConfigKey(entity).orElse(null);
        if (storageKey == null || configKey == null) return;

        float damage = event.getNewDamage();
        BuffContext context = BuffContext.playerAttack(player, entity, event);

        // Apply all buffs that work on player attack
        for (BuffType buffType : BuffRegistry.getAll()) {
            if (!buffType.appliesOnPlayerAttack()) continue;
            if (!DeusExMobConfigManager.isBuffEnabled(configKey, buffType.getId())) continue;

            int buffValue;
            if (DeusExMobConfigManager.getType(configKey) == TypeEnum.INSTANCE) {
                // Instance mode: get buff from mob's attachment
                DeusExMobData mobData = entity.getData(DeusExBuffsAttachment.DEUS_EX_MOB_DATA);
                buffValue = mobData.getBuff(player.getUUID(), buffType.getId());
            } else {
                // Entity type mode: get buff from player's attachment
                buffValue = DeusExBuffsHelper.getBuffs(player).getBuff(storageKey, buffType.getId());
            }

            if (buffValue != 0) {
                float newDamage = buffType.apply(buffValue, damage, context);
                debug("Applied " + buffType.getId() + " (value=" + buffValue + "): " + damage + " -> " + newDamage);
                damage = newDamage;
            }
        }

        if (damage != event.getNewDamage()) {
            event.setNewDamage(damage);
            debug("Player " + player.getName().getString() + " hurting " + storageKey + ". Final damage: " + damage);
        }
    }
}
