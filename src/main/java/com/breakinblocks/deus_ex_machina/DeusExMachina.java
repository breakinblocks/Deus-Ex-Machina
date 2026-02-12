package com.breakinblocks.deus_ex_machina;

import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.network.NetworkHandler;
import com.breakinblocks.deus_ex_machina.registry.BrewingRegistry;
import com.breakinblocks.deus_ex_machina.registry.BuiltinBuffs;
import com.breakinblocks.deus_ex_machina.registry.EffectRegistry;
import com.breakinblocks.deus_ex_machina.registry.ItemRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DeusExMachina.MODID)
public class DeusExMachina {

    public static final String MODID = "deus_ex_machina";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static boolean debug = false;

    public DeusExMachina() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        debug = Config.debugMode;
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        EffectRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);

        // Register buff types
        BuiltinBuffs.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
        event.enqueueWork(BrewingRegistry::registerRecipes);

        // Freeze the buff registry after setup
        BuffRegistry.freeze();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS){
            event.accept(ItemRegistry.AMBROSIA.get());
        }
    }

    public static void debug (String message) {
        if (!debug) return;
        LOGGER.debug(message);
    }

}
