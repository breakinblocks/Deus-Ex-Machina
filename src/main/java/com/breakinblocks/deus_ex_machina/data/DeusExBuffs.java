package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DeusExBuffs implements IDeusExBuffs {
    private boolean enabled = false;
    private HashMap<ResourceLocation, Integer> resistances = new HashMap<>();
    private HashMap<ResourceLocation, Integer> strengths = new HashMap<>();


    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public HashMap<ResourceLocation, Integer> getResistances() {
        return resistances;
    }

    @Override
    public int getResistance(ResourceLocation mob) {
        return this.resistances.getOrDefault(mob, 0);
    }

    @Override
    public void setResistance(ResourceLocation mob, int amount) {
        int currentValue = this.resistances.getOrDefault(mob, 0);
        if (currentValue + amount > Config.maxResistance) {
            this.resistances.put(mob, Config.maxResistance);
            return;
        }
        if (currentValue + amount < Config.minResistance) {
            this.resistances.put(mob, Config.minResistance);
            return;
        }
        this.resistances.put(mob, amount);
    }

    @Override
    public void addResistance(ResourceLocation mob, int amount) {
        setResistance(mob, resistances.getOrDefault(mob, 0) + amount);
    }

    @Override
    public HashMap<ResourceLocation, Integer> getStrengths() {
        return strengths;
    }

    @Override
    public int getStrength(ResourceLocation mob) {
        return this.strengths.getOrDefault(mob, 0);
    }

    @Override
    public void setStrength(ResourceLocation mob, int amount) {
        int currentValue = this.strengths.getOrDefault(mob, 0);
        if (currentValue + amount > Config.maxAttackBoost) {
            this.strengths.put(mob, Config.maxAttackBoost);
            return;
        }
        if (currentValue + amount < Config.minAttackBoost) {
            this.strengths.put(mob, Config.minAttackBoost);
            return;
        }
        this.strengths.put(mob, amount);
    }

    @Override
    public void addStrength(ResourceLocation mob, int amount) {
        setStrength(mob, strengths.getOrDefault(mob, 0) + amount);
    }

    @Override
    public void saveNBTData(CompoundTag nbt) {
        CompoundTag resistancesTag = new CompoundTag();
        for (ResourceLocation mob : resistances.keySet()) {
            resistancesTag.putInt(mob.toString(), resistances.get(mob));
        }
        CompoundTag strengthsTag = new CompoundTag();
        for (ResourceLocation mob : strengths.keySet()) {
            strengthsTag.putInt(mob.toString(), strengths.get(mob));
        }
        nbt.putBoolean("enabled", enabled);
        nbt.put("resistances", resistancesTag);
        nbt.put("strengths", strengthsTag);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        CompoundTag resistancesTag = nbt.getCompound("resistances");
        for (String key : resistancesTag.getAllKeys()) {
            ResourceLocation mob = ResourceLocation.tryParse(key);
            int resistance = resistancesTag.getInt(key);
            this.resistances.put(mob, resistance);
        }
        CompoundTag strengthsTag = nbt.getCompound("strengths");
        for (String key : strengthsTag.getAllKeys()) {
            ResourceLocation mob = ResourceLocation.tryParse(key);
            int strength = strengthsTag.getInt(key);
            this.strengths.put(mob, strength);
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
