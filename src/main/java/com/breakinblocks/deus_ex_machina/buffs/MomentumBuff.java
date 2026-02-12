package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Momentum buff - damage increases with consecutive hits, resets when hit.
 * value=5 means each hit adds 5% damage, up to value*10 max stacks.
 * Taking damage resets the stacks.
 */
public class MomentumBuff extends AbstractBuff {

    public static final ResourceLocation ID = new ResourceLocation(DeusExMachina.MODID, "momentum");

    private static final Map<UUID, Integer> PLAYER_STACKS = new ConcurrentHashMap<>();
    private static final int MAX_STACKS = 10;

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    public BuffCategory getCategory() { return BuffCategory.MISC; }

    @Override
    public Component getDisplayName() { return Component.translatable("buff.deus_ex_machina.momentum"); }

    @Override
    public int getColor() { return 0xFFFFFF00; }

    @Override
    public BuffSettings getDefaultSettings() { return new BuffSettings(0, 10, 1, ResetEnum.FULL); }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        UUID playerId = context.player().getUUID();

        if (context.type() == BuffContext.ContextType.PLAYER_ATTACK) {
            int stacks = PLAYER_STACKS.getOrDefault(playerId, 0);
            float bonus = stacks * (value / 100f);
            PLAYER_STACKS.put(playerId, Math.min(stacks + 1, MAX_STACKS));
            return damage * (1 + bonus);
        } else if (context.type() == BuffContext.ContextType.PLAYER_HURT) {
            PLAYER_STACKS.remove(playerId);
        }

        return damage;
    }

    @Override
    public boolean appliesOnPlayerHurt() { return true; }

    @Override
    public boolean appliesOnPlayerAttack() { return true; }

    public static void clearStacks(UUID playerId) {
        PLAYER_STACKS.remove(playerId);
    }
}
