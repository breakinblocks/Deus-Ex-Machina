package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeusExBuffsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<IDeusExBuffs> DEUS_EX_BUFFS = CapabilityManager.get(new CapabilityToken<IDeusExBuffs>() {});

    private DeusExBuffs deusExBuffs = new DeusExBuffs();
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return DEUS_EX_BUFFS.orEmpty(cap, LazyOptional.of(this::createDeusExBuffs));
    }

    private @NotNull IDeusExBuffs createDeusExBuffs() {
        if (deusExBuffs == null) {
            deusExBuffs = new DeusExBuffs();
        }
        return deusExBuffs;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (deusExBuffs != null) {
            deusExBuffs.saveNBTData(nbt);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createDeusExBuffs().loadNBTData(nbt);
    }
}
