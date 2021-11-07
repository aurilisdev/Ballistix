package ballistix.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentTextInputBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenMissileSilo extends GenericScreen<ContainerMissileSilo> {
    public ScreenMissileSilo(ContainerMissileSilo container, Inventory playerInventory, Component title) {
	super(container, playerInventory, title);
	components.add(new ScreenComponentTextInputBar(this, 122, 10).small());
	components.add(new ScreenComponentTextInputBar(this, 122, 28).small());
	components.add(new ScreenComponentTextInputBar(this, 122, 46).small());
	components.add(new ScreenComponentTextInputBar(this, 122, 64).small());
    }

    private EditBox xCoordField;
    private EditBox yCoordField;
    private EditBox zCoordField;
    private EditBox frequencyField;

    @Override
    public void containerTick() {
	super.containerTick();
	xCoordField.tick();
	yCoordField.tick();
	zCoordField.tick();
	frequencyField.tick();
    }

    @Override
    @Deprecated(since = "Uses deprecated method!", forRemoval = false)
    protected void init() {
	super.init();
	initFields();
    }

    @Deprecated(since = "Uses deprecated method!", forRemoval = false)
    protected void initFields() {
	minecraft.keyboardHandler.setSendRepeatsToGui(true);
	int i = (width - imageWidth) / 2;
	int j = (height - imageHeight) / 2;
	xCoordField = new EditBox(font, i + 127, j + 14, 46, 13, new TranslatableComponent("container.missilesilo.xCoord"));
	xCoordField.setTextColor(-1);
	xCoordField.setTextColorUneditable(-1);
	xCoordField.setBordered(false);
	xCoordField.setMaxLength(6);
	xCoordField.setResponder(this::setX);

	yCoordField = new EditBox(font, i + 127, j + 14 + 18, 46, 13, new TranslatableComponent("container.missilesilo.yCoord"));
	yCoordField.setTextColor(-1);
	yCoordField.setTextColorUneditable(-1);
	yCoordField.setBordered(false);
	yCoordField.setMaxLength(6);
	yCoordField.setResponder(this::setY);

	zCoordField = new EditBox(font, i + 127, j + 14 + 18 * 2, 46, 13, new TranslatableComponent("container.missilesilo.zCoord"));
	zCoordField.setTextColor(-1);
	zCoordField.setTextColorUneditable(-1);
	zCoordField.setBordered(false);
	zCoordField.setMaxLength(6);
	zCoordField.setResponder(this::setZ);
	frequencyField = new EditBox(font, i + 127, j + 14 + 18 * 3, 46, 13, new TranslatableComponent("container.missilesilo.frequency"));
	frequencyField.setTextColor(-1);
	zCoordField.setTextColorUneditable(-1);
	frequencyField.setBordered(false);
	frequencyField.setMaxLength(6);
	frequencyField.setResponder(this::setFrequency);
	addWidget(xCoordField);
	addWidget(yCoordField);
	addWidget(zCoordField);
	addWidget(frequencyField);
	setInitialFocus(frequencyField);
    }

    private boolean needsUpdate = true;

    @Deprecated(since = "Uses deprecated method!", forRemoval = false)
    private void setCoord(String coord) {
	if (!coord.isEmpty()) {
	    menu.setCoord(xCoordField.getValue(), yCoordField.getValue(), zCoordField.getValue(), frequencyField.getValue());
	}
    }

    @Deprecated(since = "Uses deprecated method!", forRemoval = false)
    private void setFrequency(String val) {
	frequencyField.setFocus(true);
	xCoordField.setFocus(false);
	yCoordField.setFocus(false);
	zCoordField.setFocus(false);
	setCoord(val);
    }

    @Deprecated(since = "Uses deprecated method!", forRemoval = false)
    private void setX(String val) {
	xCoordField.setFocus(true);
	yCoordField.setFocus(false);
	zCoordField.setFocus(false);
	setCoord(val);
    }

    @Deprecated(since = "Uses deprecated method!", forRemoval = false)
    private void setY(String val) {
	yCoordField.setFocus(true);
	xCoordField.setFocus(false);
	zCoordField.setFocus(false);
	setCoord(val);
    }

    @Deprecated(since = "Uses deprecated method!", forRemoval = false)
    private void setZ(String val) {
	zCoordField.setFocus(true);
	yCoordField.setFocus(false);
	xCoordField.setFocus(false);
	setCoord(val);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
	String s = xCoordField.getValue();
	String s1 = yCoordField.getValue();
	String s2 = zCoordField.getValue();
	String s3 = frequencyField.getValue();
	init(minecraft, width, height);
	xCoordField.setValue(s);
	yCoordField.setValue(s1);
	zCoordField.setValue(s2);
	frequencyField.setValue(s3);
    }

    @Override
    public void removed() {
	super.removed();
	minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
	super.render(matrixStack, mouseX, mouseY, partialTicks);
	if (needsUpdate) {
	    needsUpdate = false;
	    TileMissileSilo silo = menu.getHostFromIntArray();
	    if (silo != null && silo.target != null) {
		xCoordField.setValue("" + silo.target.intX());
		yCoordField.setValue("" + silo.target.intY());
		zCoordField.setValue("" + silo.target.intZ());
		frequencyField.setValue("" + silo.frequency);
	    }
	}
	xCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
	yCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
	zCoordField.render(matrixStack, mouseX, mouseY, partialTicks);
	frequencyField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
	super.renderLabels(matrixStack, mouseX, mouseY);
	font.draw(matrixStack, new TranslatableComponent("gui.missilesilo.missile"), inventoryLabelX, inventoryLabelY - 55.0f, 4210752);
	font.draw(matrixStack, new TranslatableComponent("gui.missilesilo.explosive"), inventoryLabelX, inventoryLabelY - 20.0f, 4210752);
    }

}