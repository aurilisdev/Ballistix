package ballistix.common.item;

import java.util.List;

import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import voltaic.common.item.ItemVoltaic;

public class ItemMissile extends ItemVoltaic {

	public final SubtypeMissile missile;

	public ItemMissile(SubtypeMissile missile) {
		super(new Item.Properties(), BallistixCreativeTabs.MAIN);
		this.missile = missile;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		if(missile != SubtypeMissile.clustershard) {
			tooltipComponents.add(BallistixTextUtils.tooltip("missile.maxbombtier", Component.literal("" + missile.tier()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	@Override
	public void addCreativeModeItems(CreativeModeTab tab, List<ItemStack> items) {
		if(missile != SubtypeMissile.clustershard) {
			super.addCreativeModeItems(tab, items);
		}
	}
}
