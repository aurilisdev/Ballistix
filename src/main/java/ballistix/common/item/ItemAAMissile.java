package ballistix.common.item;

import java.util.List;

import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.common.item.ItemVoltaic;

public class ItemAAMissile extends ItemVoltaic {
	private double accuracy;

	public ItemAAMissile(Properties properties, Holder<CreativeModeTab> creativeTab, double accuracy) {
		super(properties, creativeTab);
		this.accuracy = accuracy;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		tooltip.add(BallistixTextUtils.tooltip("aamissile.hitrate", ChatFormatter.getChatDisplayShort(accuracy * 100.0, DisplayUnits.PERCENTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
	}

}
