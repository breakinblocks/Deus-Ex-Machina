package com.breakinblocks.deus_ex_machina;

import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

import static com.breakinblocks.deus_ex_machina.DeusMode.WHITELIST;

@EventBusSubscriber(modid = DeusExMachina.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<List<? extends String>> DEUS_EX_MOBS = BUILDER
            .comment("A List of mobs that are considered 'Deus Ex Machina' mobs. Supports exact matches (minecraft:zombie) or regex patterns wrapped in slashes (/minecraft:.*/)")
            .defineListAllowEmpty("deusExMobs", List.of("minecraft:ender_dragon", "minecraft:wither", "minecraft:warden"), Config::validateMobPattern);

    private static final ModConfigSpec.EnumValue<DeusMode> DEUS_MODE = BUILDER
            .defineEnum("deusMode", WHITELIST);

    private static final ModConfigSpec.BooleanValue SHOW_ICON = BUILDER
            .comment("Show Deus Ex Machina icon on the player's HUD when the effect is active.")
            .define("showIcon", true);

    private static final ModConfigSpec.BooleanValue DEBUG_MODE = BUILDER
            .comment("Enable debug mode for additional logging.")
            .define("debugMode", false);

    private static final ModConfigSpec.IntValue BASE_RESISTANCE = BUILDER.push("Resistance")
            .comment("Resistance Settings when Deus Ex Machina is active")
            .defineInRange("minResistance", 0, 0, 1000);

    private static final ModConfigSpec.IntValue MAX_RESISTANCE = BUILDER
            .defineInRange("maxResistance", 80, 0, 1000);

    private static final ModConfigSpec.IntValue RESISTANCE_INCREASE = BUILDER
            .defineInRange("resistanceIncrease", 1, 0, 1000);

    private static final ModConfigSpec.BooleanValue RESISTANCE_RESET = BUILDER
            .comment("Reset Resistance for a mob when it's killed by the player. if false, Resistance will permanently increase until cap is reached.")
            .define("resistanceReset", true);

    private static final ModConfigSpec.IntValue BASE_ATTACK_BOOST = BUILDER.pop().push("Attack-Boost")
            .comment("Attack Boost Settings when Deus Ex Machina is active")
            .defineInRange("minAttackBoost", 0, 0, 1000);

    private static final ModConfigSpec.IntValue MAX_ATTACK_BOOST = BUILDER
            .defineInRange("maxAttackBoost", 80, 0, 1000);

    private static final ModConfigSpec.IntValue ATTACK_BOOST_INCREASE = BUILDER
            .defineInRange("attackBoostIncrease", 1, 0, 1000);

    private static final ModConfigSpec.BooleanValue ATTACK_BOOST_RESET = BUILDER
            .comment("Reset Attack Boost for a mob when it's killed by the player. if false, Attack Boost will permanently increase until cap is reached.")
            .define("attackBoostReset", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static DeusMode deusMode;
    public static boolean showIcon;
    public static boolean debugMode;
    public static int minResistance;
    public static int maxResistance;
    public static int resistanceIncrease;
    public static boolean resistanceReset;
    public static int minAttackBoost;
    public static int maxAttackBoost;
    public static int attackBoostIncrease;
    public static boolean attackBoostReset;

    private static boolean validateMobPattern(final Object obj) {
        if (!(obj instanceof String pattern)) return false;
        // Regex patterns wrapped in slashes - just check it's not empty
        if (pattern.startsWith("/") && pattern.endsWith("/")) {
            return pattern.length() > 2;
        }
        ResourceLocation loc = ResourceLocation.tryParse(pattern);
        return loc != null && BuiltInRegistries.ENTITY_TYPE.containsKey(loc);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        DeusExMobHandler.reload(DEUS_EX_MOBS.get());
        deusMode = DEUS_MODE.get();
        debugMode = DEBUG_MODE.get();
        showIcon = SHOW_ICON.get();

        minResistance = BASE_RESISTANCE.get();
        resistanceIncrease = RESISTANCE_INCREASE.get();
        maxResistance = MAX_RESISTANCE.get();
        resistanceReset = RESISTANCE_RESET.get();

        minAttackBoost = BASE_ATTACK_BOOST.get();
        attackBoostIncrease = ATTACK_BOOST_INCREASE.get();
        maxAttackBoost = MAX_ATTACK_BOOST.get();
        attackBoostReset = ATTACK_BOOST_RESET.get();
    }

    /**
     * Checks if an entity type should be affected by Deus Ex Machina.
     * Respects the current mode (WHITELIST/BLACKLIST).
     */
    public static boolean isDeusExMob(EntityType<?> entityType) {
        boolean inList = DeusExMobHandler.matches(entityType);
        return switch (deusMode) {
            case WHITELIST -> inList;
            case BLACKLIST -> !inList;
        };
    }
}
