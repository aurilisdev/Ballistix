package ballistix.common.item;

import java.util.List;

import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.SiloRegistry;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.math.MathUtils;
import voltaic.prefab.utilities.object.Location;
import voltaic.prefab.utilities.object.TransferPack;

public class ItemLaserDesignator extends ItemElectric {

	public static final double USAGE = 150.0;
	
	public static final String FREQUENCY_KEY = "freq";

	public ItemLaserDesignator() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), BallistixCreativeTabs.MAIN, item -> Items.AIR);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		BlockEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
		TileLauncherControlPanelT1 silo = ent instanceof TileLauncherControlPanelT1 s ? s : null;
		if (ent instanceof TileMultiSubnode node) {
			BlockEntity core = node.getLevel().getBlockEntity(node.parentPos.getValue());
			if (core instanceof TileLauncherControlPanelT1 c) {
				silo = c;
			}
		}
		if (silo != null && !context.getLevel().isClientSide) {

			context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.setfrequency", silo.frequency.getValue()), false);

			CompoundTag nbt = stack.getOrCreateTag();
			nbt.putInt(FREQUENCY_KEY, silo.frequency.getValue());

		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

		if (worldIn.isClientSide) {
			return super.use(worldIn, playerIn, handIn);
		}

		ItemStack designator = playerIn.getItemInHand(handIn);

		if (getJoulesStored(designator) < USAGE || !designator.getOrCreateTag().contains(FREQUENCY_KEY)) {
			return super.use(worldIn, playerIn, handIn);
		}

		Location trace = MathUtils.getRaytracedBlock(playerIn);

		if (trace == null) {
			return super.use(worldIn, playerIn, handIn);
		}

		BlockEntity tile = trace.getTile(worldIn);

		// fixes bug of blowing self up
		if (tile instanceof TileLauncherControlPanelT1 || tile instanceof TileMultiSubnode) {
			return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
		}

		int frequency = getFrequency(designator);

		int range;

		BlockPos target = trace.toBlockPos();

		double distance;

		for (ILauncherControlPanel silo : SiloRegistry.getSilos(frequency, worldIn)) {

			if (!silo.getPlatform().valid() || silo.getTier() < 3)
				continue;
			ILauncherPlatform platform = silo.getPlatform().<ILauncherPlatform>getSafe();
			if (platform == null)
				continue;
			range = platform.getRange();
			distance = TileLauncherControlPanelT1.calculateDistance(silo.getPos(), target);

			if (range == 0 || (range > 0 && range < distance) || distance > BallistixConstants.LASER_DESIGNATOR_RANGE) {
				continue;
			}

			silo.setTarget(trace.toBlockPos());

			silo.launch();

			extractPower(designator, USAGE, false);

		}

		playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launch", frequency), false);
		playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launchsend", trace.toString()), false);

		return super.use(worldIn, playerIn, handIn);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		if (!worldIn.isClientSide || !isSelected) {
			return;
		}

		Location trace = MathUtils.getRaytracedBlock(entityIn);

		if (trace == null) {
			return;
		}

		if (entityIn instanceof Player player) {
			player.displayClientMessage(BallistixTextUtils.chatMessage("radargun.text", trace.toBlockPos().toShortString()), true);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		if (stack.hasTag() && stack.getTag().contains(FREQUENCY_KEY)) {
			tooltip.add(BallistixTextUtils.tooltip("laserdesignator.frequency", getFrequency(stack)).withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(BallistixTextUtils.tooltip("laserdesignator.nofrequency").withStyle(ChatFormatting.GRAY));
		}
	}
	
	public static int getFrequency(ItemStack stack) {
		return stack.getOrCreateTag().getInt(FREQUENCY_KEY);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.is(newStack.getItem());
	}

}
