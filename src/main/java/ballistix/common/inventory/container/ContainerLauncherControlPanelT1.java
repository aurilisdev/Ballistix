package ballistix.common.inventory.container;

import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerLauncherControlPanelT1 extends GenericContainerBlockEntity<TileLauncherControlPanelT1> {

	public ContainerLauncherControlPanelT1(int id, PlayerInventory playerinv) {
		this(id, playerinv, new Inventory(0), new IntArray(3));
	}

	public ContainerLauncherControlPanelT1(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
		super(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T1.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(IInventory arg0, PlayerInventory arg1) {
	}

}
