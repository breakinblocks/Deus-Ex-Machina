package com.breakinblocks.deus_ex_machina.enums;

/**
 * Determines how buff progression is tracked for a mob configuration.
 */
public enum TypeEnum {
    /**
     * Default: All mobs of this type share buff progression.
     * When any zombie kills the player, the zombie resistance buff increases.
     */
    ENTITY_TYPE,

    /**
     * Each individual mob tracks its own kill history.
     * Only deaths to this specific mob instance increase that mob's buff.
     */
    INSTANCE
}
