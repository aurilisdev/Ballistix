package ballistix.common.item;

import java.util.List;

import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.SiloRegistry;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), () -> BallistixCreativeTabs.MAIN);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		TileEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
		ILauncherControlPanel silo = ent instanceof ILauncherControlPanel ? (ILauncherControlPanel) ent : null;
		if (ent instanceof TileMultiSubnode) {
			TileEntity core = ((TileMultiSubnode) ent).getLevel().getBlockEntity(((TileMultiSubnode) ent).parentPos.getValue());
			if (core instanceof ILauncherControlPanel) {
				silo = (ILauncherControlPanel) core;
			}
		}
		if (silo != null && !context.getLevel().isClientSide) {

			context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.setfrequency", silo.getFrequency()), false);

			CompoundNBT nbt = stack.getOrCreateTag();
			nbt.putInt(FREQUENCY_KEY, silo.getFrequency());

		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {

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

		TileEntity tile = trace.getTile(worldIn);

		// fixes bug of blowing self up
		if (tile instanceof ILauncherControlPanel || tile instanceof TileMultiSubnode) {
			return ActionResult.pass(playerIn.getItemInHand(handIn));
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
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		if (!worldIn.isClientSide || !isSelected) {
			return;
		}

		Location trace = MathUtils.getRaytracedBlock(entityIn);

		if (trace == null) {
			return;
		}

		if (entityIn instanceof PlayerEntity) {
			((PlayerEntity) entityIn).displayClientMessage(BallistixTextUtils.chatMessage("radargun.text", trace.toBlockPos().toShortString()), true);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		if (stack.hasTag() && stack.getTag().contains(FREQUENCY_KEY)) {
			tooltip.add(BallistixTextUtils.tooltip("laserdesignator.frequency", getFrequency(stack)).withStyle(TextFormatting.GRAY));
		} else {
			tooltip.add(BallistixTextUtils.tooltip("laserdesignator.nofrequency").withStyle(TextFormatting.GRAY));
		}
	}
	
	public static int getFrequency(ItemStack stack) {
		return stack.getOrCreateTag().getInt(FREQUENCY_KEY);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

}
