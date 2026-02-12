package com.breakinblocks.deus_ex_machina.api.buff;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Context passed to buff applicators containing relevant event data.
 */
public record BuffContext(
        Player player,
        LivingEntity target,
        ContextType type,
        @Nullable LivingDamageEvent.Pre damageEvent
) {
    public enum ContextType {
        /**
         * Player is taking damage from the target.
         */
        PLAYER_HURT,

        /**
         * Player is dealing damage to the target.
         */
        PLAYER_ATTACK
    }

    public static BuffContext playerHurt(Player player, LivingEntity attacker, LivingDamageEvent.Pre event) {
        return new BuffContext(player, attacker, ContextType.PLAYER_HURT, event);
    }

    public static BuffContext playerAttack(Player player, LivingEntity target, LivingDamageEvent.Pre event) {
        return new BuffContext(player, target, ContextType.PLAYER_ATTACK, event);
    }
}
