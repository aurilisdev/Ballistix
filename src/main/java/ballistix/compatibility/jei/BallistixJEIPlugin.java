package ballistix.compatibility.jei;


import ballistix.compatibility.jei.util.psuedorecipes.BallistixPsuedoRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
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
	public void registerRecipes(IRecipeRegistration registration) {
		BallistixPsuedoRecipes.addBallistixRecipes();
		
		ballistixInfoTabs(registration);

	}

	private static void ballistixInfoTabs(IRecipeRegistration registration) {

		for (ItemStack itemStack : BallistixPsuedoRecipes.BALLISTIX_ITEMS) {
			registration.addIngredientInfo(itemStack, VanillaTypes.ITEM, new TranslatableComponent(INFO_ITEM + itemStack.getItem().toString()));
		}

	}

}
