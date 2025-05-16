package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.TileESMTower;
import ballistix.prefab.screen.WrapperESMTowerDetections;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.ScreenComponentSlot;
import voltaic.prefab.screen.component.types.ScreenComponentVerticalSlider;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenESMTower extends GenericScreen<ContainerESMTower> {

	public final ScreenComponentVerticalSlider slider;
	public final WrapperESMTowerDetections wrapper;

	public ScreenESMTower(ContainerESMTower container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(BallistixConstants.ESM_TOWER_USAGE_PER_TICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, ScreenComponentSlot.IconType.SONAR_PROFILE, () -> {
            List<IReorderingProcessor> info = new ArrayList<>();

            TileESMTower radar = menu.getSafeHost();

            if (radar == null) {
                return info;
            }

            info.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            info.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(BallistixConstants.ESM_TOWER_SEARCH_RADIUS, 1).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());


            return info;

        }, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

        addComponent(new ScreenComponentSimpleLabel(25, 18, 10, Color.TEXT_GRAY, () -> {
            TileESMTower tower = menu.getSafeHost();

            if(tower == null) {
                return VoltaicTextUtils.empty();
            }

            return tower.active.getValue() ? tower.searchRadarDetected.getValue() ? BallistixTextUtils.gui("esmtower.searchradardetected").withStyle(TextFormatting.GREEN) : BallistixTextUtils.gui("esmtower.nosearchradars").withStyle(TextFormatting.RED) : BallistixTextUtils.gui("esmtower.nosearchradars").withStyle(TextFormatting.RED);

        }));

        wrapper = new WrapperESMTowerDetections(this, 0, 0);

        addComponent(slider = new ScreenComponentVerticalSlider(11, 40, 120).setClickConsumer(wrapper.getSliderClickedConsumer()).setDragConsumer(wrapper.getSliderDraggedConsumer()));


    }

	@Override
	protected void initializeComponents() {
		super.initializeComponents();
		playerInvLabel.setVisible(false);
	}

	@Override
	public void tick() {
		super.tick();
		wrapper.tick();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
		if (wrapper != null) {
			if (scrollY > 0) {
				// scroll up
				wrapper.handleMouseScroll(-1);
			} else if (scrollY < 0) {
				// scroll down
				wrapper.handleMouseScroll(1);
			}
		}
		return super.mouseScrolled(mouseX, mouseY, scrollY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (slider != null && slider.isVisible()) {
			slider.mouseClicked(mouseX, mouseY, button);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (slider != null && slider.isVisible()) {
			slider.mouseReleased(mouseX, mouseY, button);
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (slider.isVisible()) {
			return slider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

}
