package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface IDeusExBuffs {
    boolean isEnabled();
    void setEnabled(boolean enabled);
    HashMap<String, Integer> getResistances();
    int getResistance(String mob);
    void setResistance(String mob, int resistance);
    void addResistance(String mob, int amount);

    HashMap<String, Integer> getStrengths();
    int getStrength(String mob);
    void setStrength(String mob, int strength);
    void addStrength(String mob, int amount);

    void saveNBTData(CompoundTag nbt);
    void loadNBTData(CompoundTag nbt);

    void copyFrom(@NotNull IDeusExBuffs oldStore);
}
