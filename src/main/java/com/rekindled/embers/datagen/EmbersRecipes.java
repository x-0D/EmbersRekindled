package com.rekindled.embers.datagen;

import java.util.function.Consumer;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.BoringRecipeBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

public class EmbersRecipes extends RecipeProvider implements IConditionBuilder {

	public EmbersRecipes(DataGenerator gen) {
		super(gen);
	}

	@Override
	public void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		String boringFolder = "boring";
		BoringRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(boringFolder).weight(20).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(boringFolder).weight(60).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(boringFolder).weight(20).maxHeight(7).save(consumer);
	}

	public ResourceLocation getResource(String name) {
		return new ResourceLocation(Embers.MODID, name);
	}
}
