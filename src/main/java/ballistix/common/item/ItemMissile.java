package ballistix.common.item;

import java.util.List;

import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import voltaic.common.item.ItemVoltaic;

public class ItemMissile extends ItemVoltaic {

	public final SubtypeMissile missile;

	public ItemMissile(SubtypeMissile missile) {
		super(new Item.Properties(), () -> BallistixCreativeTabs.MAIN);
		this.missile = missile;
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, World pLevel, List<ITextComponent> tooltipComponents, ITooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, tooltipComponents, pIsAdvanced);
		tooltipComponents.add(BallistixTextUtils.tooltip("missile.maxbombtier", new StringTextComponent("" + missile.tier()).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY));
	}
	
}
