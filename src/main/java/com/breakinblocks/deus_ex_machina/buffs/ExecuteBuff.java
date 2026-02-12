package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Execute buff - deal bonus damage to low-health targets.
 * value=30 means deal 30% more damage when target is below 30% health.
 */
public class ExecuteBuff extends AbstractBuff {

    public static final ResourceLocation ID = new ResourceLocation(DeusExMachina.MODID, "execute");
    private static final float HEALTH_THRESHOLD = 0.3f;

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    public BuffCategory getCategory() { return BuffCategory.DAMAGE; }

    @Override
    public Component getDisplayName() { return Component.translatable("buff.deus_ex_machina.execute"); }

    @Override
    public int getColor() { return 0xFF8B0000; }

    @Override
    public BuffSettings getDefaultSettings() { return new BuffSettings(0, 100, 5, ResetEnum.FULL); }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        if (context.target() == null) return damage;

        float healthPercent = context.target().getHealth() / context.target().getMaxHealth();
        if (healthPercent <= HEALTH_THRESHOLD) {
            return damage * (1 + value / 100f);
        }
        return damage;
    }
}
