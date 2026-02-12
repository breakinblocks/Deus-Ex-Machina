package com.breakinblocks.deus_ex_machina.api.buff;

import com.breakinblocks.deus_ex_machina.data.BuffSettings;
import com.breakinblocks.deus_ex_machina.enums.ResetEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


/**
 * Simple implementation of BuffType using a builder pattern.
 */
public class SimpleBuffType implements BuffType {
    private final ResourceLocation id;
    private final BuffCategory category;
    private final Component displayName;
    private final int color;
    @Nullable
    private final ResourceLocation icon;
    private final BuffSettings defaultSettings;
    private final BuffApplicator applicator;
    private final boolean appliesOnPlayerHurt;
    private final boolean appliesOnPlayerAttack;

    private SimpleBuffType(Builder builder) {
        this.id = builder.id;
        this.category = builder.category;
        this.displayName = builder.displayName;
        this.color = builder.color;
        this.icon = builder.icon;
        this.defaultSettings = builder.defaultSettings;
        this.applicator = builder.applicator;
        this.appliesOnPlayerHurt = builder.appliesOnPlayerHurt;
        this.appliesOnPlayerAttack = builder.appliesOnPlayerAttack;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public BuffCategory getCategory() {
        return category;
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    @Nullable
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public BuffSettings getDefaultSettings() {
        return defaultSettings;
    }

    @Override
    public float apply(int value, float damage, BuffContext context) {
        return applicator.apply(value, damage);
    }

    @Override
    public boolean appliesOnPlayerHurt() {
        return appliesOnPlayerHurt;
    }

    @Override
    public boolean appliesOnPlayerAttack() {
        return appliesOnPlayerAttack;
    }

    /**
     * Functional interface for buff application logic.
     */
    @FunctionalInterface
    public interface BuffApplicator {
        /**
         * @param value  Buff value (can be negative for debuffs)
         * @param damage Original damage value
         * @return Modified damage value
         */
        float apply(int value, float damage);
    }

    /**
     * Common applicator implementations.
     */
    public static class Applicators {
        /**
         * Reduces damage by value percent.
         * value=50 -> 50% damage reduction
         * value=-25 -> 25% more damage taken
         */
        public static final BuffApplicator RESISTANCE = (value, damage) ->
                Math.max(0, damage * (1 - value / 100f));

        /**
         * Increases damage by value percent.
         * value=50 -> 50% more damage dealt
         * value=-25 -> 25% less damage dealt
         */
        public static final BuffApplicator DAMAGE_BOOST = (value, damage) ->
                Math.max(0, damage * (1 + value / 100f));
    }

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static class Builder {
        private final ResourceLocation id;
        private BuffCategory category = BuffCategory.MISC;
        private Component displayName;
        private int color = 0xFFFFFFFF;
        @Nullable
        private ResourceLocation icon;
        private BuffSettings defaultSettings = new BuffSettings(0, 100, 5, ResetEnum.FULL);
        private BuffApplicator applicator = (value, damage) -> damage;
        private boolean appliesOnPlayerHurt = false;
        private boolean appliesOnPlayerAttack = false;

        private Builder(ResourceLocation id) {
            this.id = id;
            this.displayName = Component.translatable("buff." + id.getNamespace() + "." + id.getPath());
        }

        public Builder category(BuffCategory category) {
            this.category = category;
            // Set sensible defaults based on category
            if (category == BuffCategory.RESISTANCE) {
                this.appliesOnPlayerHurt = true;
                this.applicator = Applicators.RESISTANCE;
            } else if (category == BuffCategory.DAMAGE) {
                this.appliesOnPlayerAttack = true;
                this.applicator = Applicators.DAMAGE_BOOST;
            }
            return this;
        }

        public Builder displayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public Builder defaultSettings(BuffSettings settings) {
            this.defaultSettings = settings;
            return this;
        }

        public Builder defaultSettings(int min, int max, int increase, ResetEnum reset) {
            this.defaultSettings = new BuffSettings(min, max, increase, reset);
            return this;
        }

        public Builder applicator(BuffApplicator applicator) {
            this.applicator = applicator;
            return this;
        }

        public Builder appliesOnPlayerHurt(boolean applies) {
            this.appliesOnPlayerHurt = applies;
            return this;
        }

        public Builder appliesOnPlayerAttack(boolean applies) {
            this.appliesOnPlayerAttack = applies;
            return this;
        }

        public SimpleBuffType build() {
            return new SimpleBuffType(this);
        }
    }
}
