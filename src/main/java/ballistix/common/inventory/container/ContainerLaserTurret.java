package ballistix.common.inventory.container;

import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IntArray;

public class ContainerLaserTurret extends GenericContainerBlockEntity<TileTurretLaser> {

    public ContainerLaserTurret(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(0), new IntArray(3));
    }

    public ContainerLaserTurret(int id, PlayerInventory playerinv, IInventory inventory, IntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_LASERTURRET.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory container, PlayerInventory inventory) {
        playerInvOffset = 10;
    }
}
