package com.breakinblocks.deus_ex_machina;

import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import static com.breakinblocks.deus_ex_machina.enums.ResetEnum.*;

@EventBusSubscriber(modid = DeusExMachina.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SHOW_ICON = BUILDER
            .comment("Show Deus Ex Machina icon on the player's HUD when the effect is active.")
            .define("showIcon", true);

    private static final ModConfigSpec.BooleanValue DEBUG_MODE = BUILDER
            .comment("Enable debug mode for additional logging.")
            .define("debugMode", false);

    private static final ModConfigSpec.EnumValue<ResetEnum> RESISTANCE_RESET = BUILDER
            .comment("Default reset behavior for Resistance when a mob is killed by the player.",
                    "FULL resets to minimum, PARTIAL reduces by the increase value, NONE keeps current value.",
                    "Can be overridden per-mob via datapacks (data/<namespace>/deus_mobs/<name>.json)")
            .defineEnum("resistanceReset", FULL);

    private static final ModConfigSpec.EnumValue<ResetEnum> ATTACK_BOOST_RESET = BUILDER
            .comment("Default reset behavior for Attack Boost when a mob is killed by the player.",
                    "FULL resets to minimum, PARTIAL reduces by the increase value, NONE keeps current value.",
                    "Can be overridden per-mob via datapacks (data/<namespace>/deus_mobs/<name>.json)")
            .defineEnum("attackBoostReset", FULL);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean showIcon;
    public static boolean debugMode;
    public static ResetEnum resistanceReset;
    public static ResetEnum attackBoostReset;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        debugMode = DEBUG_MODE.get();
        showIcon = SHOW_ICON.get();
        resistanceReset = RESISTANCE_RESET.get();
        attackBoostReset = ATTACK_BOOST_RESET.get();
    }

    /**
     * Checks if an entity type should be affected by Deus Ex Machina.
     * Returns true if the entity is defined in a datapack (data/namespace/deus_mobs/).
     */
    public static boolean isDeusExMob(EntityType<?> entityType) {
        return DeusExMobHandler.matches(entityType);
    }
}
