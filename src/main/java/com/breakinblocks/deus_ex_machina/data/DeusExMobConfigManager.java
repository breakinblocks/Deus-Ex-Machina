package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import com.breakinblocks.deus_ex_machina.enums.TypeEnum;
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
 * Loads JSON files from data/&lt;namespace&gt;/deus_mobs/
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
     * Get the tracking type for a key.
     */
    public static TypeEnum getType(String key) {
        DeusExMobConfig config = getConfig(key);
        if (config == null) return TypeEnum.ENTITY_TYPE;
        return config.type();
    }

    /**
     * Check if a key uses instance mode.
     */
    public static boolean isInstanceMode(String key) {
        return getType(key) == TypeEnum.INSTANCE;
    }

    /**
     * Check if a specific buff type is enabled for a key.
     * Returns true if config has settings for this buff type.
     */
    public static boolean isBuffEnabled(String key, ResourceLocation buffTypeId) {
        DeusExMobConfig config = getConfig(key);
        if (config == null) return false;
        return config.isBuffEnabled(buffTypeId);
    }

    /**
     * Get settings for a specific buff type, or null if not configured.
     */
    @Nullable
    public static BuffSettings getBuffSettings(String key, ResourceLocation buffTypeId) {
        DeusExMobConfig config = getConfig(key);
        if (config == null) return null;
        return config.getBuffSettings(buffTypeId);
    }

    /**
     * Get the min value for a buff type, falling back to the buff type's default.
     */
    public static int getBuffMin(String key, BuffType buffType) {
        BuffSettings settings = getBuffSettings(key, buffType.getId());
        if (settings != null) {
            return settings.getMin(buffType.getDefaultSettings().min());
        }
        return buffType.getDefaultSettings().min();
    }

    /**
     * Get the max value for a buff type, falling back to the buff type's default.
     */
    public static int getBuffMax(String key, BuffType buffType) {
        BuffSettings settings = getBuffSettings(key, buffType.getId());
        if (settings != null) {
            return settings.getMax(buffType.getDefaultSettings().max());
        }
        return buffType.getDefaultSettings().max();
    }

    /**
     * Get the increase value for a buff type, falling back to the buff type's default.
     */
    public static int getBuffIncrease(String key, BuffType buffType) {
        BuffSettings settings = getBuffSettings(key, buffType.getId());
        if (settings != null) {
            return settings.getIncrease(buffType.getDefaultSettings().increase());
        }
        return buffType.getDefaultSettings().increase();
    }

    /**
     * Get the reset behavior for a buff type, falling back to the buff type's default.
     */
    public static ResetEnum getBuffReset(String key, BuffType buffType) {
        BuffSettings settings = getBuffSettings(key, buffType.getId());
        ResetEnum defaultReset = buffType.getDefaultSettings().reset();
        if (defaultReset == null) {
            defaultReset = ResetEnum.FULL;
        }
        if (settings != null) {
            return settings.getReset(defaultReset);
        }
        return defaultReset;
    }
}
