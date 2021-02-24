package ballistix.common.tab;

import ballistix.DeferredRegisters;
import ballistix.common.block.SubtypeBlast;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupBallistix extends ItemGroup {

    public ItemGroupBallistix(String label) {
	super(label);
    }

    @Override
    public ItemStack createIcon() {
	return new ItemStack(DeferredRegisters.SUBTYPEBLOCK_MAPPINGS.get(SubtypeBlast.condensive));
    }
}