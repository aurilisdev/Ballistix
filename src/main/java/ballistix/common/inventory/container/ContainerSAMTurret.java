package ballistix.common.inventory.container;

import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.common.item.subtype.SubtypeItemUpgrade;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.inventory.container.slot.item.type.SlotUpgrade;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerSAMTurret extends GenericContainerBlockEntity<TileTurretSAM> {

    public static final SubtypeItemUpgrade[] VALID_UPGRADES = { SubtypeItemUpgrade.range };

    public ContainerSAMTurret(int id, Inventory playerinv) {
        this(id, playerinv, new SimpleContainer(4), new SimpleContainerData(3));
    }

    public ContainerSAMTurret(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
        super(BallistixMenuTypes.CONTAINER_SAMTURRET.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container inv, Inventory playerinv) {

        playerInvOffset = 10;

        addSlot(new SlotGeneric(inv, nextIndex(), 80, 20).setIOColor(new Color(0, 240, 255, 255)));

        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 14, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 37, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 60, VALID_UPGRADES));

    }
}
