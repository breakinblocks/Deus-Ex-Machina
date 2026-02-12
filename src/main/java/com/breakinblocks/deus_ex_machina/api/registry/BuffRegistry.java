package com.breakinblocks.deus_ex_machina.api.registry;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Registry for buff types. Use this to register custom buff types.
 *
 * <p>Example usage:
 * <pre>{@code
 * public static final Supplier<BuffType> MY_BUFF = BuffRegistry.BUFF_TYPES.register("my_buff",
 *     () -> SimpleBuffType.builder(ResourceLocation.fromNamespaceAndPath("mymod", "my_buff"))
 *         .category(BuffCategory.RESISTANCE)
 *         .color(0xFF00FF00)
 *         .build()
 * );
 * }</pre>
 */
public class BuffRegistry {

    /**
     * The registry key for buff types.
     */
    public static final ResourceKey<Registry<BuffType>> BUFF_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "buff_types"));

    /**
     * Deferred register for buff types that also creates the registry.
     */
    public static final DeferredRegister<BuffType> BUFF_TYPES =
            DeferredRegister.create(BUFF_TYPE_REGISTRY_KEY, DeusExMachina.MODID);

    /**
     * The registry instance. Available after NewRegistryEvent.
     */
    public static final Registry<BuffType> BUFF_TYPE_REGISTRY =
            BUFF_TYPES.makeRegistry(builder -> {});

    /**
     * Create a DeferredRegister for buff types for another mod.
     *
     * @param modId The mod ID
     * @return A new DeferredRegister for buff types
     */
    public static DeferredRegister<BuffType> createDeferredRegister(String modId) {
        return DeferredRegister.create(BUFF_TYPE_REGISTRY_KEY, modId);
    }

    /**
     * Get the registry. Only available after registration.
     */
    public static Registry<BuffType> getRegistry() {
        return BUFF_TYPE_REGISTRY;
    }

    /**
     * Get all registered buff types.
     */
    public static Collection<BuffType> getAll() {
        return getRegistry().stream().toList();
    }

    /**
     * Get a buff type by its ID.
     */
    public static Optional<BuffType> get(ResourceLocation id) {
        return getRegistry().getOptional(id);
    }

    /**
     * Get a buff type by its ID, or throw if not found.
     */
    public static BuffType getOrThrow(ResourceLocation id) {
        return get(id).orElseThrow(() ->
                new IllegalArgumentException("Unknown buff type: " + id));
    }
}
