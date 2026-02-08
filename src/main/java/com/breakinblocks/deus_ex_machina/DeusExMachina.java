package com.breakinblocks.deus_ex_machina;

import com.breakinblocks.deus_ex_machina.data.DeusExBuffsAttachment;
import com.breakinblocks.deus_ex_machina.network.NetworkHandler;
import com.breakinblocks.deus_ex_machina.registry.EffectRegistry;
import com.breakinblocks.deus_ex_machina.registry.ItemRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(DeusExMachina.MODID)
public class DeusExMachina {

    public static final String MODID = "deus_ex_machina";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static boolean debug = false;

    public DeusExMachina(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        debug = Config.debugMode;

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(NetworkHandler::register);

        EffectRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        DeusExBuffsAttachment.ATTACHMENT_TYPES.register(modEventBus);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ItemRegistry.AMBROSIA.get());
        }
    }

    public static void debug(String message) {
        if (!debug) return;
        LOGGER.debug(message);
    }
}
