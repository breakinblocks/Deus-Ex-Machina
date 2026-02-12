package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Critical chance buff - chance to deal bonus critical damage.
 * value=25 means 25% chance to deal 50% bonus damage.
 */
public class CriticalChanceBuff extends AbstractBuff {

    public static final ResourceLocation ID = new ResourceLocation(DeusExMachina.MODID, "critical_chance");
    private static final float CRIT_MULTIPLIER = 1.5f;

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    public BuffCategory getCategory() { return BuffCategory.DAMAGE; }

    @Override
    public Component getDisplayName() { return Component.translatable("buff.deus_ex_machina.critical_chance"); }

    @Override
    public int getColor() { return 0xFFFFAA00; }

    @Override
    public BuffSettings getDefaultSettings() { return new BuffSettings(0, 75, 5, ResetEnum.FULL); }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        if (context.player().getRandom().nextInt(100) < value) {
            return damage * CRIT_MULTIPLIER;
        }
        return damage;
    }
}
