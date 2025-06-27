package ballistix.common.item;

import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import voltaic.common.item.ItemVoltaic;

import java.util.List;

public class ItemMissile extends ItemVoltaic {

	public final SubtypeMissile missile;

	public ItemMissile(SubtypeMissile missile) {
		super(new Item.Properties(), BallistixCreativeTabs.MAIN);
		this.missile = missile;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		tooltipComponents.add(BallistixTextUtils.tooltip("missile.maxbombtier", Component.literal("" + missile.tier()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
	}
}
