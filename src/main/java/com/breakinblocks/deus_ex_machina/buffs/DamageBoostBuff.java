package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Damage boost buff - increases damage dealt to mobs.
 * value=50 means 50% more damage dealt.
 */
public class DamageBoostBuff extends AbstractBuff {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "attack_boost");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BuffCategory getCategory() {
        return BuffCategory.DAMAGE;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("buff.deus_ex_machina.attack_boost");
    }

    @Override
    public int getColor() {
        return 0xFFFF5555; // Red
    }

    @Override
    public BuffSettings getDefaultSettings() {
        return new BuffSettings(0, 50, 3, ResetEnum.FULL);
    }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        // Increase damage by value percent
        // value=50 -> deal 50% more damage
        // value=-25 -> deal 25% less damage (debuff)
        return Math.max(0, damage * (1 + value / 100f));
    }
}
