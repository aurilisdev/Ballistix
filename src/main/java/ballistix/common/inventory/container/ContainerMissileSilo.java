package ballistix.common.inventory.container;

import ballistix.common.tile.TileMissileSilo;
import ballistix.prefab.BallistixIconTypes;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.screen.component.types.ScreenComponentSlot;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class ContainerMissileSilo extends GenericContainerBlockEntity<TileMissileSilo> {

	public ContainerMissileSilo(int id, PlayerInventory playerinv) {
		this(id, playerinv, new Inventory(3));
	}

	public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory) {
		this(id, playerinv, inventory, new IntArray(3));
	}

	public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
		super(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
		playerInvOffset = 20;

		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.MISSILE_DARK, inv, nextIndex(), 90, 20).setIOColor(new Color(0, 240, 255, 255)));
		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.EXPLOSIVE_DARK, inv, nextIndex(), 90, 41).setIOColor(new Color(0, 240, 255, 255)));

		addSlot(new SlotGeneric(inv, nextIndex(), 90, 70));
	}

}
