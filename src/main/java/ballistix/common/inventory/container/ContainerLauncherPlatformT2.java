package ballistix.common.inventory.container;

import ballistix.common.tile.TileLauncherPlatformT2;
import ballistix.prefab.BallistixIconTypes;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.inventory.container.types.GenericContainerBlockEntity;
import electrodynamics.prefab.screen.component.types.ScreenComponentSlot;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerLauncherPlatformT2 extends GenericContainerBlockEntity<TileLauncherPlatformT2> {

	public ContainerLauncherPlatformT2(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(2), new SimpleContainerData(3));
	}

	public ContainerLauncherPlatformT2(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T2.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		setPlayerInvOffset(20);
		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.MISSILE_DARK, inv, nextIndex(), 80, 30).setIOColor(new Color(0, 240, 255, 255)));
		addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.EXPLOSIVE_DARK, inv, nextIndex(), 80, 51).setIOColor(new Color(0, 240, 255, 255)));
	}

}
