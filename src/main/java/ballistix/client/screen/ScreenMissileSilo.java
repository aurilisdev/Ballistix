package ballistix.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.tile.TileMissileSilo;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.ScreenComponentSimpleLabel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenMissileSilo extends GenericScreen<ContainerMissileSilo> {
	
	private boolean needsUpdate = true;
	
	private ScreenComponentEditBox xCoordField;
	private ScreenComponentEditBox yCoordField;
	private ScreenComponentEditBox zCoordField;
	private ScreenComponentEditBox frequencyField;
	
	public ScreenMissileSilo(ContainerMissileSilo container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addEditBox(xCoordField = new ScreenComponentEditBox(122, 10, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(6).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(yCoordField = new ScreenComponentEditBox(122, 28, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(6).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(zCoordField = new ScreenComponentEditBox(122, 46, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(6).setResponder(this::setZ).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(frequencyField = new ScreenComponentEditBox(122, 64, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(6).setResponder(this::setFrequency).setFilter(ScreenComponentEditBox.INTEGER));
		
		addComponent(new ScreenComponentSimpleLabel(inventoryLabelX, inventoryLabelY - 55.0f, 10, 4210752, BallistixTextUtils.gui("missilesilo.missile")));
		addComponent(new ScreenComponentSimpleLabel(inventoryLabelX, inventoryLabelY - 20.0f, 10, 4210752, BallistixTextUtils.gui("missilesilo.explosive")));
		addComponent(new ScreenComponentSimpleLabel(79, 13, 10, 4210752, BallistixTextUtils.gui("missilesilo.x")));
		addComponent(new ScreenComponentSimpleLabel(79, 31, 10, 4210752, BallistixTextUtils.gui("missilesilo.y")));
		addComponent(new ScreenComponentSimpleLabel(79, 49, 10, 4210752, BallistixTextUtils.gui("missilesilo.z")));
		addComponent(new ScreenComponentSimpleLabel(79, 67, 10, 4210752, BallistixTextUtils.gui("missilesilo.freq")));
	}

	private void setSiloTargetX(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		TileMissileSilo silo = menu.getHostFromIntArray();

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

		TileMissileSilo silo = menu.getHostFromIntArray();

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

		TileMissileSilo silo = menu.getHostFromIntArray();

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
	
	private void updateSiloCoords(int x, int y, int z, TileMissileSilo silo) {
		
		silo.target.set(new BlockPos(x, y, z));

		silo.target.updateServer();
		
	}

	private void setSiloFrequency(String val) {

		if (val.isEmpty()) {
			return;
		}

		TileMissileSilo silo = menu.getHostFromIntArray();

		if (silo == null) {
			return;
		}

		int frequency = 0;

		try {
			frequency = Integer.parseInt(val);
		} catch (Exception e) {
			// Filler
		}

		silo.frequency.set(frequency);
		
		silo.frequency.updateServer();

	}

	private void setFrequency(String val) {
		frequencyField.setFocus(true);
		xCoordField.setFocus(false);
		yCoordField.setFocus(false);
		zCoordField.setFocus(false);
		setSiloFrequency(val);
	}

	private void setX(String val) {
		xCoordField.setFocus(true);
		yCoordField.setFocus(false);
		zCoordField.setFocus(false);
		frequencyField.setFocus(false);
		setSiloTargetX(val);
	}

	private void setY(String val) {
		yCoordField.setFocus(true);
		xCoordField.setFocus(false);
		zCoordField.setFocus(false);
		frequencyField.setFocus(false);
		setSiloTargetY(val);
	}

	private void setZ(String val) {
		zCoordField.setFocus(true);
		yCoordField.setFocus(false);
		xCoordField.setFocus(false);
		frequencyField.setFocus(false);
		setSiloTargetZ(val);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if (needsUpdate) {
			needsUpdate = false;
			TileMissileSilo silo = menu.getHostFromIntArray();
			if (silo != null) {
				xCoordField.setValue("" + silo.target.get().getX());
				yCoordField.setValue("" + silo.target.get().getY());
				zCoordField.setValue("" + silo.target.get().getZ());
				frequencyField.setValue("" + silo.frequency.get());
			}
		}
	}

}