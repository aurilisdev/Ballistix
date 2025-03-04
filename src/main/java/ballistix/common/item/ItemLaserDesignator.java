package ballistix.common.item;

import java.util.List;

import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.SiloRegistry;
import ballistix.common.tile.TileLauncherControlPanelT1;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import ballistix.registers.BallistixDataComponentTypes;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.math.MathUtils;
import electrodynamics.prefab.utilities.object.Location;
import electrodynamics.prefab.utilities.object.TransferPack;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemLaserDesignator extends ItemElectric {

	public static final double USAGE = 150.0;

	public ItemLaserDesignator() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), BallistixCreativeTabs.MAIN, item -> ElectrodynamicsItems.ITEM_BATTERY.get());
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		BlockEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
		TileLauncherControlPanelT1 silo = ent instanceof TileLauncherControlPanelT1 s ? s : null;
		if (ent instanceof TileMultiSubnode node) {
			BlockEntity core = node.getLevel().getBlockEntity(node.parentPos.get());
			if (core instanceof TileLauncherControlPanelT1 c) {
				silo = c;
			}
		}
		if (silo != null && !context.getLevel().isClientSide) {

			context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.setfrequency", silo.frequency.get()), false);

			stack.set(BallistixDataComponentTypes.BOUND_FREQUENCY, silo.frequency.get());

		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

		if (worldIn.isClientSide) {
			return super.use(worldIn, playerIn, handIn);
		}

		ItemStack designator = playerIn.getItemInHand(handIn);

		if (getJoulesStored(designator) < USAGE || !designator.has(BallistixDataComponentTypes.BOUND_FREQUENCY)) {
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

		int frequency = designator.get(BallistixDataComponentTypes.BOUND_FREQUENCY);

		int range;

		BlockPos target = trace.toBlockPos();

		double distance;

		for (TileLauncherControlPanelT1 silo : SiloRegistry.getSilos(frequency)) {

			if (!silo.launcherPlatform.valid())
				continue;
			range = silo.launcherPlatform.<ILauncherPlatform>getSafe().getRange();

			distance = TileLauncherControlPanelT1.calculateDistance(silo.getBlockPos(), target);

			if (range == 0 || (range > 0 && range < distance)) {
				continue;
			}

			silo.target.set(trace.toBlockPos());

			silo.shouldLaunch = true;

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
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		if (stack.has(BallistixDataComponentTypes.BOUND_FREQUENCY)) {
			tooltip.add(BallistixTextUtils.tooltip("laserdesignator.frequency", stack.get(BallistixDataComponentTypes.BOUND_FREQUENCY)).withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(BallistixTextUtils.tooltip("laserdesignator.nofrequency").withStyle(ChatFormatting.GRAY));
		}
	}

}
