package ballistix.datagen.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ballistix.datagen.server.recipe.custom.item2item.BallistixMineralGrinderRecipes;
import ballistix.datagen.server.recipe.vanilla.BallistixCraftingTableRecipes;
import electrodynamics.datagen.utils.recipe.AbstractRecipeGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

public class BallistixRecipeProvider extends RecipeProvider {

	public final List<AbstractRecipeGenerator> GENERATORS = new ArrayList<>();

	public BallistixRecipeProvider(PackOutput output) {
		super(output);
		addRecipes();
	}

	public void addRecipes() {
		GENERATORS.add(new BallistixCraftingTableRecipes());
		GENERATORS.add(new BallistixMineralGrinderRecipes());
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		for (AbstractRecipeGenerator generator : GENERATORS) {
			generator.addRecipes(consumer);
		}
	}

}
