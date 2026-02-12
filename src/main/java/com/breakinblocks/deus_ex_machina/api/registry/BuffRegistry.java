package com.breakinblocks.deus_ex_machina.api.registry;

import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for buff types. Use this to register custom buff types.
 *
 * <p>Example usage:
 * <pre>{@code
 * BuffRegistry.register(SimpleBuffType.builder(
 *         new ResourceLocation("mymod", "my_buff"))
 *     .category(BuffCategory.RESISTANCE)
 *     .color(0xFF00FF00)
 *     .build()
 * );
 * }</pre>
 *
 * <p>Register your buff types during mod construction or FMLCommonSetupEvent.
 */
public class BuffRegistry {

    private static final Map<ResourceLocation, BuffType> REGISTRY = new LinkedHashMap<>();
    private static boolean frozen = false;

    /**
     * Register a buff type. Must be called before the registry is frozen
     * (which happens after FMLCommonSetupEvent).
     *
     * @param buffType The buff type to register
     * @return The registered buff type (for assignment to static fields)
     */
    public static BuffType register(BuffType buffType) {
        if (frozen) {
            throw new IllegalStateException("Cannot register buff types after initialization: " + buffType.getId());
        }
        if (REGISTRY.containsKey(buffType.getId())) {
            throw new IllegalArgumentException("Duplicate buff type registration: " + buffType.getId());
        }
        REGISTRY.put(buffType.getId(), buffType);
        return buffType;
    }

    /**
     * Freeze the registry. Called internally after setup is complete.
     */
    public static void freeze() {
        frozen = true;
    }

    /**
     * Get all registered buff types.
     */
    public static Collection<BuffType> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    /**
     * Get a buff type by its ID.
     */
    public static Optional<BuffType> get(ResourceLocation id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    /**
     * Get a buff type by its ID, or throw if not found.
     */
    public static BuffType getOrThrow(ResourceLocation id) {
        return get(id).orElseThrow(() ->
                new IllegalArgumentException("Unknown buff type: " + id));
    }
}
