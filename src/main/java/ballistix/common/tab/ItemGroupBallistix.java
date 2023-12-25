package ballistix.common.tab;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupBallistix extends ItemGroup {

	public ItemGroupBallistix(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(SubtypeBlast.antimatter).get());
	}
}