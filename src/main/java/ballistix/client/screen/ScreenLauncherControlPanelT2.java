package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerLauncherControlPanelT2;
import ballistix.common.settings.Constants;
import ballistix.common.tile.TileLauncherControlPanelT2;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.ScreenComponentFillArea;
import electrodynamics.prefab.screen.component.types.ScreenComponentSimpleLabel;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public class ScreenLauncherControlPanelT2 extends GenericScreen<ContainerLauncherControlPanelT2> {

	private boolean needsUpdate = true;

	private final ScreenComponentEditBox xCoordField;
	private final ScreenComponentEditBox yCoordField;
	private final ScreenComponentEditBox zCoordField;

	public ScreenLauncherControlPanelT2(ContainerLauncherControlPanelT2 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);

		imageHeight += 20;
		inventoryLabelY += 20;

		addComponent(new ScreenComponentElectricInfo(this::getElectricInformation, -AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.MISSILESILO_USAGE * 20));

		addEditBox(xCoordField = new ScreenComponentEditBox(10, 20, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(yCoordField = new ScreenComponentEditBox(10, 38, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(zCoordField = new ScreenComponentEditBox(10, 56, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setZ).setFilter(ScreenComponentEditBox.INTEGER));

		addComponent(new ScreenComponentSimpleLabel(60, 22, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.x")));
		addComponent(new ScreenComponentSimpleLabel(60, 40, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.y")));
		addComponent(new ScreenComponentSimpleLabel(60, 58, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.z")));
	}

	@Override
	protected void initializeComponents() {
		super.initializeComponents();
	}

	private void setSiloTargetX(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		TileLauncherControlPanelT2 silo = menu.getSafeHost();

		if (silo == null) {
			return;
		}

		int x = silo.target.get().getX();

		try {
			x = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(x, silo.target.get().getY(), silo.target.get().getZ(), silo);

	}

	private void setSiloTargetY(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		TileLauncherControlPanelT2 silo = menu.getSafeHost();

		if (silo == null) {
			return;
		}

		int y = silo.target.get().getY();

		try {
			y = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(silo.target.get().getX(), y, silo.target.get().getZ(), silo);

	}

	private void setSiloTargetZ(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		TileLauncherControlPanelT2 silo = menu.getSafeHost();

		if (silo == null) {
			return;
		}

		int z = silo.target.get().getZ();

		try {
			z = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(silo.target.get().getX(), silo.target.get().getY(), z, silo);

	}

	private void updateSiloCoords(int x, int y, int z, TileLauncherControlPanelT2 silo) {

		silo.target.set(new BlockPos(x, y, z));

	}

	private void setX(String val) {
		xCoordField.setFocus(true);
		yCoordField.setFocus(false);
		zCoordField.setFocus(false);
		setSiloTargetX(val);
	}

	private void setY(String val) {
		yCoordField.setFocus(true);
		xCoordField.setFocus(false);
		zCoordField.setFocus(false);
		setSiloTargetY(val);
	}

	private void setZ(String val) {
		zCoordField.setFocus(true);
		yCoordField.setFocus(false);
		xCoordField.setFocus(false);
		setSiloTargetZ(val);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		if (needsUpdate) {
			needsUpdate = false;
			TileLauncherControlPanelT2 silo = menu.getSafeHost();
			if (silo != null) {
				xCoordField.setValue("" + silo.target.get().getX());
				yCoordField.setValue("" + silo.target.get().getY());
				zCoordField.setValue("" + silo.target.get().getZ());
			}
		}
	}

	private List<? extends FormattedCharSequence> getElectricInformation() {
		ArrayList<FormattedCharSequence> list = new ArrayList<>();

		TileLauncherControlPanelT2 silo = menu.getSafeHost();
		if (silo == null) {
			return list;
		}

		ComponentElectrodynamic el = silo.getComponent(IComponentType.Electrodynamic);
		list.add(BallistixTextUtils.tooltip("missilesilo.charge", ChatFormatter.getChatDisplayShort(el.getJoulesStored(), DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY), ChatFormatter.getChatDisplayShort(Constants.MISSILESILO_USAGE, DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
		list.add(ElectroTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(el.getVoltage(), DisplayUnit.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());

		return list;
	}

}