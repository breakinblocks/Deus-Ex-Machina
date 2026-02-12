package com.breakinblocks.deus_ex_machina.enums;

/**
 * Tracking mode for mob buff storage.
 * ENTITY_TYPE: Buffs are stored on the player, keyed by mob type.
 * INSTANCE: Buffs are stored on each mob instance, keyed by player UUID.
 */
public enum TypeEnum {
    ENTITY_TYPE,
    INSTANCE
}
