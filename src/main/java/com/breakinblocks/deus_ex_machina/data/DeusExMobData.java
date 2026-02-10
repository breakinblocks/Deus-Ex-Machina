package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data attachment for mobs in instance mode.
 * Stores buff values per player - when this mob dies, the data dies with it.
 */
public class DeusExMobData implements INBTSerializable<CompoundTag> {
    private final Map<UUID, PlayerBuffData> playerBuffs = new HashMap<>();

    /**
     * Buff data for a specific player against this mob.
     */
    public record PlayerBuffData(int resistance, int strength) {
        public static final PlayerBuffData EMPTY = new PlayerBuffData(0, 0);

        public PlayerBuffData withResistance(int resistance) {
            return new PlayerBuffData(resistance, this.strength);
        }

        public PlayerBuffData withStrength(int strength) {
            return new PlayerBuffData(this.resistance, strength);
        }

        public CompoundTag toNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("resistance", resistance);
            tag.putInt("strength", strength);
            return tag;
        }

        public static PlayerBuffData fromNBT(CompoundTag tag) {
            return new PlayerBuffData(
                    tag.getInt("resistance"),
                    tag.getInt("strength")
            );
        }

        public boolean isEmpty() {
            return resistance <= 0 && strength <= 0;
        }
    }

    public PlayerBuffData getBuffData(UUID playerUUID) {
        return playerBuffs.getOrDefault(playerUUID, PlayerBuffData.EMPTY);
    }

    public int getResistance(UUID playerUUID) {
        return getBuffData(playerUUID).resistance();
    }

    public int getStrength(UUID playerUUID) {
        return getBuffData(playerUUID).strength();
    }

    public void setResistance(UUID playerUUID, int amount) {
        PlayerBuffData current = getBuffData(playerUUID);
        PlayerBuffData updated = current.withResistance(amount);
        if (updated.isEmpty()) {
            playerBuffs.remove(playerUUID);
        } else {
            playerBuffs.put(playerUUID, updated);
        }
    }

    public void setStrength(UUID playerUUID, int amount) {
        PlayerBuffData current = getBuffData(playerUUID);
        PlayerBuffData updated = current.withStrength(amount);
        if (updated.isEmpty()) {
            playerBuffs.remove(playerUUID);
        } else {
            playerBuffs.put(playerUUID, updated);
        }
    }

    public void addResistance(UUID playerUUID, int amount, int max) {
        int current = getResistance(playerUUID);
        setResistance(playerUUID, Math.min(max, current + amount));
    }

    public void addStrength(UUID playerUUID, int amount, int max) {
        int current = getStrength(playerUUID);
        setStrength(playerUUID, Math.min(max, current + amount));
    }

    public boolean hasPlayer(UUID playerUUID) {
        return playerBuffs.containsKey(playerUUID);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag playerList = new ListTag();

        for (Map.Entry<UUID, PlayerBuffData> entry : playerBuffs.entrySet()) {
            CompoundTag playerEntry = entry.getValue().toNBT();
            playerEntry.putUUID("uuid", entry.getKey());
            playerList.add(playerEntry);
        }

        nbt.put("players", playerList);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        playerBuffs.clear();

        ListTag playerList = nbt.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag entry = playerList.getCompound(i);
            if (entry.hasUUID("uuid")) {
                UUID uuid = entry.getUUID("uuid");
                PlayerBuffData data = PlayerBuffData.fromNBT(entry);
                if (!data.isEmpty()) {
                    playerBuffs.put(uuid, data);
                }
            }
        }
    }
}
