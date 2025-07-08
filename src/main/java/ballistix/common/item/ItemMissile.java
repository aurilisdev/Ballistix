package ballistix.common.item;

import java.util.List;

import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import voltaic.common.item.ItemVoltaic;

public class ItemMissile extends ItemVoltaic {

	public final SubtypeMissile missile;

	public ItemMissile(SubtypeMissile missile) {
		super(new Item.Properties(), () -> BallistixCreativeTabs.MAIN);
		this.missile = missile;
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> tooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, tooltipComponents, pIsAdvanced);
		if(missile != SubtypeMissile.clustershard) {
			tooltipComponents.add(BallistixTextUtils.tooltip("missile.maxbombtier", new TextComponent("" + missile.tier()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		if(missile != SubtypeMissile.clustershard) {
			super.fillItemCategory(tab, items);
		}
	}
	
}
