package ballistix.compatibility.jei;

import ballistix.Ballistix;
import ballistix.compatibility.jei.util.psuedorecipes.BallistixPsuedoRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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
			registration.addIngredientInfo(itemStack, VanillaTypes.ITEM_STACK, Component.translatable("jei.info.item." + BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath()));
		}

	}

}
