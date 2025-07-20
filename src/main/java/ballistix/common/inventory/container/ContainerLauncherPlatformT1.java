package ballistix.common.inventory.container;

import ballistix.common.tile.silo.TileLauncherPlatformT1;
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

public class ContainerLauncherPlatformT1 extends GenericContainerBlockEntity<TileLauncherPlatformT1> {

	public ContainerLauncherPlatformT1(int id, PlayerInventory playerinv) {
		this(id, playerinv, new Inventory(2), new IntArray(5));
	}

	public ContainerLauncherPlatformT1(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
		super(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T1.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
		//setPlayerInvOffset(20);
		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.MISSILE_DARK, inv, nextIndex(), 80, 25).setIOColor(new Color(0, 240, 255, 255)));
		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.EXPLOSIVE_DARK, inv, nextIndex(), 80, 45).setIOColor(new Color(0, 240, 255, 255)));
	}

}
