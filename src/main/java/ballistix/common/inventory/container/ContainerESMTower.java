package ballistix.common.inventory.container;

import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerESMTower extends GenericContainerBlockEntity<TileESMTower> {

    public ContainerESMTower(int id, Inventory playerinv) {
        this(id, playerinv, new SimpleContainer(0), new SimpleContainerData(3));
    }

    public ContainerESMTower(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
        super(BallistixMenuTypes.CONTAINER_ESMTOWER.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container container, Inventory inventory) {

    }

    @Override
    public void addPlayerInventory(Inventory playerinv) {

    }
}
