package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

/**
 * Reusable buff settings for min/max/increase/reset values.
 * Used for both resistance and attack boost configurations.
 */
public record BuffSettings(int min, int max, int increase, @Nullable ResetEnum reset) {

    // Default values used when no datapack config exists
    public static final int DEFAULT_MIN = 0;
    public static final int DEFAULT_MAX = 80;
    public static final int DEFAULT_INCREASE = 1;

    public static BuffSettings fromJson(JsonObject json) {
        int min = json.has("min") ? json.get("min").getAsInt() : -1;
        int max = json.has("max") ? json.get("max").getAsInt() : -1;
        int increase = json.has("increase") ? json.get("increase").getAsInt() : -1;

        ResetEnum reset = null;
        if (json.has("reset")) {
            String resetStr = json.get("reset").getAsString().toUpperCase();
            try {
                reset = ResetEnum.valueOf(resetStr);
            } catch (IllegalArgumentException ignored) {
                // Invalid reset value, leave as null to use global default
            }
        }

        return new BuffSettings(min, max, increase, reset);
    }

    /**
     * Returns the min value, or the fallback if not specified (-1).
     */
    public int getMin(int fallback) {
        return min >= 0 ? min : fallback;
    }

    /**
     * Returns the max value, or the fallback if not specified (-1).
     */
    public int getMax(int fallback) {
        return max >= 0 ? max : fallback;
    }

    /**
     * Returns the increase value, or the fallback if not specified (-1).
     */
    public int getIncrease(int fallback) {
        return increase >= 0 ? increase : fallback;
    }

    /**
     * Returns the reset behavior, or the fallback if not specified.
     */
    public ResetEnum getReset(ResetEnum fallback) {
        return reset != null ? reset : fallback;
    }

    /**
     * Check if any value is specified.
     */
    public boolean hasAnyValue() {
        return min >= 0 || max >= 0 || increase >= 0 || reset != null;
    }
}
