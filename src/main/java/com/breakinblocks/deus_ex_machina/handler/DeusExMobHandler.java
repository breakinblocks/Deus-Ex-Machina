package com.breakinblocks.deus_ex_machina.handler;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DeusExMobHandler {
    private static Set<EntityType<?>> exactMatches = new HashSet<>();
    private static Set<Pattern> regexPatterns = new HashSet<>();
    private static Set<TagKey<EntityType<?>>> entityTags = new HashSet<>();
    private static final Map<EntityType<?>, Boolean> cache = new HashMap<>();

    public static void reload(List<? extends String> entries) {
        exactMatches.clear();
        regexPatterns.clear();
        entityTags.clear();
        cache.clear();

        for (String entry : entries) {
            if (entry.startsWith("#")) {
                // Entity tag format: #namespace:tagname
                String tagId = entry.substring(1);
                ResourceLocation tagLocation = ResourceLocation.tryParse(tagId);
                if (tagLocation != null) {
                    entityTags.add(TagKey.create(Registries.ENTITY_TYPE, tagLocation));
                } else {
                    System.err.println("[DeusExMachina] Invalid tag format: " + entry);
                }
            } else if (entry.startsWith("/") && entry.endsWith("/") && entry.length() > 2) {
                String regex = entry.substring(1, entry.length() - 1);
                try {
                    regexPatterns.add(Pattern.compile(regex));
                } catch (PatternSyntaxException e) {
                    System.err.println("[DeusExMachina] Invalid regex pattern: " + entry + " - " + e.getMessage());
                }
            } else {
                ResourceLocation resourceLocation = ResourceLocation.tryParse(entry);
                if (resourceLocation != null) {
                    EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);
                    if (type != null) {
                        exactMatches.add(type);
                    }
                }
            }
        }
    }

    public static boolean matches(EntityType<?> entityType) {
        return cache.computeIfAbsent(entityType, DeusExMobHandler::compute);
    }

    private static boolean compute(EntityType<?> entityType) {
        if (exactMatches.contains(entityType)) return true;

        // Check entity tags
        if (!entityTags.isEmpty()) {
            Optional<Holder.Reference<EntityType<?>>> holderOptional = BuiltInRegistries.ENTITY_TYPE.getHolder(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
            if (holderOptional.isPresent()) {
                Holder<EntityType<?>> holder = holderOptional.get();
                for (TagKey<EntityType<?>> tag : entityTags) {
                    if (holder.is(tag)) return true;
                }
            }
        }

        // Check regex patterns
        if (!regexPatterns.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            if (id != null) {
                String fullId = id.toString();
                for (Pattern pattern : regexPatterns) {
                    if (pattern.matcher(fullId).matches()) return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the storage key for an entity type (used to store buff data per player).
     * If the entity matches a tag, returns the tag (e.g., "#minecraft:undead") — tags group entities.
     * If it matches a regex or exact entry, returns the entity's own ResourceLocation string.
     * Regex patterns act only as matchers — each entity gets its own storage key.
     * Returns null if the entity doesn't match any config entry.
     */
    public static String getGroupKey(EntityType<?> entityType) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        if (id == null) return null;

        // Check exact matches - return entity ID
        if (exactMatches.contains(entityType)) {
            return id.toString();
        }

        // Check entity tags - return tag key (tags group entities together)
        if (!entityTags.isEmpty()) {
            Optional<Holder.Reference<EntityType<?>>> holderOptional = BuiltInRegistries.ENTITY_TYPE.getHolder(id);
            if (holderOptional.isPresent()) {
                Holder<EntityType<?>> holder = holderOptional.get();
                for (TagKey<EntityType<?>> tag : entityTags) {
                    if (holder.is(tag)) {
                        return "#" + tag.location().toString();
                    }
                }
            }
        }

        // Check regex patterns - return entity's own ID (regex is just a matcher)
        if (!regexPatterns.isEmpty()) {
            String fullId = id.toString();
            for (Pattern pattern : regexPatterns) {
                if (pattern.matcher(fullId).matches()) {
                    return fullId;
                }
            }
        }

        return null;
    }

    /**
     * Gets the config key for an entity type (used to look up datapack config settings).
     * This returns the original target string from the datapack: the tag, regex pattern, or entity ID.
     * Returns null if the entity doesn't match any config entry.
     */
    public static String getConfigKey(EntityType<?> entityType) {
        // Check exact matches - config key is the entity ID
        if (exactMatches.contains(entityType)) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            return id != null ? id.toString() : null;
        }

        // Check entity tags - config key is the tag string
        if (!entityTags.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            if (id != null) {
                Optional<Holder.Reference<EntityType<?>>> holderOptional = BuiltInRegistries.ENTITY_TYPE.getHolder(id);
                if (holderOptional.isPresent()) {
                    Holder<EntityType<?>> holder = holderOptional.get();
                    for (TagKey<EntityType<?>> tag : entityTags) {
                        if (holder.is(tag)) {
                            return "#" + tag.location().toString();
                        }
                    }
                }
            }
        }

        // Check regex patterns - config key is the original regex target
        if (!regexPatterns.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            if (id != null) {
                String fullId = id.toString();
                for (Pattern pattern : regexPatterns) {
                    if (pattern.matcher(fullId).matches()) {
                        return "/" + pattern.pattern() + "/";
                    }
                }
            }
        }

        return null;
    }
}
