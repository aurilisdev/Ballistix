package ballistix.common.item;

import java.util.List;
import java.util.function.Supplier;

import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.common.item.ItemVoltaic;

public class ItemAAMissile extends ItemVoltaic {
	private double accuracy;

	public ItemAAMissile(Properties properties, Supplier<ItemGroup> creativeTab, double accuracy) {
		super(properties, creativeTab);
		this.accuracy = accuracy;
	}

	@Override
	public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		tooltip.add(BallistixTextUtils.tooltip("aamissile.hitrate", ChatFormatter.getChatDisplayShort(accuracy * 100.0, DisplayUnits.PERCENTAGE).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY));
	}

}
