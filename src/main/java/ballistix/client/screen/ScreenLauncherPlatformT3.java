package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerLauncherPlatformT3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.wrapper.WrapperInventoryIO;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;

public class ScreenLauncherPlatformT3 extends GenericScreen<ContainerLauncherPlatformT3> {

    public ScreenLauncherPlatformT3(ContainerLauncherPlatformT3 container, Inventory playerInventory, Component title) {
	super(container, playerInventory, title);
	// imageHeight += 20;
	// inventoryLabelY += 20;
	new WrapperInventoryIO(this, -AbstractScreenComponentInfo.SIZE + 1, 2, 75, 82, 8, 72);
    }

}