package ballistix.common.inventory.container;

import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerESMTower extends GenericContainerBlockEntity<TileESMTower> {

    public ContainerESMTower(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(0), new IntArray(5));
    }

    public ContainerESMTower(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_ESMTOWER.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory container, PlayerInventory inventory) {

    }

    @Override
    public void addPlayerInventory(PlayerInventory playerinv) {

    }
}
