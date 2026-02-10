package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages loading of per-mob/tag buff configurations from datapacks.
 * Loads JSON files from data/<namespace>/deus_mobs/
 */
public class DeusExMobConfigManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIRECTORY = "deus_mobs";

    private static final Map<String, DeusExMobConfig> MOB_CONFIGS = new HashMap<>();

    public DeusExMobConfigManager() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        MOB_CONFIGS.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();

            try {
                if (!element.isJsonObject()) {
                    DeusExMachina.LOGGER.error("Invalid deus_mobs config {}: expected JSON object", id);
                    continue;
                }

                JsonObject json = element.getAsJsonObject();
                if (!json.has("target")) {
                    DeusExMachina.LOGGER.error("Invalid deus_mobs config {}: missing 'target' field", id);
                    continue;
                }

                DeusExMobConfig config = DeusExMobConfig.fromJson(json);
                MOB_CONFIGS.put(config.target(), config);

                DeusExMachina.LOGGER.info("Loaded deus_mobs config for {}", config.target());

            } catch (Exception e) {
                DeusExMachina.LOGGER.error("Failed to load deus_mobs config {}: {}", id, e.getMessage());
            }
        }

        DeusExMachina.LOGGER.info("Loaded {} deus_mobs configurations", MOB_CONFIGS.size());

        // Update DeusExMobHandler with the list of targets
        List<String> targets = new ArrayList<>(MOB_CONFIGS.keySet());
        DeusExMobHandler.reload(targets);
    }

    /**
     * Get the configuration for a specific mob/tag key.
     * Returns null if no custom config exists for this key.
     */
    @Nullable
    public static DeusExMobConfig getConfig(String key) {
        return MOB_CONFIGS.get(key);
    }

    /**
     * Check if resistance buffs are enabled for a key.
     * Returns true if no config exists (use defaults) or if resistance is explicitly defined.
     */
    public static boolean isResistanceEnabled(String key) {
        DeusExMobConfig config = getConfig(key);
        // No config = use defaults (enabled)
        if (config == null) return true;
        // Config exists = only enabled if resistance is defined
        return config.resistance() != null;
    }

    /**
     * Check if attack buffs are enabled for a key.
     * Returns true if no config exists (use defaults) or if attack is explicitly defined.
     */
    public static boolean isAttackEnabled(String key) {
        DeusExMobConfig config = getConfig(key);
        // No config = use defaults (enabled)
        if (config == null) return true;
        // Config exists = only enabled if attack is defined
        return config.attack() != null;
    }

    // ===== RESISTANCE SETTINGS =====

    /**
     * Get resistance min for a key, falling back to default.
     */
    public static int getResistanceMin(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.resistance() != null) {
            return config.resistance().getMin(BuffSettings.DEFAULT_MIN);
        }
        return BuffSettings.DEFAULT_MIN;
    }

    /**
     * Get resistance max for a key, falling back to default.
     */
    public static int getResistanceMax(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.resistance() != null) {
            return config.resistance().getMax(BuffSettings.DEFAULT_MAX);
        }
        return BuffSettings.DEFAULT_MAX;
    }

    /**
     * Get resistance increase for a key, falling back to default.
     */
    public static int getResistanceIncrease(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.resistance() != null) {
            return config.resistance().getIncrease(BuffSettings.DEFAULT_INCREASE);
        }
        return BuffSettings.DEFAULT_INCREASE;
    }

    /**
     * Get resistance reset behavior for a key, falling back to global config.
     */
    public static ResetEnum getResistanceReset(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.resistance() != null) {
            return config.resistance().getReset(Config.resistanceReset);
        }
        return Config.resistanceReset;
    }

    // ===== ATTACK SETTINGS =====

    /**
     * Get attack min for a key, falling back to default.
     */
    public static int getAttackMin(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.attack() != null) {
            return config.attack().getMin(BuffSettings.DEFAULT_MIN);
        }
        return BuffSettings.DEFAULT_MIN;
    }

    /**
     * Get attack max for a key, falling back to default.
     */
    public static int getAttackMax(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.attack() != null) {
            return config.attack().getMax(BuffSettings.DEFAULT_MAX);
        }
        return BuffSettings.DEFAULT_MAX;
    }

    /**
     * Get attack increase for a key, falling back to default.
     */
    public static int getAttackIncrease(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.attack() != null) {
            return config.attack().getIncrease(BuffSettings.DEFAULT_INCREASE);
        }
        return BuffSettings.DEFAULT_INCREASE;
    }

    /**
     * Get attack reset behavior for a key, falling back to global config.
     */
    public static ResetEnum getAttackReset(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config != null && config.attack() != null) {
            return config.attack().getReset(Config.attackBoostReset);
        }
        return Config.attackBoostReset;
    }
}
