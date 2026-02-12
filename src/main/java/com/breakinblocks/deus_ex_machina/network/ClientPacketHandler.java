package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.client.DeathScreenData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-only packet handler to avoid classloading issues on dedicated server.
 */
public class ClientPacketHandler {

    public static void handleDeathBuff(DeathBuffPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            DeusExMachina.LOGGER.info("[ClientPacketHandler] Received DeathBuffPayload for {} with {} buff changes",
                    payload.killerType(), payload.buffChanges().size());
            DeathScreenData.set(payload.killerType(), payload.buffChanges());
            DeusExMachina.LOGGER.info("[ClientPacketHandler] DeathScreenData.hasData() = {}", DeathScreenData.hasData());
        });
    }
}
