package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/**
 * Knockback resistance buff - reduces knockback taken when hit.
 * value=50 means 50% less knockback taken.
 */
public class KnockbackResistanceBuff extends AbstractBuff {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "knockback_resistance");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public BuffCategory getCategory() {
        return BuffCategory.RESISTANCE;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("buff.deus_ex_machina.knockback_resistance");
    }

    @Override
    public int getColor() {
        return 0xFF4444AA; // Dark blue
    }

    @Override
    public BuffSettings getDefaultSettings() {
        return new BuffSettings(0, 100, 10, ResetEnum.FULL);
    }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        if (value > 0) {
            Vec3 currentVel = context.player().getDeltaMovement();
            double reduction = 1.0 - (value / 100.0);

            context.player().setDeltaMovement(
                currentVel.x * reduction,
                currentVel.y,
                currentVel.z * reduction
            );
        }

        return damage; // Don't modify damage
    }
}
