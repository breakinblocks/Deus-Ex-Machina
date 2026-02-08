package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = DeusExMachina.MODID)
public class BrewingRegistry {

    @SubscribeEvent
    public static void registerRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addRecipe(new AmbrosiaBrewingRecipe());
    }
}

class AmbrosiaBrewingRecipe implements IBrewingRecipe {

    @Override
    public boolean isInput(ItemStack input) {
        PotionContents contents = input.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(Potions.LONG_REGENERATION);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.is(Items.HONEY_BOTTLE);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            return new ItemStack(ItemRegistry.AMBROSIA.get());
        }
        return ItemStack.EMPTY;
    }
}
