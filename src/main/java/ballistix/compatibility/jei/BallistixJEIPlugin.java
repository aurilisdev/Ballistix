package ballistix.compatibility.jei;

import ballistix.Ballistix;
import ballistix.compatibility.jei.util.psuedorecipes.BallistixPsuedoRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class BallistixJEIPlugin implements IModPlugin {

	public static final ResourceLocation ID = Ballistix.rl("jei");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		BallistixPsuedoRecipes.addBallistixRecipes();

		ballistixInfoTabs(registration);

	}

	private static void ballistixInfoTabs(IRecipeRegistration registration) {

		for (ItemStack itemStack : BallistixPsuedoRecipes.BALLISTIX_ITEMS) {
			//registration.addIngredientInfo(itemStack, VanillaTypes.ITEM, new TranslationTextComponent("jei.info.item." + ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getPath()));
		}

	}

}
