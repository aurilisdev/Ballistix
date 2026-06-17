package ballistix.common.inventory.container;

import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;

public class ContainerLaserTurret extends GenericContainerBlockEntity<TileTurretLaser> {

    public ContainerLaserTurret(int id, Inventory playerinv) {
	this(id, playerinv, new SimpleContainer(0), new SimpleContainerData(5));
    }

    public ContainerLaserTurret(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
	super(BallistixMenuTypes.CONTAINER_LASERTURRET.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container container, Inventory inventory) {
	setPlayerInvOffset(10);
    }
}
