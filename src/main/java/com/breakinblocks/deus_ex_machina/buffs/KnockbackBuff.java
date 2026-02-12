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
 * Knockback buff - increased knockback on hit.
 * value=50 means 50% stronger knockback.
 */
public class KnockbackBuff extends AbstractBuff {

    public static final ResourceLocation ID = new ResourceLocation(DeusExMachina.MODID, "knockback");

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    public BuffCategory getCategory() { return BuffCategory.DAMAGE; }

    @Override
    public Component getDisplayName() { return Component.translatable("buff.deus_ex_machina.knockback"); }

    @Override
    public int getColor() { return 0xFF7777FF; }

    @Override
    public BuffSettings getDefaultSettings() { return new BuffSettings(0, 200, 10, ResetEnum.FULL); }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        if (context.target() == null) return damage;

        Vec3 playerPos = context.player().position();
        Vec3 targetPos = context.target().position();
        Vec3 direction = targetPos.subtract(playerPos).normalize();

        double strength = 0.4 * (1 + value / 100.0);
        context.target().push(direction.x * strength, 0.1, direction.z * strength);

        return damage;
    }
}
