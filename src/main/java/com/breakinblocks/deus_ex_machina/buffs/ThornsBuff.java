package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Thorns buff - reflect a percentage of damage back to the attacker.
 * value=30 means reflect 30% of damage taken.
 */
public class ThornsBuff extends AbstractBuff {

    public static final ResourceLocation ID = new ResourceLocation(DeusExMachina.MODID, "thorns");

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    public BuffCategory getCategory() { return BuffCategory.RESISTANCE; }

    @Override
    public Component getDisplayName() { return Component.translatable("buff.deus_ex_machina.thorns"); }

    @Override
    public int getColor() { return 0xFF8B4513; }

    @Override
    public BuffSettings getDefaultSettings() { return new BuffSettings(0, 100, 5, ResetEnum.FULL); }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        float reflectDamage = damage * (value / 100f);

        if (reflectDamage > 0 && context.target() != null) {
            DamageSource thornsSource = context.player().damageSources().thorns(context.player());
            context.target().hurt(thornsSource, reflectDamage);
        }

        return damage;
    }
}
