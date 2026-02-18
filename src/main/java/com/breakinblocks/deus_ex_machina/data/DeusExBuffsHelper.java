package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Optional;


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
     * Get storage key for an entity type (used to store buff data per player).
     * Tags group entities under one key; regex matches return the entity's own ID.
     */
    public static Optional<String> getGroupKey(EntityType<?> entityType) {
        return Optional.ofNullable(DeusExMobHandler.getGroupKey(entityType));
    }

    /**
     * Get storage key from a living entity.
     */
    public static Optional<String> getGroupKey(LivingEntity entity) {
        return getGroupKey(entity.getType());
    }

    /**
     * Get config key for an entity type (used to look up datapack config settings).
     * Returns the original target string: tag, regex pattern, or entity ID.
     */
    public static Optional<String> getConfigKey(EntityType<?> entityType) {
        return Optional.ofNullable(DeusExMobHandler.getConfigKey(entityType));
    }

    /**
     * Get config key from a living entity.
     */
    public static Optional<String> getConfigKey(LivingEntity entity) {
        return getConfigKey(entity.getType());
    }

    /**
     * Execute action with buffs, storage key, and config key if all are present.
     * @param action receives (buffs, storageKey, configKey)
     */
    public static void withBuffsForMob(LivingEntity player, EntityType<?> mobType, TriConsumer<IDeusExBuffs, String, String> action) {
        getGroupKey(mobType).ifPresent(storageKey ->
                getConfigKey(mobType).ifPresent(configKey ->
                        withBuffs(player, buff -> action.accept(buff, storageKey, configKey))
                )
        );
    }

    /**
     * Execute action with buffs, storage key, and config key if all are present.
     * @param action receives (buffs, storageKey, configKey)
     */
    public static void withBuffsForMob(LivingEntity player, LivingEntity mob, TriConsumer<IDeusExBuffs, String, String> action) {
        withBuffsForMob(player, mob.getType(), action);
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
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
