package com.breakinblocks.deus_ex_machina.integration;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.registry.BrewingRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class DeusJEIPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(DeusExMachina.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        List<IJeiBrewingRecipe> brewingRecipes = new ArrayList<>();
        for (BrewingRegistry.BrewingRecipe recipe : BrewingRegistry.getRecipes()) {
            ItemStack inputPotion = PotionContents.createItemStack(Items.POTION, recipe.getInputPotion());
            ItemStack ingredient = new ItemStack(recipe.getIngredientItem());
            ItemStack output = recipe.getRecipeOutput();

            brewingRecipes.add(factory.createBrewingRecipe(
                    List.of(ingredient),
                    List.of(inputPotion),
                    output
            ));
        }

        registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
    }
}
