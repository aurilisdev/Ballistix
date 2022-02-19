package ballistix.compatibility.jei.recipecategories.psuedo.specificmachines;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.References;
import ballistix.compatibility.jei.util.psuedorecipes.BallistixPsuedoRecipes;
import electrodynamics.compatibility.jei.recipecategories.psuedo.PsuedoItem2ItemRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class WarheadRecipeCategory implements IRecipeCategory<PsuedoItem2ItemRecipe> {

	// JEI Window Parameters
	private static int[] GUI_BACKGROUND = { 0, 0, 132, 58 };
	private static int[] PROCESSING_ARROW_COORDS = { 0, 0, 10, 10 };

	private static int[] PROCESSING_ARROW_OFFSET = { 0, 0 };

	private static int ANIM_TIME = 50;
	private static int DESC_Y_HEIGHT = 48;

	private static String MOD_ID = References.ID;
	private static String RECIPE_GROUP = "warhead_template";
	private static String GUI_TEXTURE = "textures/gui/jei/warhead_template_gui.png";

	public static ItemStack INPUT_MACHINE = new ItemStack(ballistix.DeferredRegisters.blockMissileSilo);

	private IDrawable BACKGROUND;
	private IDrawable ICON;

	private static IDrawableAnimated.StartDirection START_DIRECTION = IDrawableAnimated.StartDirection.LEFT;
	private LoadingCache<Integer, IDrawableAnimated> cachedArrows;

	public static ResourceLocation UID = new ResourceLocation(MOD_ID, RECIPE_GROUP);

	public WarheadRecipeCategory(IGuiHelper guiHelper) {

		ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, INPUT_MACHINE);
		BACKGROUND = guiHelper.createDrawable(new ResourceLocation(MOD_ID, GUI_TEXTURE), GUI_BACKGROUND[0], GUI_BACKGROUND[1], GUI_BACKGROUND[2], GUI_BACKGROUND[3]);

		cachedArrows = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<Integer, IDrawableAnimated>() {
			@Override
			public IDrawableAnimated load(Integer cookTime) {
				return guiHelper.drawableBuilder(new ResourceLocation(MOD_ID, GUI_TEXTURE), PROCESSING_ARROW_COORDS[0], PROCESSING_ARROW_COORDS[1], PROCESSING_ARROW_COORDS[2], PROCESSING_ARROW_COORDS[3]).buildAnimated(cookTime, START_DIRECTION, false);
			}
		});

	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends PsuedoItem2ItemRecipe> getRecipeClass() {
		return PsuedoItem2ItemRecipe.class;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("gui.jei.category." + RECIPE_GROUP);
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, PsuedoItem2ItemRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.CATALYST, 90, 8).addItemStacks(BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(0));
		builder.addSlot(RecipeIngredientRole.CATALYST, 90, 34).addItemStacks(BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(1));
	}

	@Override
	public void draw(PsuedoItem2ItemRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		IDrawableAnimated arrow = cachedArrows.getUnchecked(ANIM_TIME);
		arrow.draw(matrixStack, PROCESSING_ARROW_OFFSET[0], PROCESSING_ARROW_OFFSET[1]);

		int animTimeSeconds = ANIM_TIME / 20;

		TranslatableComponent missileString = new TranslatableComponent("gui.jei.category." + RECIPE_GROUP + ".info.missile", animTimeSeconds);
		TranslatableComponent explosiveString = new TranslatableComponent("gui.jei.category." + RECIPE_GROUP + ".info.explosive", animTimeSeconds);

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;

		int missileWidth = fontRenderer.width(missileString);
		int explosiveWidth = fontRenderer.width(explosiveString);

		fontRenderer.draw(matrixStack, missileString, GUI_BACKGROUND[2] - missileWidth - 46f, DESC_Y_HEIGHT - 37f, 0xFF333333);
		fontRenderer.draw(matrixStack, explosiveString, GUI_BACKGROUND[2] - explosiveWidth - 46f, DESC_Y_HEIGHT - 10f, 0xFF333333);
	}

}
