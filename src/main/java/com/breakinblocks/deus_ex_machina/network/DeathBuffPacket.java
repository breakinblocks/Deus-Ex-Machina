package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DeathBuffPacket {
    private final ResourceLocation killerType;
    private final boolean resistanceEnabled;
    private final boolean attackEnabled;
    private final int resistanceGain;
    private final int attackBoostGain;
    private final int newResistance;
    private final int newAttackBoost;

    public DeathBuffPacket(ResourceLocation killerType, boolean resistanceEnabled, boolean attackEnabled,
                           int resistanceGain, int attackBoostGain, int newResistance, int newAttackBoost) {
        this.killerType = killerType;
        this.resistanceEnabled = resistanceEnabled;
        this.attackEnabled = attackEnabled;
        this.resistanceGain = resistanceGain;
        this.attackBoostGain = attackBoostGain;
        this.newResistance = newResistance;
        this.newAttackBoost = newAttackBoost;
    }

    public static void encode(DeathBuffPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.killerType);
        buf.writeBoolean(packet.resistanceEnabled);
        buf.writeBoolean(packet.attackEnabled);
        buf.writeInt(packet.resistanceGain);
        buf.writeInt(packet.attackBoostGain);
        buf.writeInt(packet.newResistance);
        buf.writeInt(packet.newAttackBoost);
    }

    public static DeathBuffPacket decode(FriendlyByteBuf buf) {
        return new DeathBuffPacket(
                buf.readResourceLocation(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(DeathBuffPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DeusExMachina.LOGGER.info("[DeathBuffPacket] Received: {} res={} +{}%, atk={} +{}%",
                    packet.killerType, packet.resistanceEnabled, packet.resistanceGain,
                    packet.attackEnabled, packet.attackBoostGain);
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.handle(packet.killerType, packet.resistanceEnabled, packet.attackEnabled,
                            packet.resistanceGain, packet.attackBoostGain, packet.newResistance, packet.newAttackBoost)
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
