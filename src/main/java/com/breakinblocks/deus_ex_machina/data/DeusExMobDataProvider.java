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

/**
 * Forge capability provider for per-mob-instance buff tracking.
 * Attached to LivingEntity instances that use instance mode tracking.
 */
public class DeusExMobDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<DeusExMobData> DEUS_EX_MOB_DATA = CapabilityManager.get(new CapabilityToken<DeusExMobData>() {});

    private DeusExMobData mobData = null;
    private final LazyOptional<DeusExMobData> optional = LazyOptional.of(this::createMobData);

    private @NotNull DeusExMobData createMobData() {
        if (mobData == null) {
            mobData = new DeusExMobData();
        }
        return mobData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return DEUS_EX_MOB_DATA.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return createMobData().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createMobData().deserializeNBT(nbt);
    }
}
