package ballistix.common.inventory.container;

import ballistix.common.tile.silo.TileLauncherControlPanelT3;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerLauncherControlPanelT3 extends GenericContainerBlockEntity<TileLauncherControlPanelT3> {

	public ContainerLauncherControlPanelT3(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(1), new SimpleContainerData(3));
	}

	public ContainerLauncherControlPanelT3(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T3.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		//setPlayerInvOffset(20);
		addSlot(new SlotGeneric(inv, nextIndex(), 81, 51));
	}
}
