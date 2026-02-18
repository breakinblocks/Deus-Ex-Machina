package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = DeusExMachina.MODID)
public class BrewingRegistry {

    private static final List<BrewingRecipe> RECIPES = new ArrayList<>();

    public static List<BrewingRecipe> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    @SubscribeEvent
    public static void registerRecipes(RegisterBrewingRecipesEvent event) {
        addRecipe(event, new BrewingRecipe(
                Potions.STRONG_HEALING,
                Items.HONEY_BOTTLE,
                new ItemStack(ItemRegistry.AMBROSIA.get())
        ));
    }

    private static void addRecipe(RegisterBrewingRecipesEvent event, BrewingRecipe recipe) {
        event.getBuilder().addRecipe(recipe);
        RECIPES.add(recipe);
    }

    public static class BrewingRecipe implements IBrewingRecipe {

        private final Holder<Potion> input;
        private final Item ingredient;
        private final ItemStack output;

        public BrewingRecipe(Holder<Potion> input, Item ingredient, ItemStack output) {
            this.input = input;
            this.ingredient = ingredient;
            this.output = output;
        }

        public Holder<Potion> getInputPotion() {
            return input;
        }

        public Item getIngredientItem() {
            return ingredient;
        }

        public ItemStack getRecipeOutput() {
            return output.copy();
        }

        @Override
        public boolean isInput(ItemStack input) {
            PotionContents contents = input.get(DataComponents.POTION_CONTENTS);
            return contents != null && contents.is(this.input);
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return ingredient.is(this.ingredient);
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (isInput(input) && isIngredient(ingredient)) {
                return this.output.copy();
            }
            return ItemStack.EMPTY;
        }
    }
}
