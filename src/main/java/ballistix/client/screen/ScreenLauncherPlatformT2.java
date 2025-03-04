package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerLauncherPlatformT2;
import electrodynamics.prefab.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenLauncherPlatformT2 extends GenericScreen<ContainerLauncherPlatformT2> {

	public ScreenLauncherPlatformT2(ContainerLauncherPlatformT2 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		imageHeight += 20;
		inventoryLabelY += 20;
	}

}