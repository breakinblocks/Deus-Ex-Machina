package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Life leech buff - heals the player for a percentage of damage dealt.
 * value=10 means heal 10% of damage dealt.
 */
public class LifeLeechBuff extends AbstractBuff {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "life_leech");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BuffCategory getCategory() {
        return BuffCategory.HEALTH;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("buff.deus_ex_machina.life_leech");
    }

    @Override
    public int getColor() {
        return 0xFFFF55FF; // Magenta/Pink
    }

    @Override
    public BuffSettings getDefaultSettings() {
        return new BuffSettings(0, 25, 2, ResetEnum.FULL);
    }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        float healAmount = damage * (value / 100f);

        if (healAmount > 0) {
            context.player().heal(healAmount);
        }

        return damage;
    }

    @Override
    public boolean appliesOnPlayerHurt() {
        return false;
    }

    @Override
    public boolean appliesOnPlayerAttack() {
        return true;
    }
}
