package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.buffs.*;

import java.util.function.Supplier;

/**
 * Built-in buff types provided by Deus Ex Machina.
 */
public class BuiltinBuffs {

    // ===== DEFENSIVE =====

    /**
     * Resistance buff - reduces damage taken from mobs.
     */
    public static final Supplier<BuffType> RESISTANCE = BuffRegistry.BUFF_TYPES.register("resistance",
            ResistanceBuff::new
    );

    /**
     * Evasion buff - chance to completely dodge an attack.
     */
    public static final Supplier<BuffType> EVASION = BuffRegistry.BUFF_TYPES.register("evasion",
            EvasionBuff::new
    );

    /**
     * Thorns buff - reflect damage back to attacker.
     */
    public static final Supplier<BuffType> THORNS = BuffRegistry.BUFF_TYPES.register("thorns",
            ThornsBuff::new
    );

    /**
     * Knockback resistance buff - reduces knockback taken.
     */
    public static final Supplier<BuffType> KNOCKBACK_RESISTANCE = BuffRegistry.BUFF_TYPES.register("knockback_resistance",
            KnockbackResistanceBuff::new
    );

    // ===== OFFENSIVE =====

    /**
     * Attack boost buff - increases damage dealt to mobs.
     */
    public static final Supplier<BuffType> ATTACK_BOOST = BuffRegistry.BUFF_TYPES.register("attack_boost",
            DamageBoostBuff::new
    );

    /**
     * Life leech buff - heals player for a percentage of damage dealt.
     */
    public static final Supplier<BuffType> LIFE_LEECH = BuffRegistry.BUFF_TYPES.register("life_leech",
            LifeLeechBuff::new
    );

    /**
     * Critical chance buff - chance for bonus critical damage.
     */
    public static final Supplier<BuffType> CRITICAL_CHANCE = BuffRegistry.BUFF_TYPES.register("critical_chance",
            CriticalChanceBuff::new
    );

    /**
     * Execute buff - bonus damage to low-health targets.
     */
    public static final Supplier<BuffType> EXECUTE = BuffRegistry.BUFF_TYPES.register("execute",
            ExecuteBuff::new
    );

    /**
     * Knockback buff - increased knockback on hit.
     */
    public static final Supplier<BuffType> KNOCKBACK = BuffRegistry.BUFF_TYPES.register("knockback",
            KnockbackBuff::new
    );

    // ===== HYBRID =====

    /**
     * Berserker buff - deal and take more damage.
     */
    public static final Supplier<BuffType> BERSERKER = BuffRegistry.BUFF_TYPES.register("berserker",
            BerserkerBuff::new
    );

    /**
     * Momentum buff - damage increases with consecutive hits, resets when hit.
     */
    public static final Supplier<BuffType> MOMENTUM = BuffRegistry.BUFF_TYPES.register("momentum",
            MomentumBuff::new
    );

    /**
     * Call this to ensure the class is loaded and buffs are registered.
     */
    public static void init() {
        // Static initialization is enough
    }
}
