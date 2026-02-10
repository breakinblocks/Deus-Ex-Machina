package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

/**
 * Reusable buff settings for resistance or attack.
 * Values of -1 indicate "use default".
 */
public record BuffSettings(int min, int max, int increase, @Nullable ResetEnum reset) {
    public static final int DEFAULT_MIN = 0;
    public static final int DEFAULT_MAX = 80;
    public static final int DEFAULT_INCREASE = 1;

    public static BuffSettings fromJson(JsonObject json) {
        int min = json.has("min") ? json.get("min").getAsInt() : -1;
        int max = json.has("max") ? json.get("max").getAsInt() : -1;
        int increase = json.has("increase") ? json.get("increase").getAsInt() : -1;
        ResetEnum reset = null;
        if (json.has("reset")) {
            try {
                reset = ResetEnum.valueOf(json.get("reset").getAsString().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return new BuffSettings(min, max, increase, reset);
    }

    public int getMin(int fallback) {
        return min >= 0 ? min : fallback;
    }

    public int getMax(int fallback) {
        return max >= 0 ? max : fallback;
    }

    public int getIncrease(int fallback) {
        return increase >= 0 ? increase : fallback;
    }

    public ResetEnum getReset(ResetEnum fallback) {
        return reset != null ? reset : fallback;
    }
}
