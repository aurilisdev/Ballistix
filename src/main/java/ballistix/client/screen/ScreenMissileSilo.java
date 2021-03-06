package ballistix.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.References;
import ballistix.common.inventory.container.ContainerMissileSilo;
import electrodynamics.client.screen.generic.GenericContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenMissileSilo extends GenericContainerScreen<ContainerMissileSilo> {
    public static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(
	    References.ID + ":textures/gui/missilesilo.png");

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
	xCoordField.tick();
	yCoordField.tick();
	zCoordField.tick();
    }

    @Override
    protected void init() {
	super.init();
	initFields();
    }

    protected void initFields() {
	minecraft.keyboardListener.enableRepeatEvents(true);
	int i = (width - xSize) / 2;
	int j = (height - ySize) / 2;
	xCoordField = new TextFieldWidget(font, i + 127, j + 18, 46, 13,
		new TranslationTextComponent("container.missilesilo.xCoord"));
	xCoordField.setTextColor(-1);
	xCoordField.setDisabledTextColour(-1);
	xCoordField.setEnableBackgroundDrawing(false);
	xCoordField.setMaxStringLength(6);
	xCoordField.setResponder(this::setCoord);

	yCoordField = new TextFieldWidget(font, i + 127, j + 18 + 18, 46, 13,
		new TranslationTextComponent("container.missilesilo.yCoord"));
	yCoordField.setTextColor(-1);
	yCoordField.setDisabledTextColour(-1);
	yCoordField.setEnableBackgroundDrawing(false);
	yCoordField.setMaxStringLength(6);
	yCoordField.setResponder(this::setCoord);

	zCoordField = new TextFieldWidget(font, i + 127, j + 18 + 18 * 2, 46, 13,
		new TranslationTextComponent("container.missilesilo.zCoord"));
	zCoordField.setTextColor(-1);
	zCoordField.setDisabledTextColour(-1);
	zCoordField.setEnableBackgroundDrawing(false);
	zCoordField.setMaxStringLength(6);
	zCoordField.setResponder(this::setCoord);
	children.add(xCoordField);
	children.add(yCoordField);
	children.add(zCoordField);
	setFocusedDefault(zCoordField);
    }

    private boolean needsUpdate = true;

    private void setCoord(String coord) {
	if (!coord.isEmpty()) {
	    container.setCoord(xCoordField.getText(), yCoordField.getText(), zCoordField.getText());
	}
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
	String s = xCoordField.getText();
	String s1 = yCoordField.getText();
	String s2 = zCoordField.getText();
	this.init(minecraft, width, height);
	xCoordField.setText(s);
	yCoordField.setText(s1);
	zCoordField.setText(s2);
    }

    @Override
    public void onClose() {
	super.onClose();
	minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
	if (keyCode == 256) {
	    minecraft.player.closeScreen();
	}
	boolean x = !xCoordField.keyPressed(keyCode, scanCode, modifiers) && !xCoordField.canWrite();
	boolean y = !yCoordField.keyPressed(keyCode, scanCode, modifiers) && !yCoordField.canWrite();
	boolean z = !zCoordField.keyPressed(keyCode, scanCode, modifiers) && !zCoordField.canWrite();
	if (x || y || z) {
	    return super.keyPressed(keyCode, scanCode, modifiers);
	}
	return true;
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
	font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.missile"), playerInventoryTitleX,
		playerInventoryTitleY - 55.0f, 4210752);
	font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.explosive"),
		playerInventoryTitleX, playerInventoryTitleY - 20.0f, 4210752);
    }

}