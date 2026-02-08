package com.breakinblocks.deus_ex_machina.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(
                DeathBuffPayload.TYPE,
                DeathBuffPayload.STREAM_CODEC,
                ClientPacketHandler::handleDeathBuff
        );
    }

    public static void sendToPlayer(ServerPlayer player, DeathBuffPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
