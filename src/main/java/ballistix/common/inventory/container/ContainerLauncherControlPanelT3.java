package ballistix.common.inventory.container;

import ballistix.common.tile.silo.TileLauncherControlPanelT3;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerLauncherControlPanelT3 extends GenericContainerBlockEntity<TileLauncherControlPanelT3> {

	public ContainerLauncherControlPanelT3(int id, PlayerInventory playerinv) {
		this(id, playerinv, new Inventory(1), new IntArray(3));
	}

	public ContainerLauncherControlPanelT3(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
		super(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T3.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
		//setPlayerInvOffset(20);
		addSlot(new SlotGeneric(inv, nextIndex(), 81, 51));
	}
}
