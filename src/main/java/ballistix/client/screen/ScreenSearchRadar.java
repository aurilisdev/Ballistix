package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.prefab.screen.WrapperSearchFrequencyManager;
import ballistix.prefab.screen.WrapperSearchRadarDetections;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentSlot;
import voltaic.prefab.screen.component.types.ScreenComponentVerticalSlider;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;

public class ScreenSearchRadar extends GenericScreen<ContainerSearchRadar> {

    public final ScreenComponentVerticalSlider slider;
    public final ScreenComponentVerticalSlider detectionsSlider;
    public final WrapperSearchFrequencyManager frequencyWrapper;
    public final WrapperSearchRadarDetections detectionsWrapper;

    public ScreenSearchRadar(ContainerSearchRadar container, Inventory inv, Component title) {
	super(container, inv, title);

	addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2)
		.wattage(BallistixConfig.INSTANCE.RADAR_USAGE.getAsDouble()));

	addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR,
		ScreenComponentSlot.IconType.SONAR_PROFILE, () -> {
		    List<FormattedCharSequence> info = new ArrayList<>();

		    TileSearchRadar radar = menu.getSafeHost();

		    if (radar == null) {
			return info;
		    }

		    info.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(ChatFormatting.DARK_GRAY)
			    .getVisualOrderText());
		    info.add(BallistixTextUtils
			    .tooltip("turret.maxrange",
				    ChatFormatter.formatDecimals(BallistixConfig.INSTANCE.RADAR_RANGE.getDefault(), 1)
					    .withStyle(ChatFormatting.GRAY))
			    .withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());

		    return info;

		}, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

	frequencyWrapper = new WrapperSearchFrequencyManager(this, -AbstractScreenComponentInfo.SIZE + 1,
		AbstractScreenComponentInfo.SIZE * 2 + 2, 0, 0);

	addComponent(slider = new ScreenComponentVerticalSlider(11, 80, 75)
		.setClickConsumer(frequencyWrapper.getSliderClickedConsumer())
		.setDragConsumer(frequencyWrapper.getSliderDraggedConsumer()));

	slider.setVisible(false);

	detectionsWrapper = new WrapperSearchRadarDetections(this, 0, 0);

	addComponent(detectionsSlider = new ScreenComponentVerticalSlider(10, 20, 130)
		.setClickConsumer(detectionsWrapper.getSliderClickedConsumer())
		.setDragConsumer(detectionsWrapper.getSliderDraggedConsumer()));

    }

    @Override
    protected void containerTick() {
	super.containerTick();
	frequencyWrapper.tick();
	detectionsWrapper.tick();
    }

    @Override
    protected void initializeComponents() {
	super.initializeComponents();
	playerInvLabel.setVisible(false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
	if (frequencyWrapper != null) {
	    if (scrollY > 0) {
		// scroll up
		frequencyWrapper.handleMouseScroll(-1);
	    } else if (scrollY < 0) {
		// scroll down
		frequencyWrapper.handleMouseScroll(1);
	    }
	}
	if (detectionsWrapper != null) {
	    if (scrollY > 0) {
		// scroll up
		detectionsWrapper.handleMouseScroll(-1);
	    } else if (scrollY < 0) {
		// scroll down
		detectionsWrapper.handleMouseScroll(1);
	    }
	}
	return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
	if ((slider != null && slider.isVisible()) || (detectionsSlider != null && detectionsSlider.isVisible())) {
	    slider.mouseClicked(mouseX, mouseY, button);
	}
	return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
	if ((slider != null && slider.isVisible()) || (detectionsSlider != null && detectionsSlider.isVisible())) {
	    slider.mouseReleased(mouseX, mouseY, button);
	}
	return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
	InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
	if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && frequencyWrapper.addEditBox.isVisible()
		&& frequencyWrapper.addEditBox.isFocused()) {
	    return false;
	}
	return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
	if (slider.isVisible()) {
	    return slider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	} else if (detectionsSlider.isVisible()) {
	    return detectionsSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}
	return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

}
