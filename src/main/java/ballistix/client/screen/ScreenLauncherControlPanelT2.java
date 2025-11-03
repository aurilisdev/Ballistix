package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerLauncherControlPanelT2;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tile.silo.TileLauncherControlPanelT2;
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

public class ScreenLauncherControlPanelT2 extends GenericScreen<ContainerLauncherControlPanelT2> {

	private boolean needsUpdate = true;

	private final ScreenComponentEditBox xCoordField;
	private final ScreenComponentEditBox yCoordField;
	private final ScreenComponentEditBox zCoordField;

	public ScreenLauncherControlPanelT2(ContainerLauncherControlPanelT2 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);

		addComponent(new ScreenComponentElectricInfo(this::getElectricInformation, -AbstractScreenComponentInfo.SIZE + 1, 2).wattage(BallistixConfig.INSTANCE.MISSILESILO_USAGE.getAsDouble() * 20));

		addEditBox(xCoordField = new ScreenComponentEditBox(10, 17, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(yCoordField = new ScreenComponentEditBox(10, 35, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(zCoordField = new ScreenComponentEditBox(10, 53, 48, 15, getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(10).setResponder(this::setZ).setFilter(ScreenComponentEditBox.INTEGER));

		addComponent(new ScreenComponentSimpleLabel(60, 22, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.x")));
		addComponent(new ScreenComponentSimpleLabel(60, 40, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.y")));
		addComponent(new ScreenComponentSimpleLabel(60, 58, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.z")));

		addComponent(new ScreenComponentButton<>(100, 25, 40, 40).setOnPress(button -> {
			//
			TileLauncherControlPanelT2 silo = getMenu().getSafeHost();
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

		TileLauncherControlPanelT2 silo = menu.getSafeHost();

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

		TileLauncherControlPanelT2 silo = menu.getSafeHost();

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

	private void setSiloTargetZ(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		TileLauncherControlPanelT2 silo = menu.getSafeHost();

		if (silo == null) {
			return;
		}

		int z = silo.target.getValue().getZ();

		try {
			z = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(silo.target.getValue().getX(), silo.target.getValue().getY(), z, silo);

	}

	private static void updateSiloCoords(int x, int y, int z, TileLauncherControlPanelT2 silo) {
		silo.target.setValue(new BlockPos(x, y, z));
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
				xCoordField.setValue("" + silo.target.getValue().getX());
				yCoordField.setValue("" + silo.target.getValue().getY());
				zCoordField.setValue("" + silo.target.getValue().getZ());
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
		list.add(BallistixTextUtils.tooltip("missilesilo.charge", ChatFormatter.getChatDisplayShort(el.getJoulesStored(), DisplayUnits.JOULES).withStyle(ChatFormatting.GRAY), ChatFormatter.getChatDisplayShort(BallistixConfig.INSTANCE.MISSILESILO_USAGE.getAsDouble()*20*2, DisplayUnits.JOULES).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
		list.add(VoltaicTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(el.getVoltage(), DisplayUnits.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());

		return list;
	}

}