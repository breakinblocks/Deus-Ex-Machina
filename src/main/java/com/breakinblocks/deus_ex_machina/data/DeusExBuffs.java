package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DeusExBuffs implements IDeusExBuffs, INBTSerializable<CompoundTag> {
    private boolean enabled = false;
    private final Map<String, Map<ResourceLocation, Integer>> buffs = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Map<ResourceLocation, Integer> getBuffsForMob(String mobKey) {
        return buffs.getOrDefault(mobKey, Collections.emptyMap());
    }

    @Override
    public Map<String, Map<ResourceLocation, Integer>> getAllBuffs() {
        return Collections.unmodifiableMap(buffs);
    }

    @Override
    public int getBuff(String mobKey, ResourceLocation buffTypeId) {
        return getBuffsForMob(mobKey).getOrDefault(buffTypeId, 0);
    }

    @Override
    public void setBuff(String mobKey, ResourceLocation buffTypeId, int value) {
        if (value == 0) {
            // Remove if zero
            Map<ResourceLocation, Integer> mobBuffs = buffs.get(mobKey);
            if (mobBuffs != null) {
                mobBuffs.remove(buffTypeId);
                if (mobBuffs.isEmpty()) {
                    buffs.remove(mobKey);
                }
            }
        } else {
            buffs.computeIfAbsent(mobKey, k -> new HashMap<>()).put(buffTypeId, value);
        }
    }

    @Override
    public void addBuff(String mobKey, ResourceLocation buffTypeId, int amount) {
        int current = getBuff(mobKey, buffTypeId);
        setBuff(mobKey, buffTypeId, current + amount);
    }

    @Override
    public void saveNBTData(CompoundTag nbt) {
        nbt.putBoolean("enabled", enabled);

        ListTag mobList = new ListTag();
        for (Map.Entry<String, Map<ResourceLocation, Integer>> mobEntry : buffs.entrySet()) {
            CompoundTag mobTag = new CompoundTag();
            mobTag.putString("mob", mobEntry.getKey());

            ListTag buffList = new ListTag();
            for (Map.Entry<ResourceLocation, Integer> buffEntry : mobEntry.getValue().entrySet()) {
                CompoundTag buffTag = new CompoundTag();
                buffTag.putString("type", buffEntry.getKey().toString());
                buffTag.putInt("value", buffEntry.getValue());
                buffList.add(buffTag);
            }
            mobTag.put("buffs", buffList);
            mobList.add(mobTag);
        }
        nbt.put("mobs", mobList);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        this.enabled = nbt.getBoolean("enabled");
        this.buffs.clear();

        ListTag mobList = nbt.getList("mobs", Tag.TAG_COMPOUND);
        for (int i = 0; i < mobList.size(); i++) {
            CompoundTag mobTag = mobList.getCompound(i);
            String mobKey = mobTag.getString("mob");

            Map<ResourceLocation, Integer> mobBuffs = new HashMap<>();
            ListTag buffList = mobTag.getList("buffs", Tag.TAG_COMPOUND);
            for (int j = 0; j < buffList.size(); j++) {
                CompoundTag buffTag = buffList.getCompound(j);
                ResourceLocation typeId = ResourceLocation.tryParse(buffTag.getString("type"));
                int value = buffTag.getInt("value");
                if (typeId != null && value != 0) {
                    mobBuffs.put(typeId, value);
                }
            }

            if (!mobBuffs.isEmpty()) {
                buffs.put(mobKey, mobBuffs);
            }
        }
    }

    @Override
    public void copyFrom(@NotNull IDeusExBuffs source) {
        this.buffs.clear();
        this.enabled = source.isEnabled();

        for (Map.Entry<String, Map<ResourceLocation, Integer>> mobEntry : source.getAllBuffs().entrySet()) {
            this.buffs.put(mobEntry.getKey(), new HashMap<>(mobEntry.getValue()));
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        loadNBTData(nbt);
    }
}
