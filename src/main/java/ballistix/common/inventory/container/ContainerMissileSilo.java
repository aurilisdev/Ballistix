package ballistix.common.inventory.container;

import ballistix.common.tile.TileMissileSilo;
import ballistix.prefab.BallistixIconTypes;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.screen.component.types.ScreenComponentSlot;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerMissileSilo extends GenericContainerBlockEntity<TileMissileSilo> {

	public ContainerMissileSilo(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(3), new SimpleContainerData(3));
	}

	public ContainerMissileSilo(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		playerInvOffset = 20;
		
		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.MISSILE_DARK, inv, nextIndex(), 90, 20).setIOColor(new Color(0, 240, 255, 255)));
		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.EXPLOSIVE_DARK, inv, nextIndex(), 90, 41).setIOColor(new Color(0, 240, 255, 255)));

		addSlot(new SlotGeneric(inv, nextIndex(), 90, 70));
	}

}
