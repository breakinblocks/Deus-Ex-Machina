package com.breakinblocks.deus_ex_machina.client;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Client-side cache for displaying buff gains on the death screen.
 */
public class DeathScreenData {
    private static ResourceLocation killerType = null;
    private static Map<ResourceLocation, int[]> buffChanges = new HashMap<>();

    public static void set(ResourceLocation killer, Map<ResourceLocation, int[]> changes) {
        killerType = killer;
        buffChanges = new HashMap<>(changes);
    }

    public static void clear() {
        killerType = null;
        buffChanges.clear();
    }

    public static boolean hasData() {
        return killerType != null && !buffChanges.isEmpty();
    }

    public static ResourceLocation getKillerType() {
        return killerType;
    }

    /**
     * Get all buff changes.
     * @return Map of buff type ID to [gain, newValue]
     */
    public static Map<ResourceLocation, int[]> getBuffChanges() {
        return Collections.unmodifiableMap(buffChanges);
    }

    /**
     * Get the gain for a specific buff type.
     */
    public static int getGain(ResourceLocation buffTypeId) {
        int[] values = buffChanges.get(buffTypeId);
        return values != null ? values[0] : 0;
    }

    /**
     * Get the new value for a specific buff type.
     */
    public static int getNewValue(ResourceLocation buffTypeId) {
        int[] values = buffChanges.get(buffTypeId);
        return values != null ? values[1] : 0;
    }

    /**
     * Check if there are changes for a specific buff type.
     */
    public static boolean hasChanges(ResourceLocation buffTypeId) {
        return buffChanges.containsKey(buffTypeId);
    }
}
