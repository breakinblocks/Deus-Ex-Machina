package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
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
        if (!Config.isDeusExMob(source.getEntity().getType())) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

        DeusExBuffsHelper.withBuffsForMob(player, source.getEntity().getType(), (buff, key) -> {
            int resistance = buff.getResistance(key);
            if (resistance > 0) {
                float originalDamage = event.getAmount();
                float newDamage = Math.max(0, originalDamage * (1 - resistance / 100f));
                event.setAmount(newDamage);
                debug("Player " + player.getName().getString() + " hurt by " + key + ". Damage: " + originalDamage + " -> " + newDamage + " (" + resistance + "% resistance)");
            }
        });
    }

    private static void handleMobHurt(LivingHurtEvent event, LivingEntity entity, DamageSource source) {
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;
        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

        DeusExBuffsHelper.withBuffsForMob(player, entity, (buff, key) -> {
            int attackBoost = buff.getStrength(key);
            if (attackBoost > 0) {
                float originalDamage = event.getAmount();
                float newDamage = originalDamage * (1 + attackBoost / 100f);
                event.setAmount(newDamage);
                debug("Player " + player.getName().getString() + " hurting " + key + ". Damage: " + originalDamage + " -> " + newDamage + " (" + attackBoost + "% boost)");
            }
        });
    }
}
