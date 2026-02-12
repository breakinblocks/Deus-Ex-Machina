package com.breakinblocks.deus_ex_machina.api.buff;

/**
 * Categories for buff types, used for organization and display.
 */
public enum BuffCategory {
    /**
     * Buffs that reduce incoming damage.
     */
    RESISTANCE,

    /**
     * Buffs that increase outgoing damage.
     */
    DAMAGE,

    /**
     * Buffs that affect movement speed.
     */
    MOVEMENT,

    /**
     * Buffs that affect health or healing.
     */
    HEALTH,

    /**
     * Miscellaneous buffs that don't fit other categories.
     */
    MISC
}
