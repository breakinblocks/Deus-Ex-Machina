package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
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
    public static void withBuffsForMob(LivingEntity player, EntityType<?> mobType, TriConsumer<DeusExBuffs, String, String> action) {
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
    public static void withBuffsForMob(LivingEntity player, LivingEntity mob, TriConsumer<DeusExBuffs, String, String> action) {
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
