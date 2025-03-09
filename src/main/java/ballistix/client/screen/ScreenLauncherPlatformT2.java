package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerLauncherPlatformT2;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.wrapper.WrapperInventoryIO;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenLauncherPlatformT2 extends GenericScreen<ContainerLauncherPlatformT2> {

	public ScreenLauncherPlatformT2(ContainerLauncherPlatformT2 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		//imageHeight += 20;
		//inventoryLabelY += 20;
		new WrapperInventoryIO(this, -AbstractScreenComponentInfo.SIZE + 1, 2, 75, 82, 8, 72);
	}

}