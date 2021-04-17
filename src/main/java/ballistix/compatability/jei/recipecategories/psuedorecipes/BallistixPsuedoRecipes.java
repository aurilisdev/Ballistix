package ballistix.compatability.jei.recipecategories.psuedorecipes;

import java.util.ArrayList;

import ballistix.common.block.SubtypeBlast;
import electrodynamics.compatability.jei.recipecategories.psuedorecipes.PsuedoRecipes;
import electrodynamics.prefab.tile.processing.DO2OProcessingRecipe;
import electrodynamics.prefab.tile.processing.O2OProcessingRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BallistixPsuedoRecipes {

    public static ArrayList<ArrayList<ItemStack>> BALLISTIX_ITEMS = new ArrayList<>();

    public static ArrayList<DO2OProcessingRecipe> WARHEAD_RECIPES = new ArrayList<>();
    public static ArrayList<O2OProcessingRecipe> SILO_RECIPES = new ArrayList<>();

    public static void addBallistixRecipes() {
	PsuedoRecipes.addElectrodynamicsMachines();
	PsuedoRecipes.addElectrodynamicsFluids();
	PsuedoRecipes.addElectrodynamicsItems();

	addBallistixMachines();
	addBallistixFluids();
	addBallistixItems();

	/* SILO RECIPES */
	SILO_RECIPES.add(new O2OProcessingRecipe(BALLISTIX_ITEMS.get(1).get(0), BALLISTIX_ITEMS.get(2).get(0)));

	/* WARHEAD RECIPES */
	WARHEAD_RECIPES.add(new DO2OProcessingRecipe(BALLISTIX_ITEMS.get(0).get(0), BALLISTIX_ITEMS.get(1).get(0), BALLISTIX_ITEMS.get(1).get(0)));

    }

    private static void addBallistixMachines() {

    }

    private static void addBallistixFluids() {

    }

    private static void addBallistixItems() {

	/* EXPLOSIVES */
	SubtypeBlast[] explosives = SubtypeBlast.values();
	int explosivesLength = explosives.length;
	Item[] explosiveItems = new Item[explosivesLength];
	for (int i = 0; i < explosivesLength; i++) {
	    explosiveItems[i] = ballistix.DeferredRegisters.SUBTYPEBLOCK_MAPPINGS.get(explosives[i]).asItem();
	}
	BALLISTIX_ITEMS.add(formItemStacks(explosiveItems, 1));

	/* MISSILES */
	Item[] missiles = { ballistix.DeferredRegisters.ITEM_MISSILECLOSERANGE.get(), ballistix.DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get(),
		ballistix.DeferredRegisters.ITEM_MISSILELONGRANGE.get() };
	BALLISTIX_ITEMS.add(formItemStacks(missiles, 1));

	/* MISC */
	Item[] misc = { ballistix.DeferredRegisters.blockMissileSilo.asItem(), ballistix.DeferredRegisters.ITEM_ROCKETLAUNCHER.get() };
	BALLISTIX_ITEMS.add(formItemStacks(misc, 1));
    }

    private static ArrayList<ItemStack> formItemStacks(Item[] items, int countPerItemStack) {
	ArrayList<ItemStack> inputItems = new ArrayList<>();

	for (int i = 0; i < items.length; i++) {
	    inputItems.add(new ItemStack(items[i]));
	    inputItems.get(i).setCount(countPerItemStack);
	}
	return inputItems;
    }
}
