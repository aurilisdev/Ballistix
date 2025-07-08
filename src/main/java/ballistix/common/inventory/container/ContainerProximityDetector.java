package ballistix.common.inventory.container;

import ballistix.common.tile.TileProximityDetector;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerProximityDetector extends GenericContainerBlockEntity<TileProximityDetector> {


    public ContainerProximityDetector(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(0), new IntArray(3));
    }

    public ContainerProximityDetector(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_PROXIMITYDETECTOR.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory container, PlayerInventory inventory) {

    }

    @Override
    public void addPlayerInventory(PlayerInventory playerinv) {

    }

    @Override
    public int getPlayerInvOffset() {
        return 10;
    }
}
