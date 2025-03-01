package ballistix.common.inventory.container;

import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.common.item.subtype.SubtypeItemUpgrade;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.inventory.container.slot.item.type.SlotUpgrade;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;

public class ContainerCIWSTurret extends GenericContainerBlockEntity<TileTurretCIWS> {

    public static final SubtypeItemUpgrade[] VALID_UPGRADES = { SubtypeItemUpgrade.range };

    public ContainerCIWSTurret(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(5), new IntArray(3));
    }

    public ContainerCIWSTurret(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_CIWSTURRET.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
        playerInvOffset = 10;

        addSlot(new SlotGeneric(inv, nextIndex(), 70, 20).setIOColor(new Color(0, 240, 255, 255)));
        addSlot(new SlotGeneric(inv, nextIndex(), 90, 20).setIOColor(new Color(0, 240, 255, 255)));

        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 14, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 37, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 60, VALID_UPGRADES));

    }
}
