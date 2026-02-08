package com.breakinblocks.deus_ex_machina.integration;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;

@JeiPlugin
public class DeusJEIPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(DeusExMachina.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        // Long Regeneration Potion + Honey Bottle = Ambrosia
        ItemStack inputPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LONG_REGENERATION);
        ItemStack ingredient = new ItemStack(Items.HONEY_BOTTLE);
        ItemStack output = new ItemStack(ItemRegistry.AMBROSIA.get());

        IJeiBrewingRecipe brewingRecipe = factory.createBrewingRecipe(
                List.of(ingredient),
                List.of(inputPotion),
                output
        );

        registration.addRecipes(RecipeTypes.BREWING, List.of(brewingRecipe));
    }
}
