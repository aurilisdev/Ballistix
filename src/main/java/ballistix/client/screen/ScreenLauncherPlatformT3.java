package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerLauncherPlatformT3;
import electrodynamics.prefab.screen.GenericScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenLauncherPlatformT3 extends GenericScreen<ContainerLauncherPlatformT3> {

	public ScreenLauncherPlatformT3(ContainerLauncherPlatformT3 container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		imageHeight += 20;
		inventoryLabelY += 20;
	}

}