package ballistix.common.inventory.container;

import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.slot.item.type.SlotUpgrade;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.utilities.math.Color;

public class ContainerSAMTurret extends GenericContainerBlockEntity<TileTurretSAM> {

    public static final SubtypeItemUpgrade[] VALID_UPGRADES = { SubtypeItemUpgrade.range };

    public ContainerSAMTurret(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(4), new IntArray(3));
    }

    public ContainerSAMTurret(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_SAMTURRET.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {

        setPlayerInvOffset(10);

        addSlot(new SlotGeneric(inv, nextIndex(), 80, 20).setIOColor(new Color(0, 240, 255, 255)));

        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 14, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 37, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 60, VALID_UPGRADES));

    }
}
