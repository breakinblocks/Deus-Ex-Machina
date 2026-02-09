package com.breakinblocks.deus_ex_machina.data;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration for a specific mob or tag group.
 * Loaded from datapack JSON files in data/deus_ex_machina/deus_mobs/
 */
public record DeusExMobConfig(
        String target,
        @Nullable BuffSettings resistance,
        @Nullable BuffSettings attack
) {

    public static DeusExMobConfig fromJson(JsonObject json) {
        String target = json.get("target").getAsString();

        BuffSettings resistance = null;
        if (json.has("resistance") && json.get("resistance").isJsonObject()) {
            resistance = BuffSettings.fromJson(json.getAsJsonObject("resistance"));
        }

        BuffSettings attack = null;
        if (json.has("attack") && json.get("attack").isJsonObject()) {
            attack = BuffSettings.fromJson(json.getAsJsonObject("attack"));
        }

        return new DeusExMobConfig(target, resistance, attack);
    }

    /**
     * Check if this config has any resistance settings.
     */
    public boolean hasResistance() {
        return resistance != null && resistance.hasAnyValue();
    }

    /**
     * Check if this config has any attack settings.
     */
    public boolean hasAttack() {
        return attack != null && attack.hasAnyValue();
    }
}
