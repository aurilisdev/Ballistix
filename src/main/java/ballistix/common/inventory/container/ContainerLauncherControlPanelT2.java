package ballistix.common.inventory.container;

import ballistix.common.tile.silo.TileLauncherControlPanelT2;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerLauncherControlPanelT2 extends GenericContainerBlockEntity<TileLauncherControlPanelT2> {

	public ContainerLauncherControlPanelT2(int id, PlayerInventory playerinv) {
		this(id, playerinv, new Inventory(0), new IntArray(5));
	}

	public ContainerLauncherControlPanelT2(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
		super(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T2.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
	}
}
