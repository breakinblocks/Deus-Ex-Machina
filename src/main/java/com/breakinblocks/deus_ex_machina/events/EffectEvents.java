package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.breakinblocks.deus_ex_machina.registry.EffectRegistry.DEUS_EX_MACHINA_EFFECT;

@Mod.EventBusSubscriber(modid = DeusExMachina.MODID)
public class EffectEvents {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        buffHandler(event, true);
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        buffHandler(event, false);
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
            buffHandler(event, false);
     }

    private static void buffHandler(MobEffectEvent event, boolean enabled) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) return;

        if (event.getEffectInstance().getEffect() != DEUS_EX_MACHINA_EFFECT.get()) return;

        if (!(event.getEntity() instanceof Player player)) return;

        player.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(buff -> {
         buff.setEnabled(enabled);
        });
     }
}
