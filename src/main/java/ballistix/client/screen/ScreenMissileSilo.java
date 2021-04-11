package ballistix.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentTextInputBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenMissileSilo extends GenericScreen<ContainerMissileSilo> {
    public ScreenMissileSilo(ContainerMissileSilo container, PlayerInventory playerInventory, ITextComponent title) {
	super(container, playerInventory, title);
	components.add(new ScreenComponentTextInputBar(this, 122, 14).small());
	components.add(new ScreenComponentTextInputBar(this, 122, 32).small());
	components.add(new ScreenComponentTextInputBar(this, 122, 50).small());
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
	xCoordField = new TextFieldWidget(font, i + 127, j + 18, 46, 13, new TranslationTextComponent("container.missilesilo.xCoord"));
	xCoordField.setTextColor(-1);
	xCoordField.setDisabledTextColour(-1);
	xCoordField.setEnableBackgroundDrawing(false);
	xCoordField.setMaxStringLength(6);
	xCoordField.setResponder(this::setX);

	yCoordField = new TextFieldWidget(font, i + 127, j + 18 + 18, 46, 13, new TranslationTextComponent("container.missilesilo.yCoord"));
	yCoordField.setTextColor(-1);
	yCoordField.setDisabledTextColour(-1);
	yCoordField.setEnableBackgroundDrawing(false);
	yCoordField.setMaxStringLength(6);
	yCoordField.setResponder(this::setY);

	zCoordField = new TextFieldWidget(font, i + 127, j + 18 + 18 * 2, 46, 13, new TranslationTextComponent("container.missilesilo.zCoord"));
	zCoordField.setTextColor(-1);
	zCoordField.setDisabledTextColour(-1);
	zCoordField.setEnableBackgroundDrawing(false);
	zCoordField.setMaxStringLength(6);
	zCoordField.setResponder(this::setZ);
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

    private void setX(String val) {
	xCoordField.setFocused2(true);
	yCoordField.setFocused2(false);
	zCoordField.setFocused2(false);
	setCoord(val);
    }

    private void setY(String val) {
	yCoordField.setFocused2(true);
	xCoordField.setFocused2(false);
	zCoordField.setFocused2(false);
	setCoord(val);
    }

    private void setZ(String val) {
	zCoordField.setFocused2(true);
	yCoordField.setFocused2(false);
	xCoordField.setFocused2(false);
	setCoord(val);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
	String s = xCoordField.getText();
	String s1 = yCoordField.getText();
	String s2 = zCoordField.getText();
	init(minecraft, width, height);
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
	super.render(matrixStack, mouseX, mouseY, partialTicks);
	if (needsUpdate) {
	    needsUpdate = false;
	    TileMissileSilo silo = container.getHostFromIntArray();
	    if (silo != null && silo.target != null) {
		xCoordField.setText("" + silo.target.intX());
		yCoordField.setText("" + silo.target.intY());
		zCoordField.setText("" + silo.target.intZ());
	    }
	}
	xCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
	yCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
	zCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
	super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
	font.func_243248_b(matrixStack, title, titleX, titleY, 4210752);
	font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.missile"), playerInventoryTitleX, playerInventoryTitleY - 55.0f,
		4210752);
	font.func_243248_b(matrixStack, new TranslationTextComponent("gui.missilesilo.explosive"), playerInventoryTitleX,
		playerInventoryTitleY - 20.0f, 4210752);
    }

}