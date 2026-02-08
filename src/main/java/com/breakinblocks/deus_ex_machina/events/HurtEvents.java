package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsProvider;
import com.breakinblocks.deus_ex_machina.registry.EffectRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static com.breakinblocks.deus_ex_machina.DeusExMachina.debug;

@Mod.EventBusSubscriber(modid = DeusExMachina.MODID)
public class HurtEvents {

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
            DamageSource source = event.getSource();
        if (entity instanceof Player player) {
            if (source.getEntity() == null) return;
            if (!Config.isDeusExMob(source.getEntity().getType())) return;

            if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

            player.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(buff -> {
                ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(source.getEntity().getType());
                if (key == null) return;
                int resistance = buff.getResistance(key);
                if (resistance > 0) {
                    float originalDamage = event.getAmount();
                    float newDamage = originalDamage * (1 - resistance / 100f);
                    if (newDamage < 0) newDamage = 0;
                    event.setAmount(newDamage);
                    debug("Player " + player.getName().getString() + " is being hurt by Deus Ex Machina mob " + source.getEntity().getType() + ". Original Damage: " + originalDamage + ", Resistance: " + resistance + "%, New Damage: " + newDamage);
                }
            });

        } else if (Config.isDeusExMob(entity.getType())) {
            if (source.getEntity() == null) return;
            if (!(source.getEntity() instanceof Player player)) return;
            if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

            player.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(buff -> {
                ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
                if (key == null) return;
                int attackBoost = buff.getStrength(key);
                if (attackBoost > 0) {
                    float originalDamage = event.getAmount();
                    float newDamage = originalDamage * (1 + attackBoost / 100f);
                    event.setAmount(newDamage);
                    debug("Player " + player.getName().getString() + " is hurting Deus Ex Machina mob " + entity.getType() + ". Original Damage: " + originalDamage + ", Attack Boost: " + attackBoost + "%, New Damage: " + newDamage);
                }
            });
        }

    }


}
