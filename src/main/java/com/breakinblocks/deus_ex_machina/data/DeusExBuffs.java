package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DeusExBuffs implements IDeusExBuffs {
    private boolean enabled = false;
    private HashMap<String, Integer> resistances = new HashMap<>();
    private HashMap<String, Integer> strengths = new HashMap<>();


    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public HashMap<String, Integer> getResistances() {
        return resistances;
    }

    @Override
    public int getResistance(String mob) {
        return this.resistances.getOrDefault(mob, 0);
    }

    @Override
    public void setResistance(String mob, int amount) {
        int max = DeusExMobConfigManager.getResistanceMax(mob);
        int min = DeusExMobConfigManager.getResistanceMin(mob);
        if (amount > max) {
            this.resistances.put(mob, max);
            return;
        }
        if (amount < min) {
            this.resistances.put(mob, min);
            return;
        }
        this.resistances.put(mob, amount);
    }

    @Override
    public void addResistance(String mob, int amount) {
        setResistance(mob, resistances.getOrDefault(mob, 0) + amount);
    }

    @Override
    public HashMap<String, Integer> getStrengths() {
        return strengths;
    }

    @Override
    public int getStrength(String mob) {
        return this.strengths.getOrDefault(mob, 0);
    }

    @Override
    public void setStrength(String mob, int amount) {
        int max = DeusExMobConfigManager.getAttackMax(mob);
        int min = DeusExMobConfigManager.getAttackMin(mob);
        if (amount > max) {
            this.strengths.put(mob, max);
            return;
        }
        if (amount < min) {
            this.strengths.put(mob, min);
            return;
        }
        this.strengths.put(mob, amount);
    }

    @Override
    public void addStrength(String mob, int amount) {
        setStrength(mob, strengths.getOrDefault(mob, 0) + amount);
    }

    @Override
    public void saveNBTData(CompoundTag nbt) {
        CompoundTag resistancesTag = new CompoundTag();
        for (String mob : resistances.keySet()) {
            resistancesTag.putInt(mob, resistances.get(mob));
        }
        CompoundTag strengthsTag = new CompoundTag();
        for (String mob : strengths.keySet()) {
            strengthsTag.putInt(mob, strengths.get(mob));
        }
        nbt.putBoolean("enabled", enabled);
        nbt.put("resistances", resistancesTag);
        nbt.put("strengths", strengthsTag);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        CompoundTag resistancesTag = nbt.getCompound("resistances");
        for (String key : resistancesTag.getAllKeys()) {
            int resistance = resistancesTag.getInt(key);
            this.resistances.put(key, resistance);
        }
        CompoundTag strengthsTag = nbt.getCompound("strengths");
        for (String key : strengthsTag.getAllKeys()) {
            int strength = strengthsTag.getInt(key);
            this.strengths.put(key, strength);
        }
        this.enabled = nbt.getBoolean("enabled");
    }

    @Override
    public void copyFrom(IDeusExBuffs source) {
        this.resistances.clear();
        this.strengths.clear();

        this.enabled = source.isEnabled();
        this.resistances.putAll(source.getResistances());
        this.strengths.putAll(source.getStrengths());
    }
}
