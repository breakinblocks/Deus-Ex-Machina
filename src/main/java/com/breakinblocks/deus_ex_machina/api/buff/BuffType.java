package com.breakinblocks.deus_ex_machina.api.buff;

import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Defines a type of buff that can be gained through the Deus Ex Machina system.
 * Register buff types using {@link com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry}.
 */
public interface BuffType {

    /**
     * @return The unique identifier for this buff type
     */
    ResourceLocation getId();

    /**
     * @return The category this buff belongs to
     */
    BuffCategory getCategory();

    /**
     * @return The display name shown to players
     */
    Component getDisplayName();

    /**
     * @return The color used for display (ARGB format)
     */
    int getColor();

    /**
     * @return Optional icon texture location for display
     */
    @Nullable
    ResourceLocation getIcon();

    /**
     * @return Default settings for this buff (min, max, increase, reset)
     */
    BuffSettings getDefaultSettings();

    /**
     * Apply this buff's effect to a damage value.
     *
     * @param value   The current buff value (can be negative for debuffs)
     * @param damage  The original damage value
     * @param context Context about the damage event
     * @return The modified damage value
     */
    float apply(int value, float damage, BuffContext context);

    /**
     * @return true if this buff should apply when the player is taking damage
     */
    default boolean appliesOnPlayerHurt() {
        return getCategory() == BuffCategory.RESISTANCE;
    }

    /**
     * @return true if this buff should apply when the player is dealing damage
     */
    default boolean appliesOnPlayerAttack() {
        return getCategory() == BuffCategory.DAMAGE;
    }
}
