package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Berserker buff - deal more damage but also take more damage.
 * value=30 means deal 30% more damage AND take 30% more damage.
 * A high-risk, high-reward buff.
 */
public class BerserkerBuff extends AbstractBuff {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "berserker");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BuffCategory getCategory() {
        return BuffCategory.MISC; // Hybrid - affects both
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("buff.deus_ex_machina.berserker");
    }

    @Override
    public int getColor() {
        return 0xFFCC0000; // Blood red
    }

    @Override
    public BuffSettings getDefaultSettings() {
        return new BuffSettings(0, 50, 5, ResetEnum.FULL);
    }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        // Both hurt and attack: multiply damage by (1 + value%)
        return damage * (1 + value / 100f);
    }

    @Override
    public boolean appliesOnPlayerHurt() {
        return true; // Take MORE damage
    }

    @Override
    public boolean appliesOnPlayerAttack() {
        return true; // Deal MORE damage
    }
}
