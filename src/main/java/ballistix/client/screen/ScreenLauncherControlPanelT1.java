package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerLauncherControlPanelT1;
import ballistix.common.settings.Constants;
import ballistix.common.tile.TileLauncherControlPanelT1;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
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

public class ScreenLauncherControlPanelT1 extends GenericScreen<ContainerLauncherControlPanelT1> {

	private boolean needsUpdate = true;

	private final ScreenComponentEditBox xCoordField;
	private final ScreenComponentEditBox yCoordField;

	public ScreenLauncherControlPanelT1(ContainerLauncherControlPanelT1 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);


		addComponent(new ScreenComponentElectricInfo(this::getElectricInformation, -AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.MISSILESILO_USAGE * 20));

		addEditBox(xCoordField = new ScreenComponentEditBox(10, 20, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(yCoordField = new ScreenComponentEditBox(10, 38, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));

		addComponent(new ScreenComponentSimpleLabel(60, 22, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.x")));
		addComponent(new ScreenComponentSimpleLabel(60, 40, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.y")));
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

		TileLauncherControlPanelT1 silo = menu.getSafeHost();

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

	private void updateSiloCoords(int x, int y, int z, TileLauncherControlPanelT1 silo) {
		silo.target.set(new BlockPos(x, y, z));

	}

	private void setX(String val) {
		xCoordField.setFocus(true);
		yCoordField.setFocus(false);
		setSiloTargetX(val);
	}

	private void setY(String val) {
		yCoordField.setFocus(true);
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
				xCoordField.setValue("" + silo.target.get().getX());
				yCoordField.setValue("" + silo.target.get().getY());
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
		list.add(BallistixTextUtils.tooltip("missilesilo.charge", ChatFormatter.getChatDisplayShort(el.getJoulesStored(), DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY), ChatFormatter.getChatDisplayShort(Constants.MISSILESILO_USAGE, DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
		list.add(ElectroTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(el.getVoltage(), DisplayUnit.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());

		return list;
	}

}