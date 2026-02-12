package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import com.breakinblocks.deus_ex_machina.data.DeusExMobConfigManager;
import com.breakinblocks.deus_ex_machina.data.DeusExMobData;
import com.breakinblocks.deus_ex_machina.data.DeusExMobDataProvider;
import com.breakinblocks.deus_ex_machina.enums.TypeEnum;
import com.breakinblocks.deus_ex_machina.registry.EffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.breakinblocks.deus_ex_machina.DeusExMachina.debug;

@Mod.EventBusSubscriber(modid = DeusExMachina.MODID)
public class HurtEvents {

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (entity instanceof Player player) {
            handlePlayerHurt(event, player, source);
        } else if (Config.isDeusExMob(entity.getType())) {
            handleMobHurt(event, entity, source);
        }
    }

    private static void handlePlayerHurt(LivingHurtEvent event, Player player, DamageSource source) {
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof LivingEntity livingAttacker)) return;
        if (!Config.isDeusExMob(source.getEntity().getType())) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

        DeusExBuffsHelper.getGroupKey(livingAttacker.getType()).ifPresent(groupKey -> {
            float damage = event.getAmount();
            BuffContext context = BuffContext.playerHurt(player, livingAttacker, event);

            // Apply all buffs that work on player hurt
            for (BuffType buffType : BuffRegistry.getAll()) {
                if (!buffType.appliesOnPlayerHurt()) continue;
                if (!DeusExMobConfigManager.isBuffEnabled(groupKey, buffType.getId())) continue;

                int buffValue;
                if (DeusExMobConfigManager.getType(groupKey) == TypeEnum.INSTANCE) {
                    // Instance mode: get buff from mob's capability
                    DeusExMobData mobData = livingAttacker.getCapability(DeusExMobDataProvider.DEUS_EX_MOB_DATA)
                            .orElse(null);
                    buffValue = mobData != null ? mobData.getBuff(player.getUUID(), buffType.getId()) : 0;
                } else {
                    // Entity type mode: get buff from player's capability
                    buffValue = DeusExBuffsHelper.getBuffs(player)
                            .map(buff -> buff.getBuff(groupKey, buffType.getId()))
                            .orElse(0);
                }

                if (buffValue != 0) {
                    float newDamage = buffType.apply(buffValue, damage, context);
                    debug("Applied " + buffType.getId() + " (value=" + buffValue + "): " + damage + " -> " + newDamage);
                    damage = newDamage;
                }
            }

            if (damage != event.getAmount()) {
                event.setAmount(damage);
                debug("Player " + player.getName().getString() + " hurt by " + groupKey + ". Final damage: " + damage);
            }
        });
    }

    private static void handleMobHurt(LivingHurtEvent event, LivingEntity entity, DamageSource source) {
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

        DeusExBuffsHelper.getGroupKey(entity).ifPresent(groupKey -> {
            float damage = event.getAmount();
            BuffContext context = BuffContext.playerAttack(player, entity, event);

            // Apply all buffs that work on player attack
            for (BuffType buffType : BuffRegistry.getAll()) {
                if (!buffType.appliesOnPlayerAttack()) continue;
                if (!DeusExMobConfigManager.isBuffEnabled(groupKey, buffType.getId())) continue;

                int buffValue;
                if (DeusExMobConfigManager.getType(groupKey) == TypeEnum.INSTANCE) {
                    // Instance mode: get buff from mob's capability
                    DeusExMobData mobData = entity.getCapability(DeusExMobDataProvider.DEUS_EX_MOB_DATA)
                            .orElse(null);
                    buffValue = mobData != null ? mobData.getBuff(player.getUUID(), buffType.getId()) : 0;
                } else {
                    // Entity type mode: get buff from player's capability
                    buffValue = DeusExBuffsHelper.getBuffs(player)
                            .map(buff -> buff.getBuff(groupKey, buffType.getId()))
                            .orElse(0);
                }

                if (buffValue != 0) {
                    float newDamage = buffType.apply(buffValue, damage, context);
                    debug("Applied " + buffType.getId() + " (value=" + buffValue + "): " + damage + " -> " + newDamage);
                    damage = newDamage;
                }
            }

            if (damage != event.getAmount()) {
                event.setAmount(damage);
                debug("Player " + player.getName().getString() + " hurting " + groupKey + ". Final damage: " + damage);
            }
        });
    }
}
