package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsProvider;
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
        if (killer == null) return;
        if (!Config.isDeusExMob(source.getEntity().getType())) return;

        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

        player.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(buff -> {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(killer.getType());
            if (key == null) return;
            buff.addResistance(key, Config.resistanceIncrease);
            buff.addStrength(key , Config.attackBoostIncrease);
            System.out.println("Player " + player.getName().getString() + " killed by Deus Ex Machina mob " + killer.getType() + ". Resistance and Attack Boost increased.");
            System.out.println("New Resistance for " + key + ": " + buff.getResistance(key));
            System.out.println("New Attack Boost for " + key + ": " + buff.getStrength(key));
        });
    }

    private static void handleMobDeath(LivingEntity entity, DamageSource source) {
        if (!Config.isDeusExMob(entity.getType())) return;
        if (source.getEntity() == null) return;
        if (!(source.getEntity() instanceof Player player)) return;

        if (!player.hasEffect(EffectRegistry.DEUS_EX_MACHINA_EFFECT.get())) return;

        player.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(buff -> {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
            if (key == null) return;
            if (Config.resistanceReset) buff.setResistance(key, Config.minResistance);
            if (Config.attackBoostReset) buff.setStrength(key, Config.minResistance);
            debug("Player " + player.getName().getString() + " killed Deus Ex Machina mob " + entity.getType() + ". Resistance and Attack Boost for this mob reset to minimum.");
        });


    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(oldStore -> {
            event.getEntity().getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(newStore -> {
                newStore.copyFrom(oldStore);
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(buff -> {
                if (buff.isEnabled()) {
                    player.addEffect(new MobEffectInstance(
                            DEUS_EX_MACHINA_EFFECT.get(), -1, 0, false, false, Config.showIcon
                    ));
                } else {
                    player.removeEffect(DEUS_EX_MACHINA_EFFECT.get());
                }
            });
        }
    }

}
