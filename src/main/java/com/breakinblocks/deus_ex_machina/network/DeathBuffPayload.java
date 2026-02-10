package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DeathBuffPayload(
        ResourceLocation killerType,
        boolean resistanceEnabled,
        boolean attackEnabled,
        int resistanceGain,
        int attackBoostGain,
        int newResistance,
        int newAttackBoost
) implements CustomPacketPayload {

    public static final Type<DeathBuffPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "death_buff"));

    public static final StreamCodec<ByteBuf, DeathBuffPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DeathBuffPayload decode(ByteBuf buf) {
            ResourceLocation killerType = ResourceLocation.STREAM_CODEC.decode(buf);
            boolean resistanceEnabled = buf.readBoolean();
            boolean attackEnabled = buf.readBoolean();
            int resistanceGain = buf.readInt();
            int attackBoostGain = buf.readInt();
            int newResistance = buf.readInt();
            int newAttackBoost = buf.readInt();
            return new DeathBuffPayload(killerType, resistanceEnabled, attackEnabled,
                    resistanceGain, attackBoostGain, newResistance, newAttackBoost);
        }

        @Override
        public void encode(ByteBuf buf, DeathBuffPayload payload) {
            ResourceLocation.STREAM_CODEC.encode(buf, payload.killerType());
            buf.writeBoolean(payload.resistanceEnabled());
            buf.writeBoolean(payload.attackEnabled());
            buf.writeInt(payload.resistanceGain());
            buf.writeInt(payload.attackBoostGain());
            buf.writeInt(payload.newResistance());
            buf.writeInt(payload.newAttackBoost());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
