package ballistix.compatibility.jei.recipecategories.psuedo.specificmachines;

import java.util.ArrayList;
import java.util.List;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.References;
import ballistix.compatibility.jei.util.psuedorecipes.BallistixPsuedoRecipes;
import electrodynamics.compatibility.jei.recipecategories.psuedo.PsuedoItem2ItemRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class WarheadRecipeCategory implements IRecipeCategory<PsuedoItem2ItemRecipe> {

	// JEI Window Parameters
	private static final int INPUT_1_SLOT = 0;
	private static final int INPUT_2_SLOT = 1;

	private static int[] GUI_BACKGROUND = { 0, 0, 132, 58 };
	private static int[] PROCESSING_ARROW_COORDS = { 0, 0, 10, 10 };

	private static int[] INPUT_1_OFFSET = { 89, 7 };
	private static int[] INPUT_2_OFFSET = { 89, 33 };

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

		ICON = guiHelper.createDrawableIngredient(INPUT_MACHINE);
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
	public void setIngredients(PsuedoItem2ItemRecipe recipe, IIngredients ingredients) {
		List<List<ItemStack>> inputSlots = new ArrayList<>();
		inputSlots.add(BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(0));
		inputSlots.add(BallistixPsuedoRecipes.BALLISTIX_ITEMS.get(1));
		ingredients.setInputLists(VanillaTypes.ITEM, inputSlots);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PsuedoItem2ItemRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(INPUT_2_SLOT, true, INPUT_1_OFFSET[0], INPUT_1_OFFSET[1]);
		guiItemStacks.init(INPUT_1_SLOT, true, INPUT_2_OFFSET[0], INPUT_2_OFFSET[1]);

		guiItemStacks.set(ingredients);
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
