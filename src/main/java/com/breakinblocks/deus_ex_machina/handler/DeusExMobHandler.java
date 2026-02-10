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
     * Gets the group key for an entity type.
     * If the entity matches a tag, returns the tag (e.g., "#minecraft:undead").
     * If it matches a regex, returns the regex pattern wrapped in slashes.
     * If it matches an exact entry, returns the entity's ResourceLocation.
     * Returns null if the entity doesn't match any config entry.
     */
    public static String getGroupKey(EntityType<?> entityType) {
        // Check exact matches first - return entity ID
        if (exactMatches.contains(entityType)) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            return id != null ? id.toString() : null;
        }

        // Check entity tags - return tag key
        if (!entityTags.isEmpty()) {
            Optional<Holder.Reference<EntityType<?>>> holderOptional = BuiltInRegistries.ENTITY_TYPE.getHolder(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
            if (holderOptional.isPresent()) {
                Holder<EntityType<?>> holder = holderOptional.get();
                for (TagKey<EntityType<?>> tag : entityTags) {
                    if (holder.is(tag)) {
                        return "#" + tag.location().toString();
                    }
                }
            }
        }

        // Check regex patterns - return the regex pattern string (wrapped in slashes)
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
