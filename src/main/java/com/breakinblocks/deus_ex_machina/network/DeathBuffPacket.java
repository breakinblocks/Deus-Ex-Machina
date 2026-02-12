package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Network packet sent to client when player dies to a Deus Ex mob.
 * Contains all buff changes that occurred.
 */
public class DeathBuffPacket {
    private final ResourceLocation killerType;
    private final Map<ResourceLocation, int[]> buffChanges; // buffTypeId -> [gain, newValue]

    public DeathBuffPacket(ResourceLocation killerType, Map<ResourceLocation, int[]> buffChanges) {
        this.killerType = killerType;
        this.buffChanges = buffChanges;
    }

    public static void encode(DeathBuffPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.killerType);
        buf.writeInt(packet.buffChanges.size());
        for (Map.Entry<ResourceLocation, int[]> entry : packet.buffChanges.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeInt(entry.getValue()[0]); // gain
            buf.writeInt(entry.getValue()[1]); // newValue
        }
    }

    public static DeathBuffPacket decode(FriendlyByteBuf buf) {
        ResourceLocation killerType = buf.readResourceLocation();
        int count = buf.readInt();
        Map<ResourceLocation, int[]> buffChanges = new HashMap<>();
        for (int i = 0; i < count; i++) {
            ResourceLocation buffId = buf.readResourceLocation();
            int gain = buf.readInt();
            int newValue = buf.readInt();
            buffChanges.put(buffId, new int[]{gain, newValue});
        }
        return new DeathBuffPacket(killerType, buffChanges);
    }

    public static void handle(DeathBuffPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DeusExMachina.LOGGER.info("[DeathBuffPacket] Received for {} with {} buff changes",
                    packet.killerType, packet.buffChanges.size());
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.handle(packet.killerType, packet.buffChanges)
            );
        });
        ctx.get().setPacketHandled(true);
    }

    public ResourceLocation getKillerType() {
        return killerType;
    }

    public Map<ResourceLocation, int[]> getBuffChanges() {
        return buffChanges;
    }
}
