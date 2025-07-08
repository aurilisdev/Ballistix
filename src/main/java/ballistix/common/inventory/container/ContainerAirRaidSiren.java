package ballistix.common.inventory.container;

import ballistix.common.tile.TileAirRaidSiren;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerAirRaidSiren extends GenericContainerBlockEntity<TileAirRaidSiren> {

    public ContainerAirRaidSiren(int id, PlayerInventory playerinv) {
       this(id, playerinv, new Inventory(0), new IntArray(3));
    }

    public ContainerAirRaidSiren(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_AIRRAIDSIREN.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory container, PlayerInventory inventory) {

    }

    @Override
    public void addPlayerInventory(PlayerInventory playerinv) {

    }
}
