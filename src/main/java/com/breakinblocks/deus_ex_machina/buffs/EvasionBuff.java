package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Evasion buff - chance to completely dodge an attack.
 * value=20 means 20% chance to take no damage.
 */
public class EvasionBuff extends AbstractBuff {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "evasion");

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
        return Component.translatable("buff.deus_ex_machina.evasion");
    }

    @Override
    public int getColor() {
        return 0xFF55FFFF; // Cyan
    }

    @Override
    public BuffSettings getDefaultSettings() {
        return new BuffSettings(0, 50, 3, ResetEnum.FULL);
    }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        // value=20 -> 20% chance to dodge
        if (context.player().getRandom().nextInt(100) < value) {
            return 0; // Dodged!
        }
        return damage;
    }
}
