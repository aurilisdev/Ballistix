package ballistix.common.inventory.container;

import ballistix.common.tile.TileVerticalLaunchSilo;
import ballistix.prefab.BallistixIconTypes;
import ballistix.registers.BallistixMenuTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.component.types.ScreenComponentSlot;
import voltaic.prefab.utilities.math.Color;

public class ContainerVLS extends GenericContainerBlockEntity<TileVerticalLaunchSilo> {

    public ContainerVLS(int id, PlayerInventory playerinv) {
        this(id, playerinv, new Inventory(3), new IntArray(5));
    }

    public ContainerVLS(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
        super(BallistixMenuTypes.CONTAINER_VLS.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory container, PlayerInventory inv) {
        addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.MISSILE_DARK, container, nextIndex(), 90, 58).setIOColor(new Color(0, 240, 255, 255)));
        addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.EXPLOSIVE_DARK, container, nextIndex(), 120, 58).setIOColor(new Color(0, 240, 255, 255)));
        addSlot(new SlotGeneric(container, nextIndex(), 81, 34));
    }
}
