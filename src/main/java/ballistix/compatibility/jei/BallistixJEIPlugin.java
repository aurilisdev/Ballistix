package ballistix.compatibility.jei;

import java.util.HashSet;
import java.util.Set;

import ballistix.compatibility.jei.recipecategories.psuedo.specificmachines.WarheadRecipeCategory;
import ballistix.compatibility.jei.util.psuedorecipes.BallistixPsuedoRecipes;
import electrodynamics.compatibility.jei.recipecategories.psuedo.PsuedoItem2ItemRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class BallistixJEIPlugin implements IModPlugin {

	private static final String INFO_ITEM = "jei.info.item.";

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(electrodynamics.api.References.ID, "jei");
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(WarheadRecipeCategory.INPUT_MACHINE, WarheadRecipeCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		BallistixPsuedoRecipes.addBallistixRecipes();

		// Warhead Template
		Set<PsuedoItem2ItemRecipe> warheadTemplateRecipes = new HashSet<>(BallistixPsuedoRecipes.WARHEAD_RECIPES);
		registration.addRecipes(warheadTemplateRecipes, WarheadRecipeCategory.UID);

		ballistixInfoTabs(registration);

	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new WarheadRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	private static void ballistixInfoTabs(IRecipeRegistration registration) {

		for (ItemStack itemStack : BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(1)) {
			registration.addIngredientInfo(itemStack, VanillaTypes.ITEM, new TranslatableComponent(INFO_ITEM + itemStack.getItem().toString()));
		}

		for (ItemStack itemStack : BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(2)) {
			registration.addIngredientInfo(itemStack, VanillaTypes.ITEM, new TranslatableComponent(INFO_ITEM + itemStack.getItem().toString()));
		}

	}

}
