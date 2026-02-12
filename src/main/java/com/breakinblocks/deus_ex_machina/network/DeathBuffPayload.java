package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Network payload sent to client when player dies to a Deus Ex mob.
 * Contains all buff changes that occurred.
 */
public record DeathBuffPayload(
        ResourceLocation killerType,
        Map<ResourceLocation, int[]> buffChanges // buffTypeId -> [gain, newValue]
) implements CustomPacketPayload {

    public static final Type<DeathBuffPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "death_buff"));

    public static final StreamCodec<ByteBuf, DeathBuffPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DeathBuffPayload decode(ByteBuf buf) {
            ResourceLocation killerType = ResourceLocation.STREAM_CODEC.decode(buf);
            int count = buf.readInt();
            Map<ResourceLocation, int[]> buffChanges = new HashMap<>();
            for (int i = 0; i < count; i++) {
                ResourceLocation buffId = ResourceLocation.STREAM_CODEC.decode(buf);
                int gain = buf.readInt();
                int newValue = buf.readInt();
                buffChanges.put(buffId, new int[]{gain, newValue});
            }
            return new DeathBuffPayload(killerType, buffChanges);
        }

        @Override
        public void encode(ByteBuf buf, DeathBuffPayload payload) {
            ResourceLocation.STREAM_CODEC.encode(buf, payload.killerType());
            buf.writeInt(payload.buffChanges().size());
            for (Map.Entry<ResourceLocation, int[]> entry : payload.buffChanges().entrySet()) {
                ResourceLocation.STREAM_CODEC.encode(buf, entry.getKey());
                buf.writeInt(entry.getValue()[0]); // gain
                buf.writeInt(entry.getValue()[1]); // newValue
            }
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Get the gain for a specific buff type.
     */
    public int getGain(ResourceLocation buffTypeId) {
        int[] values = buffChanges.get(buffTypeId);
        return values != null ? values[0] : 0;
    }

    /**
     * Get the new value for a specific buff type.
     */
    public int getNewValue(ResourceLocation buffTypeId) {
        int[] values = buffChanges.get(buffTypeId);
        return values != null ? values[1] : 0;
    }

    /**
     * Check if this payload has changes for a specific buff type.
     */
    public boolean hasChanges(ResourceLocation buffTypeId) {
        return buffChanges.containsKey(buffTypeId);
    }
}
