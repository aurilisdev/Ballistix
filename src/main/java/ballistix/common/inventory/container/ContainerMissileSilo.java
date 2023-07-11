package ballistix.common.inventory.container;

import ballistix.common.tile.TileMissileSilo;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerMissileSilo extends GenericContainerBlockEntity<TileMissileSilo> {

	public ContainerMissileSilo(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(2));
	}

	public ContainerMissileSilo(int id, Inventory playerinv, Container inventory) {
		this(id, playerinv, inventory, new SimpleContainerData(7));
	}

	public ContainerMissileSilo(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		addSlot(new SlotGeneric(inv, nextIndex(), 60, 17));
		addSlot(new SlotGeneric(inv, nextIndex(), 60, 50));
	}

}
