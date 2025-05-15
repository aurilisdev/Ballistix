package ballistix.datagen.server.recipe;

import ballistix.datagen.server.recipe.custom.item2item.BallistixMineralGrinderRecipes;
import ballistix.datagen.server.recipe.vanilla.BallistixCraftingTableRecipes;
import net.minecraft.data.DataGenerator;
import voltaic.datagen.utils.server.recipe.BaseRecipeProvider;

public class BallistixRecipeProvider extends BaseRecipeProvider {

	public BallistixRecipeProvider(DataGenerator gen) {
		super(gen);
	}

	public void addRecipes() {
		generators.add(new BallistixCraftingTableRecipes());
		generators.add(new BallistixMineralGrinderRecipes());
	}

}
