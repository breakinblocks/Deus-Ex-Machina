package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-mob-instance buff tracking data.
 * Stores buff values per player - when this mob dies, the data dies with it.
 */
public class DeusExMobData {
    private final Map<UUID, Map<ResourceLocation, Integer>> playerBuffs = new HashMap<>();

    /**
     * Get all buffs for a specific player.
     * @param playerUUID The player's UUID
     * @return Map of buff type ID to buff value
     */
    public Map<ResourceLocation, Integer> getBuffsForPlayer(UUID playerUUID) {
        return playerBuffs.getOrDefault(playerUUID, Collections.emptyMap());
    }

    /**
     * Get a specific buff value for a player.
     * @param playerUUID The player's UUID
     * @param buffTypeId The buff type ID
     * @return The buff value, or 0 if not set
     */
    public int getBuff(UUID playerUUID, ResourceLocation buffTypeId) {
        return getBuffsForPlayer(playerUUID).getOrDefault(buffTypeId, 0);
    }

    /**
     * Set a specific buff value for a player.
     * @param playerUUID The player's UUID
     * @param buffTypeId The buff type ID
     * @param value The value to set
     */
    public void setBuff(UUID playerUUID, ResourceLocation buffTypeId, int value) {
        if (value == 0) {
            Map<ResourceLocation, Integer> buffs = playerBuffs.get(playerUUID);
            if (buffs != null) {
                buffs.remove(buffTypeId);
                if (buffs.isEmpty()) {
                    playerBuffs.remove(playerUUID);
                }
            }
        } else {
            playerBuffs.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(buffTypeId, value);
        }
    }

    /**
     * Add to a specific buff value for a player.
     * @param playerUUID The player's UUID
     * @param buffTypeId The buff type ID
     * @param amount The amount to add
     * @param max The maximum value
     */
    public void addBuff(UUID playerUUID, ResourceLocation buffTypeId, int amount, int max) {
        int current = getBuff(playerUUID, buffTypeId);
        setBuff(playerUUID, buffTypeId, Math.min(max, current + amount));
    }

    public boolean hasPlayer(UUID playerUUID) {
        return playerBuffs.containsKey(playerUUID);
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag playerList = new ListTag();

        for (Map.Entry<UUID, Map<ResourceLocation, Integer>> playerEntry : playerBuffs.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("uuid", playerEntry.getKey());

            ListTag buffList = new ListTag();
            for (Map.Entry<ResourceLocation, Integer> buffEntry : playerEntry.getValue().entrySet()) {
                CompoundTag buffTag = new CompoundTag();
                buffTag.putString("type", buffEntry.getKey().toString());
                buffTag.putInt("value", buffEntry.getValue());
                buffList.add(buffTag);
            }
            playerTag.put("buffs", buffList);
            playerList.add(playerTag);
        }

        nbt.put("players", playerList);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        playerBuffs.clear();

        ListTag playerList = nbt.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag playerTag = playerList.getCompound(i);
            if (!playerTag.hasUUID("uuid")) continue;

            UUID uuid = playerTag.getUUID("uuid");
            Map<ResourceLocation, Integer> buffs = new HashMap<>();

            ListTag buffList = playerTag.getList("buffs", Tag.TAG_COMPOUND);
            for (int j = 0; j < buffList.size(); j++) {
                CompoundTag buffTag = buffList.getCompound(j);
                ResourceLocation typeId = ResourceLocation.tryParse(buffTag.getString("type"));
                int value = buffTag.getInt("value");
                if (typeId != null && value != 0) {
                    buffs.put(typeId, value);
                }
            }

            if (!buffs.isEmpty()) {
                playerBuffs.put(uuid, buffs);
            }
        }
    }
}
