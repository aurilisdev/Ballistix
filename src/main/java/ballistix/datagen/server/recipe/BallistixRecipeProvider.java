package ballistix.datagen.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ballistix.datagen.server.recipe.custom.item2item.BallistixMineralGrinderRecipes;
import ballistix.datagen.server.recipe.vanilla.BallistixCraftingTableRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import voltaic.datagen.utils.server.recipe.AbstractRecipeGenerator;

public class BallistixRecipeProvider extends RecipeProvider {

	public final List<AbstractRecipeGenerator> GENERATORS = new ArrayList<>();

	public BallistixRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
		addRecipes();
	}

	public void addRecipes() {
		GENERATORS.add(new BallistixCraftingTableRecipes());
		GENERATORS.add(new BallistixMineralGrinderRecipes());
	}

	@Override
	protected void buildRecipes(RecipeOutput output) {
		for (AbstractRecipeGenerator generator : GENERATORS) {
			generator.addRecipes(output);
		}
	}

}
