package com.breakinblocks.deus_ex_machina.network;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.client.DeathScreenData;
import net.minecraft.resources.ResourceLocation;

/**
 * Client-only packet handler to avoid classloading issues on dedicated server.
 */
public class ClientPacketHandler {
    public static void handle(ResourceLocation killerType, int resistanceGain, int attackBoostGain,
                              int newResistance, int newAttackBoost) {
        DeusExMachina.LOGGER.info("[ClientPacketHandler] Setting DeathScreenData for {}", killerType);
        DeathScreenData.set(killerType, resistanceGain, attackBoostGain, newResistance, newAttackBoost);
        DeusExMachina.LOGGER.info("[ClientPacketHandler] DeathScreenData.hasData() = {}", DeathScreenData.hasData());
    }
}
