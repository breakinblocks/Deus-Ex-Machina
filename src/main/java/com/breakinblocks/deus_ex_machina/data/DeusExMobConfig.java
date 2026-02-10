package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.enums.TypeEnum;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration for a specific mob or tag loaded from datapacks.
 */
public record DeusExMobConfig(
        String target,
        TypeEnum type,
        @Nullable BuffSettings resistance,
        @Nullable BuffSettings attack
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

        BuffSettings resistance = json.has("resistance")
                ? BuffSettings.fromJson(json.getAsJsonObject("resistance"))
                : null;
        BuffSettings attack = json.has("attack")
                ? BuffSettings.fromJson(json.getAsJsonObject("attack"))
                : null;
        return new DeusExMobConfig(target, type, resistance, attack);
    }
}
