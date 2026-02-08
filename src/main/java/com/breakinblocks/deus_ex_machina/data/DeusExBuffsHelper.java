package com.breakinblocks.deus_ex_machina.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helper class to reduce boilerplate when working with Deus Ex Buffs attachments.
 */
public class DeusExBuffsHelper {

    /**
     * Get buffs for an entity. With attachments, this always returns the data.
     * For players, data is automatically attached.
     */
    public static DeusExBuffs getBuffs(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.getData(DeusExBuffsAttachment.DEUS_EX_BUFFS);
        }
        throw new IllegalArgumentException("DeusExBuffs only available on players");
    }

    /**
     * Get buffs for an entity (returns Optional for non-players).
     */
    public static Optional<DeusExBuffs> getBuffsOptional(LivingEntity entity) {
        if (entity instanceof Player player) {
            return Optional.of(player.getData(DeusExBuffsAttachment.DEUS_EX_BUFFS));
        }
        return Optional.empty();
    }

    /**
     * Execute action with buffs if entity is a player.
     */
    public static void withBuffs(LivingEntity entity, Consumer<DeusExBuffs> action) {
        if (entity instanceof Player player) {
            action.accept(player.getData(DeusExBuffsAttachment.DEUS_EX_BUFFS));
        }
    }

    /**
     * Get mob key (ResourceLocation) from entity type.
     */
    public static Optional<ResourceLocation> getMobKey(EntityType<?> entityType) {
        return Optional.ofNullable(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    /**
     * Get mob key from a living entity.
     */
    public static Optional<ResourceLocation> getMobKey(LivingEntity entity) {
        return getMobKey(entity.getType());
    }

    /**
     * Execute action with buffs and mob key if both are present.
     */
    public static void withBuffsForMob(LivingEntity player, EntityType<?> mobType, BiConsumer<DeusExBuffs, ResourceLocation> action) {
        getMobKey(mobType).ifPresent(key ->
                withBuffs(player, buff -> action.accept(buff, key))
        );
    }

    /**
     * Execute action with buffs and mob key if both are present.
     */
    public static void withBuffsForMob(LivingEntity player, LivingEntity mob, BiConsumer<DeusExBuffs, ResourceLocation> action) {
        withBuffsForMob(player, mob.getType(), action);
    }

    /**
     * Copy buffs from one entity to another (used on player respawn).
     */
    public static void copyBuffs(LivingEntity from, LivingEntity to) {
        getBuffsOptional(from).ifPresent(oldBuff ->
                withBuffs(to, newBuff -> newBuff.copyFrom(oldBuff))
        );
    }

    /**
     * Check if entity has buffs enabled.
     */
    public static boolean isEnabled(LivingEntity entity) {
        return getBuffsOptional(entity).map(IDeusExBuffs::isEnabled).orElse(false);
    }

    /**
     * Set enabled state for entity's buffs.
     */
    public static void setEnabled(LivingEntity entity, boolean enabled) {
        withBuffs(entity, buff -> buff.setEnabled(enabled));
    }
}
