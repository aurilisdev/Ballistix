package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerLauncherPlatformT1;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.wrapper.WrapperInventoryIO;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenLauncherPlatformT1 extends GenericScreen<ContainerLauncherPlatformT1> {

	public ScreenLauncherPlatformT1(ContainerLauncherPlatformT1 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		//imageHeight += 20;
		//inventoryLabelY += 20;
		new WrapperInventoryIO(this, -AbstractScreenComponentInfo.SIZE + 1, 2, 75, 82, 8, 72);
	}

}