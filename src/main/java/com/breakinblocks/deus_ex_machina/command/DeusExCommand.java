package com.breakinblocks.deus_ex_machina.command;

import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import com.breakinblocks.deus_ex_machina.data.DeusExMobConfigManager;
import com.breakinblocks.deus_ex_machina.data.IDeusExBuffs;
import com.breakinblocks.deus_ex_machina.handler.DeusExMobHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DeusExCommand {

    private static final SimpleCommandExceptionType ERROR_NO_BUFFS = new SimpleCommandExceptionType(
            Component.literal("Player does not have buff data"));
    private static final SimpleCommandExceptionType ERROR_UNKNOWN_BUFF = new SimpleCommandExceptionType(
            Component.literal("Unknown buff type"));

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_BUFF_TYPES = (context, builder) -> {
        Stream<ResourceLocation> buffIds = BuffRegistry.getAll().stream().map(BuffType::getId);
        return SharedSuggestionProvider.suggestResource(buffIds, builder);
    };

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ENTITY_TYPES = (context, builder) -> {
        Stream<ResourceLocation> entityIds = ForgeRegistries.ENTITY_TYPES.getKeys().stream();
        return SharedSuggestionProvider.suggestResource(entityIds, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("deus_ex_machina")
                .requires(source -> source.hasPermission(2))
                // set <buffType> <entityType> <value> [player]
                .then(Commands.literal("set")
                        .then(Commands.argument("buffType", ResourceLocationArgument.id())
                                .suggests(SUGGEST_BUFF_TYPES)
                                .then(Commands.argument("entityType", ResourceLocationArgument.id())
                                        .suggests(SUGGEST_ENTITY_TYPES)
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                .executes(ctx -> executeSet(ctx, null))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(ctx -> executeSet(ctx, EntityArgument.getPlayer(ctx, "player")))
                                                )
                                        )
                                )
                        )
                )
                // add <buffType> <entityType> <amount> [player]
                .then(Commands.literal("add")
                        .then(Commands.argument("buffType", ResourceLocationArgument.id())
                                .suggests(SUGGEST_BUFF_TYPES)
                                .then(Commands.argument("entityType", ResourceLocationArgument.id())
                                        .suggests(SUGGEST_ENTITY_TYPES)
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> executeAdd(ctx, null))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(ctx -> executeAdd(ctx, EntityArgument.getPlayer(ctx, "player")))
                                                )
                                        )
                                )
                        )
                )
                // remove <buffType> <entityType> <amount> [player]
                .then(Commands.literal("remove")
                        .then(Commands.argument("buffType", ResourceLocationArgument.id())
                                .suggests(SUGGEST_BUFF_TYPES)
                                .then(Commands.argument("entityType", ResourceLocationArgument.id())
                                        .suggests(SUGGEST_ENTITY_TYPES)
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> executeRemove(ctx, null))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(ctx -> executeRemove(ctx, EntityArgument.getPlayer(ctx, "player")))
                                                )
                                        )
                                )
                        )
                )
                // reset all all [player] — reset everything
                // reset all <entityType> [player] — reset all buffs for one entity
                // reset <buffType> <entityType> [player] — reset single buff
                .then(Commands.literal("reset")
                        .then(Commands.literal("all")
                                .then(Commands.literal("all")
                                        .executes(ctx -> executeResetAll(ctx, null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> executeResetAll(ctx, EntityArgument.getPlayer(ctx, "player")))
                                        )
                                )
                                .then(Commands.argument("entityType", ResourceLocationArgument.id())
                                        .suggests(SUGGEST_ENTITY_TYPES)
                                        .executes(ctx -> executeResetAllBuffs(ctx, null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> executeResetAllBuffs(ctx, EntityArgument.getPlayer(ctx, "player")))
                                        )
                                )
                        )
                        .then(Commands.argument("buffType", ResourceLocationArgument.id())
                                .suggests(SUGGEST_BUFF_TYPES)
                                .then(Commands.argument("entityType", ResourceLocationArgument.id())
                                        .suggests(SUGGEST_ENTITY_TYPES)
                                        .executes(ctx -> executeReset(ctx, null))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> executeReset(ctx, EntityArgument.getPlayer(ctx, "player")))
                                        )
                                )
                        )
                )
        );
    }

    private static ServerPlayer resolvePlayer(CommandContext<CommandSourceStack> ctx, ServerPlayer explicit) throws CommandSyntaxException {
        if (explicit != null) return explicit;
        return ctx.getSource().getPlayerOrException();
    }

    private static BuffType resolveBuffType(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation buffId = ResourceLocationArgument.getId(ctx, "buffType");
        Optional<BuffType> buffType = BuffRegistry.get(buffId);
        if (buffType.isEmpty()) {
            throw ERROR_UNKNOWN_BUFF.create();
        }
        return buffType.get();
    }

    /**
     * Resolves entity type argument into storage key and config key.
     * @return [storageKey, configKey]
     */
    private static String[] resolveMobKeys(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation entityId = ResourceLocationArgument.getId(ctx, "entityType");
        net.minecraft.world.entity.EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityId);
        if (entityType != null) {
            String storageKey = DeusExMobHandler.getGroupKey(entityType);
            String configKey = DeusExMobHandler.getConfigKey(entityType);
            if (storageKey != null && configKey != null) {
                return new String[]{storageKey, configKey};
            }
        }
        // Fall back to raw entity ID for both
        String fallback = entityId.toString();
        return new String[]{fallback, fallback};
    }

    private static int clampValue(int value, String configKey, BuffType buffType) {
        int min = DeusExMobConfigManager.getBuffMin(configKey, buffType);
        int max = DeusExMobConfigManager.getBuffMax(configKey, buffType);
        return Math.max(min, Math.min(max, value));
    }

    private static int executeSet(CommandContext<CommandSourceStack> ctx, ServerPlayer explicit) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, explicit);
        BuffType buffType = resolveBuffType(ctx);
        String[] keys = resolveMobKeys(ctx);
        String storageKey = keys[0], configKey = keys[1];
        int value = IntegerArgumentType.getInteger(ctx, "value");

        IDeusExBuffs buffs = DeusExBuffsHelper.getBuffs(player).orElseThrow(ERROR_NO_BUFFS::create);
        int clamped = clampValue(value, configKey, buffType);
        buffs.setBuff(storageKey, buffType.getId(), clamped);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Set " + buffType.getId() + " for " + storageKey + " to " + clamped + " for " + player.getName().getString()
        ), true);
        return clamped;
    }

    private static int executeAdd(CommandContext<CommandSourceStack> ctx, ServerPlayer explicit) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, explicit);
        BuffType buffType = resolveBuffType(ctx);
        String[] keys = resolveMobKeys(ctx);
        String storageKey = keys[0], configKey = keys[1];
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        IDeusExBuffs buffs = DeusExBuffsHelper.getBuffs(player).orElseThrow(ERROR_NO_BUFFS::create);
        int current = buffs.getBuff(storageKey, buffType.getId());
        int newValue = clampValue(current + amount, configKey, buffType);
        buffs.setBuff(storageKey, buffType.getId(), newValue);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Added " + amount + " to " + buffType.getId() + " for " + storageKey + " (now " + newValue + ") for " + player.getName().getString()
        ), true);
        return newValue;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> ctx, ServerPlayer explicit) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, explicit);
        BuffType buffType = resolveBuffType(ctx);
        String[] keys = resolveMobKeys(ctx);
        String storageKey = keys[0], configKey = keys[1];
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        IDeusExBuffs buffs = DeusExBuffsHelper.getBuffs(player).orElseThrow(ERROR_NO_BUFFS::create);
        int current = buffs.getBuff(storageKey, buffType.getId());
        int newValue = clampValue(current - amount, configKey, buffType);
        buffs.setBuff(storageKey, buffType.getId(), newValue);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Removed " + amount + " from " + buffType.getId() + " for " + storageKey + " (now " + newValue + ") for " + player.getName().getString()
        ), true);
        return newValue;
    }

    private static int executeResetAllBuffs(CommandContext<CommandSourceStack> ctx, ServerPlayer explicit) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, explicit);
        String[] keys = resolveMobKeys(ctx);
        String storageKey = keys[0], configKey = keys[1];

        IDeusExBuffs buffs = DeusExBuffsHelper.getBuffs(player).orElseThrow(ERROR_NO_BUFFS::create);
        int count = 0;
        for (BuffType buffType : BuffRegistry.getAll()) {
            int minValue = DeusExMobConfigManager.getBuffMin(configKey, buffType);
            buffs.setBuff(storageKey, buffType.getId(), minValue);
            count++;
        }

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Reset " + finalCount + " buff(s) for " + storageKey + " for " + player.getName().getString()
        ), true);
        return count;
    }

    private static int executeResetAll(CommandContext<CommandSourceStack> ctx, ServerPlayer explicit) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, explicit);

        IDeusExBuffs buffs = DeusExBuffsHelper.getBuffs(player).orElseThrow(ERROR_NO_BUFFS::create);
        Map<String, Map<ResourceLocation, Integer>> allBuffs = buffs.getAllBuffs();
        int count = 0;
        for (String storageKey : allBuffs.keySet()) {
            for (BuffType buffType : BuffRegistry.getAll()) {
                buffs.setBuff(storageKey, buffType.getId(), 0);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Reset all buffs for all entities (" + finalCount + " total) for " + player.getName().getString()
        ), true);
        return count;
    }

    private static int executeReset(CommandContext<CommandSourceStack> ctx, ServerPlayer explicit) throws CommandSyntaxException {
        ServerPlayer player = resolvePlayer(ctx, explicit);
        BuffType buffType = resolveBuffType(ctx);
        String[] keys = resolveMobKeys(ctx);
        String storageKey = keys[0], configKey = keys[1];

        IDeusExBuffs buffs = DeusExBuffsHelper.getBuffs(player).orElseThrow(ERROR_NO_BUFFS::create);
        int minValue = DeusExMobConfigManager.getBuffMin(configKey, buffType);
        buffs.setBuff(storageKey, buffType.getId(), minValue);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Reset " + buffType.getId() + " for " + storageKey + " to " + minValue + " for " + player.getName().getString()
        ), true);
        return minValue;
    }
}
