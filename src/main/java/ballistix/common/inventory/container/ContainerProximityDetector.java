package ballistix.common.inventory.container;

import ballistix.common.tile.TileProximityDetector;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerProximityDetector extends GenericContainerBlockEntity<TileProximityDetector> {

    public ContainerProximityDetector(int id, Inventory playerinv) {
	this(id, playerinv, new SimpleContainer(0), new SimpleContainerData(3));
    }

    public ContainerProximityDetector(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
	super(BallistixMenuTypes.CONTAINER_PROXIMITYDETECTOR.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container container, Inventory inventory) {

    }

    @Override
    public void addPlayerInventory(Inventory playerinv) {

    }

    @Override
    public int getPlayerInvOffset() {
	return 10;
    }
}
