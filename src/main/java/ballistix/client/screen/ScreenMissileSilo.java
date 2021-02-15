package ballistix.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.References;
import ballistix.common.inventory.container.ContainerMissileSilo;
import electrodynamics.client.screen.generic.GenericContainerScreen;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenMissileSilo extends GenericContainerScreen<ContainerMissileSilo> implements IHasContainer<ContainerMissileSilo> {
	public static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(References.ID + ":textures/gui/missilesilo.png");

	public ScreenMissileSilo(ContainerMissileSilo container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}

	@Override
	public ResourceLocation getScreenBackground() {
		return SCREEN_BACKGROUND;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		font.func_243248_b(matrixStack, title, titleX, titleY, 4210752);
		font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.missile"), (float) playerInventoryTitleX, playerInventoryTitleY - 55, 4210752);
		font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.explosive"), (float) playerInventoryTitleX, playerInventoryTitleY - 20, 4210752);
	}

}