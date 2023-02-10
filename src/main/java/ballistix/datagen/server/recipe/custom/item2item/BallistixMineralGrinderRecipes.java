package ballistix.datagen.server.recipe.custom.item2item;

import java.util.function.Consumer;

import ballistix.References;
import ballistix.registers.BallistixItems;
import electrodynamics.datagen.server.recipe.types.custom.item2item.ElectrodynamicsMineralGrinderRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BallistixMineralGrinderRecipes extends ElectrodynamicsMineralGrinderRecipes {
	
	public BallistixMineralGrinderRecipes() {
		super(References.ID);
	}
	
	@Override
	public void addRecipes(Consumer<FinishedRecipe> consumer) {
		
		newRecipe(new ItemStack(BallistixItems.ITEM_DUSTPOISON.get(), 2), 0.1F, MINERALGRINDER_REQUIRED_TICKS, MINERALGRINDER_USAGE_PER_TICK, "poison_dust_from_rotten_flesh")
		//
		.addItemStackInput(new ItemStack(Items.ROTTEN_FLESH))
		//
		.complete(consumer);
		
	}

}
