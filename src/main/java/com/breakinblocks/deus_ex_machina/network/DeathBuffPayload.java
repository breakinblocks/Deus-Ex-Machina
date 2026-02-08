package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DeathBuffPayload(
        ResourceLocation killerType,
        int resistanceGain,
        int attackBoostGain,
        int newResistance,
        int newAttackBoost
) implements CustomPacketPayload {

    public static final Type<DeathBuffPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "death_buff"));

    public static final StreamCodec<ByteBuf, DeathBuffPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, DeathBuffPayload::killerType,
                    ByteBufCodecs.INT, DeathBuffPayload::resistanceGain,
                    ByteBufCodecs.INT, DeathBuffPayload::attackBoostGain,
                    ByteBufCodecs.INT, DeathBuffPayload::newResistance,
                    ByteBufCodecs.INT, DeathBuffPayload::newAttackBoost,
                    DeathBuffPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
