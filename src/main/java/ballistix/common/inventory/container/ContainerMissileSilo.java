package ballistix.common.inventory.container;

import ballistix.common.tile.TileMissileSilo;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class ContainerMissileSilo extends GenericContainerBlockEntity<TileMissileSilo> {

	public ContainerMissileSilo(int id, PlayerInventory playerinv) {
		this(id, playerinv, new Inventory(2));
	}

	public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory) {
		this(id, playerinv, inventory, new IntArray(7));
	}

	public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
		super(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
		addSlot(new SlotGeneric(inv, nextIndex(), 60, 17));
		addSlot(new SlotGeneric(inv, nextIndex(), 60, 50));
	}

}
