package ballistix.common.tab;

import ballistix.DeferredRegisters;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupBallistix extends CreativeModeTab {

    public ItemGroupBallistix(String label) {
	super(label);
    }

    @Override
    public ItemStack makeIcon() {
	return new ItemStack(DeferredRegisters.SUBTYPEBLOCK_MAPPINGS.get(SubtypeBlast.condensive));
    }
}