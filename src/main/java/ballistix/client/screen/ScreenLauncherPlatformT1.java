package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerLauncherPlatformT1;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.wrapper.WrapperInventoryIO;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;

public class ScreenLauncherPlatformT1 extends GenericScreen<ContainerLauncherPlatformT1> {

	public ScreenLauncherPlatformT1(ContainerLauncherPlatformT1 container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
		//imageHeight += 20;
		//inventoryLabelY += 20;
		new WrapperInventoryIO(this, -AbstractScreenComponentInfo.SIZE + 1, 2, 75, 82, 8, 72);
	}

}