package ballistix.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.References;
import ballistix.common.inventory.container.ContainerMissileSilo;
import electrodynamics.client.screen.generic.GenericContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.widget.TextFieldWidget;
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

	private TextFieldWidget xCoordField;
	private TextFieldWidget yCoordField;
	private TextFieldWidget zCoordField;

	@Override
	public void tick() {
		super.tick();
		this.xCoordField.tick();
		this.yCoordField.tick();
		this.zCoordField.tick();
	}

	@Override
	protected void init() {
		super.init();
		initFields();
	}

	protected void initFields() {
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.xCoordField = new TextFieldWidget(this.font, i + 127, j + 19, 103, 12, new TranslationTextComponent("container.missilesilo.xCoord"));
		this.xCoordField.setCanLoseFocus(false);
		this.xCoordField.setTextColor(-1);
		this.xCoordField.setDisabledTextColour(-1);
		this.xCoordField.setEnableBackgroundDrawing(false);
		this.xCoordField.setMaxStringLength(6);
		this.xCoordField.setResponder(this::setCoord);

		this.yCoordField = new TextFieldWidget(this.font, i + 127, j + 37, 103, 24, new TranslationTextComponent("container.missilesilo.yCoord"));
		this.yCoordField.setCanLoseFocus(false);
		this.yCoordField.setTextColor(-1);
		this.yCoordField.setDisabledTextColour(-1);
		this.yCoordField.setEnableBackgroundDrawing(false);
		this.yCoordField.setMaxStringLength(6);
		this.yCoordField.setResponder(this::setCoord);

		this.zCoordField = new TextFieldWidget(this.font, i + 127, j + 55, 103, 36, new TranslationTextComponent("container.missilesilo.zCoord"));
		this.zCoordField.setCanLoseFocus(false);
		this.zCoordField.setTextColor(-1);
		this.zCoordField.setDisabledTextColour(-1);
		this.zCoordField.setEnableBackgroundDrawing(false);
		this.zCoordField.setMaxStringLength(6);
		this.zCoordField.setResponder(this::setCoord);
		this.children.add(this.xCoordField);
		this.children.add(this.yCoordField);
		this.children.add(this.zCoordField);
		this.setFocusedDefault(this.zCoordField);
	}

	private boolean needsUpdate = true;

	private void setCoord(String coord) {
		if (!coord.isEmpty()) {
			this.container.setCoord(xCoordField.getText(), yCoordField.getText(), zCoordField.getText());
		}
	}

	public void resize(Minecraft minecraft, int width, int height) {
		String s = this.xCoordField.getText();
		String s1 = this.yCoordField.getText();
		String s2 = this.zCoordField.getText();
		this.init(minecraft, width, height);
		this.xCoordField.setText(s);
		this.yCoordField.setText(s1);
		this.zCoordField.setText(s2);
	}

	public void onClose() {
		super.onClose();
		this.minecraft.keyboardListener.enableRepeatEvents(false);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.minecraft.player.closeScreen();
		}
		boolean x = !this.xCoordField.keyPressed(keyCode, scanCode, modifiers) && !this.xCoordField.canWrite();
		boolean y = !this.yCoordField.keyPressed(keyCode, scanCode, modifiers) && !this.yCoordField.canWrite();
		boolean z = !this.zCoordField.keyPressed(keyCode, scanCode, modifiers) && !this.zCoordField.canWrite();
		return x || y || z ? super.keyPressed(keyCode, scanCode, modifiers) : true;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		 if (needsUpdate) {
			needsUpdate = false;
			xCoordField.setText("" + container.getTXCoord());
			yCoordField.setText("" + container.getTYCoord());
			zCoordField.setText("" + container.getTZCoord());
		}
		xCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
		yCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
		zCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		font.func_243248_b(matrixStack, title, titleX, titleY, 4210752);
		font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.missile"), playerInventoryTitleX, playerInventoryTitleY - 55, 4210752);
		font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.explosive"), playerInventoryTitleX, playerInventoryTitleY - 20, 4210752);
	}

}