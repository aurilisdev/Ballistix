package ballistix.compatability.jei;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ballistix.compatability.jei.recipecategories.psuedorecipes.BallistixPsuedoRecipes;
import ballistix.compatability.jei.recipecategories.specificmachines.ballistix.WarheadRecipeCategory;
import electrodynamics.compatability.jei.recipecategories.psuedorecipes.PsuedoDO2ORecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class BallistixJEIPlugin implements IModPlugin {

    // public static final boolean isBallistixLoaded =
    // ModList.get().isLoaded("ballistix");

    @Override
    public ResourceLocation getPluginUid() {
	return new ResourceLocation(electrodynamics.api.References.ID, "balx_jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
	// Warhead Template
	registration.addRecipeCatalyst(new ItemStack(ballistix.DeferredRegisters.ITEM_MISSILELONGRANGE.get()), WarheadRecipeCategory.UID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
	BallistixPsuedoRecipes.addBallistixRecipes();

	// Warhead Template
	Set<PsuedoDO2ORecipe> warheadTemplateRecipes = new HashSet<>(BallistixPsuedoRecipes.WARHEAD_RECIPES);

	registration.addRecipes(warheadTemplateRecipes, WarheadRecipeCategory.UID);

	ballistixInfoTabs(registration);

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
	// Warhead Template
	registration.addRecipeCategories(new WarheadRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    private static void ballistixInfoTabs(IRecipeRegistration registration) {
	/*
	 * Items currently with tabs:
	 * 
	 * Close Range Missile Medium Range Missile Long Range Missile Missile Silo
	 * 
	 */
	ArrayList<ItemStack> ballistixInfoItems = BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(1);
	String temp;

	for (ItemStack itemStack : ballistixInfoItems) {
	    temp = itemStack.getItem().toString();
	    registration.addIngredientInfo(itemStack, VanillaTypes.ITEM, "info.jei.item." + temp);
	    // logger.info("Item name: " + temp);
	}

	ballistixInfoItems = BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(2);

	for (ItemStack itemStack : ballistixInfoItems) {
	    temp = itemStack.getItem().toString();
	    registration.addIngredientInfo(itemStack, VanillaTypes.ITEM, "info.jei.item." + temp);
	    // logger.info("Item name: " + temp);
	}

    }

}
