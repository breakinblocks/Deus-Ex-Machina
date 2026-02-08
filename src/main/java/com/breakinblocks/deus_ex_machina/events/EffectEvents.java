package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import static com.breakinblocks.deus_ex_machina.registry.EffectRegistry.DEUS_EX_MACHINA_EFFECT;

@EventBusSubscriber(modid = DeusExMachina.MODID)
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
        if (!instance.is(DEUS_EX_MACHINA_EFFECT)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        DeusExBuffsHelper.setEnabled(player, enabled);
    }
}
