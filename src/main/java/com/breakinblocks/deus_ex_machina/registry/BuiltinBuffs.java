package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.buffs.*;

/**
 * Built-in buff types provided by Deus Ex Machina.
 */
public class BuiltinBuffs {

    // ===== DEFENSIVE =====

    public static final BuffType RESISTANCE = BuffRegistry.register(new ResistanceBuff());
    public static final BuffType EVASION = BuffRegistry.register(new EvasionBuff());
    public static final BuffType THORNS = BuffRegistry.register(new ThornsBuff());
    public static final BuffType KNOCKBACK_RESISTANCE = BuffRegistry.register(new KnockbackResistanceBuff());

    // ===== OFFENSIVE =====

    public static final BuffType ATTACK_BOOST = BuffRegistry.register(new DamageBoostBuff());
    public static final BuffType LIFE_LEECH = BuffRegistry.register(new LifeLeechBuff());
    public static final BuffType CRITICAL_CHANCE = BuffRegistry.register(new CriticalChanceBuff());
    public static final BuffType EXECUTE = BuffRegistry.register(new ExecuteBuff());
    public static final BuffType KNOCKBACK = BuffRegistry.register(new KnockbackBuff());

    // ===== HYBRID =====

    public static final BuffType BERSERKER = BuffRegistry.register(new BerserkerBuff());
    public static final BuffType MOMENTUM = BuffRegistry.register(new MomentumBuff());

    /**
     * Call this to ensure the class is loaded and buffs are registered.
     */
    public static void init() {
        // Static initialization is enough
    }
}
