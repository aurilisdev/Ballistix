package nuclearscience.common.tab;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemGroupBallistix extends ItemGroup {

	public ItemGroupBallistix(String label) {
		super(label);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Items.TNT);
	}
}