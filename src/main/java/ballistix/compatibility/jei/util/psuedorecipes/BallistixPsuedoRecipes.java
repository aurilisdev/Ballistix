package ballistix.compatibility.jei.util.psuedorecipes;

import java.util.ArrayList;
import java.util.Arrays;

import ballistix.common.block.subtype.SubtypeBlast;
import electrodynamics.compatibility.jei.recipecategories.psuedo.PsuedoItem2ItemRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BallistixPsuedoRecipes {

	public static ArrayList<ArrayList<ItemStack>> BALLISTIX_ITEMS = new ArrayList<>();

	public static ArrayList<PsuedoItem2ItemRecipe> WARHEAD_RECIPES = new ArrayList<>();

	public static void addBallistixRecipes() {

		addBallistixItems();

		WARHEAD_RECIPES.add(new PsuedoItem2ItemRecipe(Arrays.asList(new ItemStack[] { BALLISTIX_ITEMS.get(0).get(0), BALLISTIX_ITEMS.get(1).get(0) }),
				BALLISTIX_ITEMS.get(1).get(0)));

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
