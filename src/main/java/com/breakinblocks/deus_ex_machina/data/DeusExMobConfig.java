package com.breakinblocks.deus_ex_machina.data;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration for a specific mob or tag loaded from datapacks.
 */
public record DeusExMobConfig(
        String target,
        @Nullable BuffSettings resistance,
        @Nullable BuffSettings attack
) {
    public static DeusExMobConfig fromJson(JsonObject json) {
        String target = json.get("target").getAsString();
        BuffSettings resistance = json.has("resistance")
                ? BuffSettings.fromJson(json.getAsJsonObject("resistance"))
                : null;
        BuffSettings attack = json.has("attack")
                ? BuffSettings.fromJson(json.getAsJsonObject("attack"))
                : null;
        return new DeusExMobConfig(target, resistance, attack);
    }
}
