package com.breakinblocks.deus_ex_machina;

import com.breakinblocks.deus_ex_machina.enums.DeusModeEnum;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static com.breakinblocks.deus_ex_machina.enums.DeusModeEnum.*;
import static com.breakinblocks.deus_ex_machina.enums.ResetEnum.*;

@Mod.EventBusSubscriber(modid = DeusExMachina.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();



    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DEUS_EX_MOBS = BUILDER.comment("A List of mobs that are considered 'Deus Ex Machina' mobs. Supports exact matches (minecraft:zombie) or regex patterns wrapped in slashes (/minecraft:.*/)").defineListAllowEmpty("deusExMobs", List.of("minecraft:ender_dragon", "minecraft:wither", "minecraft:warden"), Config::validateMobPattern);
    private static final ForgeConfigSpec.EnumValue<DeusModeEnum> DEUS_MODE = BUILDER.defineEnum("deusMode", WHITELIST);
    private static final ForgeConfigSpec.BooleanValue SHOW_ICON = BUILDER.comment("Show Deus Ex Machina icon on the player's HUD when the effect is active.").define("showIcon", true);
    private static final ForgeConfigSpec.BooleanValue DEBUG_MODE = BUILDER.comment("Enable debug mode for additional logging.").define("debugMode", false);

    private static final ForgeConfigSpec.IntValue BASE_RESISTANCE = BUILDER.push("Resistance")
            .comment("Resistance Settings when Deus Ex Machina is active")
            .defineInRange("minResistance", 0, 0, 1000);

    private static final ForgeConfigSpec.IntValue MAX_RESISTANCE = BUILDER
            .defineInRange("maxResistance", 80, 0, 1000);

    private static final ForgeConfigSpec.IntValue RESISTANCE_INCREASE = BUILDER
            .defineInRange("resistanceIncrease", 1, 0, 1000);

    private static final ForgeConfigSpec.EnumValue<ResetEnum> RESISTANCE_RESET = BUILDER
            .comment("Reset Resistance for a mob when it's killed by the player. FULL resets it to minimum, PARTIAL reduces it by above value.")
            .defineEnum("resistanceReset", FULL);

    private static final ForgeConfigSpec.IntValue BASE_ATTACK_BOOST = BUILDER.pop().push("Attack-Boost")
            .comment("Attack Boost Settings when Deus Ex Machina is active")
            .defineInRange("minAttackBoost", 0, 0, 1000);

    private static final ForgeConfigSpec.IntValue MAX_ATTACK_BOOST = BUILDER
            .defineInRange("maxAttackBoost", 80, 0, 1000);

    private static final ForgeConfigSpec.IntValue ATTACK_BOOST_INCREASE = BUILDER
            .defineInRange("attackBoostIncrease", 1, 0, 1000);

    private static final ForgeConfigSpec.EnumValue<ResetEnum> ATTACK_BOOST_RESET = BUILDER
            .comment("Reset Attack Boost for a mob when it's killed by the player. FULL resets it to minimum, PARTIAL reduces it by above value.")
            .defineEnum("attackBoostReset", FULL);

    static final ForgeConfigSpec SPEC = BUILDER.build();


    public static DeusModeEnum deusModeEnum;
    public static boolean showIcon;
    public static boolean debugMode;
    public static int minResistance;
    public static int maxResistance;
    public static int resistanceIncrease;
    public static ResetEnum resistanceReset;
    public static int minAttackBoost;
    public static int maxAttackBoost;
    public static int attackBoostIncrease;
    public static ResetEnum attackBoostReset;

    private static boolean validateMobPattern(final Object obj) {
        if (!(obj instanceof String pattern)) return false;
        // Regex patterns wrapped in slashes - just check it's not empty
        if (pattern.startsWith("/") && pattern.endsWith("/")) {
            return pattern.length() > 2;
        }
        return ForgeRegistries.ENTITY_TYPES.containsKey(ResourceLocation.tryParse(pattern));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        DeusExMobHandler.reload(DEUS_EX_MOBS.get());
        deusModeEnum = DEUS_MODE.get();
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
        return switch (deusModeEnum) {
            case WHITELIST -> inList;
            case BLACKLIST -> !inList;
        };
    }
}
