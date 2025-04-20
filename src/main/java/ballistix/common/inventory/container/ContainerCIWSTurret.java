package ballistix.common.inventory.container;

import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.slot.item.type.SlotUpgrade;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.utilities.math.Color;

public class ContainerCIWSTurret extends GenericContainerBlockEntity<TileTurretCIWS> {

    public static final SubtypeItemUpgrade[] VALID_UPGRADES = { SubtypeItemUpgrade.range };

    public ContainerCIWSTurret(int id, Inventory playerinv) {
        this(id, playerinv, new SimpleContainer(5), new SimpleContainerData(3));
    }

    public ContainerCIWSTurret(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
        super(BallistixMenuTypes.CONTAINER_CIWSTURRET.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container inv, Inventory playerinv) {
        setPlayerInvOffset(10);

        addSlot(new SlotGeneric(inv, nextIndex(), 70, 20).setIOColor(new Color(0, 240, 255, 255)));
        addSlot(new SlotGeneric(inv, nextIndex(), 90, 20).setIOColor(new Color(0, 240, 255, 255)));

        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 14, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 37, VALID_UPGRADES));
        this.addSlot(new SlotUpgrade(inv, this.nextIndex(), 153, 60, VALID_UPGRADES));

    }
}
