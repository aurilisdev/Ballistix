package ballistix.common.inventory.container;

import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class ContainerFireControlRadar extends GenericContainerBlockEntity<TileFireControlRadar> {

    public ContainerFireControlRadar(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(0), new IntArray(3));
    }

    public ContainerFireControlRadar(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_FIRECONTROLRADAR.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory container, PlayerInventory inventory) {

    }

    @Override
    public void addPlayerInventory(PlayerInventory playerinv) {

    }
}
