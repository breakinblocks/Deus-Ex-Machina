package com.breakinblocks.deus_ex_machina.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DeusExMobHandler {
    private static Set<EntityType<?>> exactMatches = new HashSet<>();
    private static Set<Pattern> regexPatterns = new HashSet<>();
    private static final Map<EntityType<?>, Boolean> cache = new HashMap<>();

    public static void reload(List<? extends String> entries) {
        exactMatches.clear();
        regexPatterns.clear();
        cache.clear();

        for (String entry : entries) {
            if (entry.startsWith("/") && entry.endsWith("/") && entry.length() > 2) {
                String regex = entry.substring(1, entry.length() - 1);
                try {
                    regexPatterns.add(Pattern.compile(regex));
                } catch (PatternSyntaxException e) {
                    System.err.println("[DeusExMachina] Invalid regex pattern: " + entry + " - " + e.getMessage());
                }
            } else {
                ResourceLocation resourceLocation = ResourceLocation.tryParse(entry);
                if (resourceLocation != null) {
                    EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
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
        if (regexPatterns.isEmpty()) return false;

        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if (id == null) return false;

        String fullId = id.toString();
        for (Pattern pattern : regexPatterns) {
            if (pattern.matcher(fullId).matches()) return true;
        }
        return false;
    }
}
