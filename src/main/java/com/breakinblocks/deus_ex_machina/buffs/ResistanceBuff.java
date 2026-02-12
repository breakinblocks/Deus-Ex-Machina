package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Resistance buff - reduces damage taken from mobs.
 * value=50 means 50% damage reduction.
 */
public class ResistanceBuff extends AbstractBuff {

    public static final ResourceLocation ID = new ResourceLocation(DeusExMachina.MODID, "resistance");

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    public BuffCategory getCategory() { return BuffCategory.RESISTANCE; }

    @Override
    public Component getDisplayName() { return Component.translatable("buff.deus_ex_machina.resistance"); }

    @Override
    public int getColor() { return 0xFF55FF55; }

    @Override
    public BuffSettings getDefaultSettings() { return new BuffSettings(0, 80, 5, ResetEnum.FULL); }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        return Math.max(0, damage * (1 - value / 100f));
    }
}
