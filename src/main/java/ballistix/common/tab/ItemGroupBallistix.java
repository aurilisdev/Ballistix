package ballistix.common.tab;

import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.registers.BallistixItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupBallistix extends ItemGroup {

	public ItemGroupBallistix(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2));
	}
}