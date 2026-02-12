package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.client.DeathScreenData;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Client-only packet handler to avoid classloading issues on dedicated server.
 */
public class ClientPacketHandler {
    public static void handle(ResourceLocation killerType, Map<ResourceLocation, int[]> buffChanges) {
        DeusExMachina.LOGGER.info("[ClientPacketHandler] Received DeathBuffPayload for {} with {} buff changes",
                killerType, buffChanges.size());
        DeathScreenData.set(killerType, buffChanges);
        DeusExMachina.LOGGER.info("[ClientPacketHandler] DeathScreenData.hasData() = {}", DeathScreenData.hasData());
    }
}
