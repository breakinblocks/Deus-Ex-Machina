package com.breakinblocks.deus_ex_machina.buffs;

import com.breakinblocks.deus_ex_machina.api.buff.BuffCategory;
import com.breakinblocks.deus_ex_machina.api.buff.BuffContext;
import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for buff implementations.
 */
public abstract class AbstractBuff implements BuffType {

    @Override
    public abstract ResourceLocation getId();

    @Override
    public abstract BuffCategory getCategory();

    @Override
    public abstract Component getDisplayName();

    @Override
    public abstract int getColor();

    @Override
    @Nullable
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public abstract BuffSettings getDefaultSettings();

    @Override
    public abstract float apply(int value, float damage, BuffContext context);

    @Override
    public boolean appliesOnPlayerHurt() {
        return getCategory() == BuffCategory.RESISTANCE;
    }

    @Override
    public boolean appliesOnPlayerAttack() {
        return getCategory() == BuffCategory.DAMAGE;
    }
}
