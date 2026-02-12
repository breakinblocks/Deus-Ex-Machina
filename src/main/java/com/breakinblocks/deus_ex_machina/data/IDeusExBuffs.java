package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IDeusExBuffs {
    boolean isEnabled();
    void setEnabled(boolean enabled);

    /**
     * Get all buffs for a specific mob key.
     * @param mobKey The mob identifier (e.g., "minecraft:zombie" or "#minecraft:undead")
     * @return Map of buff type ID to buff value
     */
    Map<ResourceLocation, Integer> getBuffsForMob(String mobKey);

    /**
     * Get all stored buff data.
     * @return Map of mob key to (buff type ID to value)
     */
    Map<String, Map<ResourceLocation, Integer>> getAllBuffs();

    /**
     * Get a specific buff value.
     * @param mobKey The mob identifier
     * @param buffTypeId The buff type ID
     * @return The buff value, or 0 if not set
     */
    int getBuff(String mobKey, ResourceLocation buffTypeId);

    /**
     * Set a specific buff value.
     * @param mobKey The mob identifier
     * @param buffTypeId The buff type ID
     * @param value The value to set
     */
    void setBuff(String mobKey, ResourceLocation buffTypeId, int value);

    /**
     * Add to a specific buff value.
     * @param mobKey The mob identifier
     * @param buffTypeId The buff type ID
     * @param amount The amount to add (can be negative)
     */
    void addBuff(String mobKey, ResourceLocation buffTypeId, int amount);

    void saveNBTData(CompoundTag nbt);
    void loadNBTData(CompoundTag nbt);

    void copyFrom(@NotNull IDeusExBuffs oldStore);
}
