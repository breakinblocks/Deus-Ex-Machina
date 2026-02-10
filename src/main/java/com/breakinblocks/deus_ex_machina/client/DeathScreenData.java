package com.breakinblocks.deus_ex_machina.client;

import net.minecraft.resources.ResourceLocation;

/**
 * Client-side cache for displaying buff gains on the death screen.
 */
public class DeathScreenData {
    private static ResourceLocation killerType = null;
    private static boolean resistanceEnabled = false;
    private static boolean attackEnabled = false;
    private static int resistanceGain = 0;
    private static int attackBoostGain = 0;
    private static int newResistance = 0;
    private static int newAttackBoost = 0;

    public static void set(ResourceLocation killer, boolean resEnabled, boolean atkEnabled,
                           int resGain, int atkGain, int newRes, int newAtk) {
        killerType = killer;
        resistanceEnabled = resEnabled;
        attackEnabled = atkEnabled;
        resistanceGain = resGain;
        attackBoostGain = atkGain;
        newResistance = newRes;
        newAttackBoost = newAtk;
    }

    public static void clear() {
        killerType = null;
        resistanceEnabled = false;
        attackEnabled = false;
        resistanceGain = 0;
        attackBoostGain = 0;
        newResistance = 0;
        newAttackBoost = 0;
    }

    public static boolean hasData() {
        return killerType != null;
    }

    public static ResourceLocation getKillerType() {
        return killerType;
    }

    public static boolean isResistanceEnabled() {
        return resistanceEnabled;
    }

    public static boolean isAttackEnabled() {
        return attackEnabled;
    }

    public static int getResistanceGain() {
        return resistanceGain;
    }

    public static int getAttackBoostGain() {
        return attackBoostGain;
    }

    public static int getNewResistance() {
        return newResistance;
    }

    public static int getNewAttackBoost() {
        return newAttackBoost;
    }
}
