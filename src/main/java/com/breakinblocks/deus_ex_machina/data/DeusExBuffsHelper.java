package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Helper class to reduce boilerplate when working with Deus Ex Buffs capabilities.
 */
public class DeusExBuffsHelper {

    /**
     * Get buffs for an entity (returns Optional).
     */
    public static Optional<IDeusExBuffs> getBuffs(LivingEntity entity) {
        return entity.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).resolve();
    }

    /**
     * Execute action with buffs if present.
     */
    public static void withBuffs(LivingEntity entity, NonNullConsumer<IDeusExBuffs> action) {
        entity.getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).ifPresent(action);
    }

    /**
     * Get group key for an entity type.
     * Returns the tag (e.g., "#minecraft:undead") if the entity matches a tag,
     * otherwise returns the entity's ResourceLocation string.
     */
    public static Optional<String> getGroupKey(EntityType<?> entityType) {
        return Optional.ofNullable(DeusExMobHandler.getGroupKey(entityType));
    }

    /**
     * Get group key from a living entity.
     */
    public static Optional<String> getGroupKey(LivingEntity entity) {
        return getGroupKey(entity.getType());
    }

    /**
     * Execute action with buffs and group key if both are present.
     */
    public static void withBuffsForMob(LivingEntity player, EntityType<?> mobType, BiConsumer<IDeusExBuffs, String> action) {
        getGroupKey(mobType).ifPresent(key ->
                withBuffs(player, buff -> action.accept(buff, key))
        );
    }

    /**
     * Execute action with buffs and group key if both are present.
     */
    public static void withBuffsForMob(LivingEntity player, LivingEntity mob, BiConsumer<IDeusExBuffs, String> action) {
        withBuffsForMob(player, mob.getType(), action);
    }

    /**
     * Copy buffs from one entity to another (used on player respawn).
     */
    public static void copyBuffs(LivingEntity from, LivingEntity to) {
        getBuffs(from).ifPresent(oldBuff ->
                withBuffs(to, newBuff -> newBuff.copyFrom(oldBuff))
        );
    }

    /**
     * Check if entity has buffs enabled.
     */
    public static boolean isEnabled(LivingEntity entity) {
        return getBuffs(entity).map(IDeusExBuffs::isEnabled).orElse(false);
    }

    /**
     * Set enabled state for entity's buffs.
     */
    public static void setEnabled(LivingEntity entity, boolean enabled) {
        withBuffs(entity, buff -> buff.setEnabled(enabled));
    }
}
