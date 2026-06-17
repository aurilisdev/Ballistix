package ballistix.common.inventory.container;

import ballistix.common.tile.TileVerticalLaunchSilo;
import ballistix.prefab.BallistixIconTypes;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.component.types.ScreenComponentSlot;
import voltaic.prefab.utilities.math.Color;

public class ContainerVLS extends GenericContainerBlockEntity<TileVerticalLaunchSilo> {

    public ContainerVLS(int id, Inventory playerinv) {
	this(id, playerinv, new SimpleContainer(3), new SimpleContainerData(3));
    }

    public ContainerVLS(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
	super(BallistixMenuTypes.CONTAINER_VLS.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container container, Inventory inv) {
	addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.MISSILE_DARK, container,
		nextIndex(), 90, 58).setIOColor(new Color(0, 240, 255, 255)));
	addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.EXPLOSIVE_DARK, container,
		nextIndex(), 120, 58).setIOColor(new Color(0, 240, 255, 255)));
	addSlot(new SlotGeneric(container, nextIndex(), 81, 34));
    }
}
