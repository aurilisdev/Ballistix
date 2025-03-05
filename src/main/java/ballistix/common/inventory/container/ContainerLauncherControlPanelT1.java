package ballistix.common.inventory.container;

import ballistix.common.tile.TileLauncherControlPanelT1;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.types.GenericContainerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerLauncherControlPanelT1 extends GenericContainerBlockEntity<TileLauncherControlPanelT1> {

	public ContainerLauncherControlPanelT1(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(0), new SimpleContainerData(3));
	}

	public ContainerLauncherControlPanelT1(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T1.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(Container arg0, Inventory arg1) {
	}

}
