package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.data.DeusExMobConfigManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeusExMachina.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new DeusExMobConfigManager());
        DeusExMachina.LOGGER.info("Registered DeusExMobConfigManager reload listener");
    }
}
