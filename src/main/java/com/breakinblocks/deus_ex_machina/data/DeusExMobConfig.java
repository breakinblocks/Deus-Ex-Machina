package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.enums.TypeEnum;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for a specific mob or tag loaded from datapacks.
 */
public record DeusExMobConfig(
        String target,
        TypeEnum type,
        Map<ResourceLocation, BuffSettings> buffSettings
) {
    public static DeusExMobConfig fromJson(JsonObject json) {
        String target = json.get("target").getAsString();

        TypeEnum type = TypeEnum.ENTITY_TYPE; // Default to entity_type
        if (json.has("type")) {
            String typeStr = json.get("type").getAsString().toUpperCase();
            try {
                type = TypeEnum.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                // Invalid type, use default
            }
        }

        Map<ResourceLocation, BuffSettings> buffSettings = new HashMap<>();

        // Parse "buffs" map
        if (json.has("buffs") && json.get("buffs").isJsonObject()) {
            JsonObject buffsObj = json.getAsJsonObject("buffs");
            for (Map.Entry<String, JsonElement> entry : buffsObj.entrySet()) {
                ResourceLocation buffId = ResourceLocation.tryParse(entry.getKey());
                if (buffId != null && entry.getValue().isJsonObject()) {
                    buffSettings.put(buffId, BuffSettings.fromJson(entry.getValue().getAsJsonObject()));
                }
            }
        }

        return new DeusExMobConfig(target, type, buffSettings);
    }

    /**
     * Get settings for a specific buff type.
     */
    @Nullable
    public BuffSettings getBuffSettings(ResourceLocation buffTypeId) {
        return buffSettings.get(buffTypeId);
    }

    /**
     * Check if a specific buff type is enabled for this config.
     */
    public boolean isBuffEnabled(ResourceLocation buffTypeId) {
        return buffSettings.containsKey(buffTypeId);
    }
}
