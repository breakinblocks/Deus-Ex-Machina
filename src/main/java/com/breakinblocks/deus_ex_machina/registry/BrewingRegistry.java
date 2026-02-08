package com.breakinblocks.deus_ex_machina.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class BrewingRegistry {
    public static void registerRecipes() {
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                Potions.LONG_REGENERATION,
                Items.HONEY_BOTTLE,
                new ItemStack(ItemRegistry.AMBROSIA.get())
        ));
    }
}

class BrewingRecipe implements IBrewingRecipe {

    private final Potion input;
    private final Item ingredient;
    private final ItemStack output;

    public BrewingRecipe(Potion input, Item ingredient, ItemStack output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }
    @Override
    public boolean isInput(ItemStack input) {
        return PotionUtils.getPotion(input) == this.input;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.getItem() == this.ingredient;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            return this.output.copy();
        }
        return ItemStack.EMPTY;
    }
}
