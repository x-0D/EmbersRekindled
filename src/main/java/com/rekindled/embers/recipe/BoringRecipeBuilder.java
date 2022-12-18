package com.rekindled.embers.recipe;

import java.util.HashSet;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class BoringRecipeBuilder {

	public ResourceLocation id;
	public ItemStack result;
	public int weight = 20;
	public int minHeight = Integer.MIN_VALUE;
	public int maxHeight = Integer.MAX_VALUE;
	public HashSet<ResourceLocation> dimensions = new HashSet<>();
	public HashSet<ResourceLocation> biomes = new HashSet<>();

	public static BoringRecipeBuilder create(ItemStack itemStack) {
		BoringRecipeBuilder builder = new BoringRecipeBuilder();
		builder.result = itemStack;
		builder.id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
		return builder;
	}

	public static BoringRecipeBuilder create(Item item) {
		return create(new ItemStack(item));
	}

	public BoringRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public BoringRecipeBuilder folder(String folder) {
		this.id = new ResourceLocation(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public BoringRecipeBuilder weight(int weight) {
		this.weight = weight;
		return this;
	}

	public BoringRecipeBuilder minHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public BoringRecipeBuilder maxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public BoringRecipeBuilder dimensions(HashSet<ResourceLocation> dimensions) {
		this.dimensions = dimensions;
		return this;
	}

	public BoringRecipeBuilder biomes(HashSet<ResourceLocation> biomes) {
		this.biomes = biomes;
		return this;
	}

	public BoringRecipeBuilder dimension(ResourceLocation... dimensions) {
		for (ResourceLocation dimension : dimensions)
			this.dimensions.add(dimension);
		return this;
	}

	public BoringRecipeBuilder biome(ResourceLocation... biomes) {
		for (ResourceLocation biome : biomes)
			this.biomes.add(biome);
		return this;
	}

	public BoringRecipe build() {
		return new BoringRecipe(id, new WeightedItemStack(result, weight), minHeight, maxHeight, dimensions, biomes);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final BoringRecipe recipe;

		public Finished(BoringRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			if (recipe.minHeight != Integer.MIN_VALUE)
				json.addProperty("min_height", recipe.minHeight);
			if (recipe.minHeight != Integer.MAX_VALUE)
				json.addProperty("max_height", recipe.maxHeight);

			if (!recipe.dimensions.isEmpty()) {
				JsonArray dimJson = new JsonArray();
				for (ResourceLocation dimension : recipe.dimensions) {
					JsonObject object = new JsonObject();
					object.addProperty("item", dimension.toString());
					dimJson.add(object);
				}
				json.add("dimensions", dimJson);
			}
			if (!recipe.biomes.isEmpty()) {
				JsonArray biomeJson = new JsonArray();
				for (ResourceLocation biome : recipe.biomes) {
					JsonObject object = new JsonObject();
					object.addProperty("item", biome.toString());
					biomeJson.add(object);
				}
				json.add("biomes", biomeJson);
			}
			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(recipe.result.getStack().getItem()).toString());
			int count = recipe.result.getStack().getCount();
			if (count > 1) {
				resultJson.addProperty("count", count);
			}
			json.add("output", resultJson);
			json.addProperty("weight", recipe.result.getWeight().asInt());
		}

		@Override
		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			return RegistryManager.BORING_SERIALIZER.get();
		}

		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
