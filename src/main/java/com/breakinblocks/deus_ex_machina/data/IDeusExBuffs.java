package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface IDeusExBuffs {
    boolean isEnabled();
    void setEnabled(boolean enabled);
    HashMap<ResourceLocation, Integer> getResistances();
    int getResistance(ResourceLocation mob);
    void setResistance(ResourceLocation mob, int resistance);
    void addResistance(ResourceLocation mob, int amount);

    HashMap<ResourceLocation, Integer> getStrengths();
    int getStrength(ResourceLocation mob);
    void setStrength(ResourceLocation mob, int strength);
    void addStrength(ResourceLocation mob, int amount);

    void saveNBTData(CompoundTag nbt);
    void loadNBTData(CompoundTag nbt);

    void copyFrom(@NotNull IDeusExBuffs oldStore);
}
