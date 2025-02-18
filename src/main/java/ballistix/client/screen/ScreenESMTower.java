package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.settings.Constants;
import ballistix.common.tile.TileESMTower;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentVerticalSlider;
import ballistix.prefab.screen.WrapperESMTowerDetections;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public class ScreenESMTower extends GenericScreen<ContainerESMTower> {

    public final ScreenComponentVerticalSlider slider;
    public final WrapperESMTowerDetections wrapper;

    public ScreenESMTower(ContainerESMTower container, Inventory inv, Component title) {
        super(container, inv, title);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.ESM_TOWER_USAGE_PER_TICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, BallistixIconTypes.SONAR_PROFILE, () -> {
            List<FormattedCharSequence> info = new ArrayList<>();

            TileESMTower radar = menu.getHostFromIntArray();

            if (radar == null) {
                return info;
            }

            info.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            info.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(Constants.ESM_TOWER_SEARCH_RADIUS, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());


            return info;

        }, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

        addComponent(new ScreenComponentBallistixLabel(25, 18, 10, ScreenComponentCustomRender.TEXT_GRAY, () -> {
            TileESMTower tower = menu.getHostFromIntArray();

            if(tower == null) {
                return Component.empty();
            }

            return tower.active.get() ? tower.searchRadarDetected.get() ? BallistixTextUtils.gui("esmtower.searchradardetected").withStyle(ChatFormatting.GREEN) : BallistixTextUtils.gui("esmtower.nosearchradars").withStyle(ChatFormatting.RED) : BallistixTextUtils.gui("esmtower.nosearchradars").withStyle(ChatFormatting.RED);

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
    protected void containerTick() {
        super.containerTick();
        wrapper.tick();
    }
    
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
    	if (wrapper != null) {
            if (pDelta > 0) {
                // scroll up
                wrapper.handleMouseScroll(-1);
            } else if (pDelta < 0) {
                // scroll down
                wrapper.handleMouseScroll(1);
            }
        }
    	return super.mouseScrolled(pMouseX, pMouseY, pDelta);
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
