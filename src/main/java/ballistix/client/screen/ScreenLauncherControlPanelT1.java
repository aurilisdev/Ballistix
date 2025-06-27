package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerLauncherControlPanelT1;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.button.ScreenComponentButton;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenLauncherControlPanelT1 extends GenericScreen<ContainerLauncherControlPanelT1> {

	private boolean needsUpdate = true;

	private final ScreenComponentEditBox xCoordField;
	private final ScreenComponentEditBox zCoordField;

	public ScreenLauncherControlPanelT1(ContainerLauncherControlPanelT1 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);


		addComponent(new ScreenComponentElectricInfo(this::getElectricInformation, -AbstractScreenComponentInfo.SIZE + 1, 2).wattage(BallistixConstants.MISSILESILO_USAGE * 20));

		addEditBox(xCoordField = new ScreenComponentEditBox(10, 28, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(zCoordField = new ScreenComponentEditBox(10, 46, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));

		addComponent(new ScreenComponentSimpleLabel(60, 32, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.x")));
		addComponent(new ScreenComponentSimpleLabel(60, 50, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.z")));

		addComponent(new ScreenComponentButton<>(100, 25, 40, 40).setOnPress(button -> {
			//
			TileLauncherControlPanelT1 silo = getMenu().getSafeHost();
			if(silo == null) {
				return;
			}
			silo.shouldLaunch.setValue(true);

		}).setColor(new Color(255, 0, 0, 255)).onTooltip((graphics, component, mouseX, mouseY) -> graphics.renderTooltip(getFontRenderer(), BallistixTextUtils.tooltip("silo.launch"), mouseX, mouseY)));
	}

	@Override
	protected void initializeComponents() {
		super.initializeComponents();
	}

	private void setSiloTargetX(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		TileLauncherControlPanelT1 silo = menu.getSafeHost();

		if (silo == null) {
			return;
		}

		int x = silo.target.getValue().getX();

		try {
			x = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(x, silo.target.getValue().getY(), silo.target.getValue().getZ(), silo);

	}

	private void setSiloTargetY(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		TileLauncherControlPanelT1 silo = menu.getSafeHost();

		if (silo == null) {
			return;
		}

		int y = silo.target.getValue().getY();

		try {
			y = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(silo.target.getValue().getX(), y, silo.target.getValue().getZ(), silo);

	}

	private static void updateSiloCoords(int x, int y, int z, TileLauncherControlPanelT1 silo) {
		silo.target.setValue(new BlockPos(x, y, z));
	}

	private void setX(String val) {
		xCoordField.setFocus(true);
		zCoordField.setFocus(false);
		setSiloTargetX(val);
	}

	private void setY(String val) {
		zCoordField.setFocus(true);
		xCoordField.setFocus(false);
		setSiloTargetY(val);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		if (needsUpdate) {
			needsUpdate = false;
			TileLauncherControlPanelT1 silo = menu.getSafeHost();
			if (silo != null) {
				xCoordField.setValue("" + silo.target.getValue().getX());
				zCoordField.setValue("" + silo.target.getValue().getY());
			}
		}
	}

	private List<? extends FormattedCharSequence> getElectricInformation() {
		ArrayList<FormattedCharSequence> list = new ArrayList<>();

		TileLauncherControlPanelT1 silo = menu.getSafeHost();
		if (silo == null) {
			return list;
		}

		ComponentElectrodynamic el = silo.getComponent(IComponentType.Electrodynamic);
		list.add(BallistixTextUtils.tooltip("missilesilo.charge", ChatFormatter.getChatDisplayShort(el.getJoulesStored(), DisplayUnits.JOULES).withStyle(ChatFormatting.GRAY), ChatFormatter.getChatDisplayShort(BallistixConstants.MISSILESILO_USAGE, DisplayUnits.JOULES).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
		list.add(VoltaicTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(el.getVoltage(), DisplayUnits.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());

		return list;
	}

}