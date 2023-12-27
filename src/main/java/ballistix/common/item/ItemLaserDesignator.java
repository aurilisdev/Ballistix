package ballistix.common.item;

import java.util.List;

import ballistix.References;
import ballistix.common.network.SiloRegistry;
import ballistix.common.tile.TileMissileSilo;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.item.ElectricItemProperties;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.math.MathUtils;
import electrodynamics.prefab.utilities.object.Location;
import electrodynamics.prefab.utilities.object.TransferPack;
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
import net.minecraft.world.World;

public class ItemLaserDesignator extends ItemElectric {

	public static final double USAGE = 150.0;
	
	public static final String FREQUENCY_KEY = "freq";
	
	public ItemLaserDesignator() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		TileEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
		TileMissileSilo silo = ent instanceof TileMissileSilo ? (TileMissileSilo) ent : null;
		if (ent instanceof TileMultiSubnode) {
			TileMultiSubnode node = (TileMultiSubnode) ent;
			TileEntity core = node.getLevel().getBlockEntity(node.parentPos.get().toBlockPos());
			if (core instanceof TileMissileSilo) {
				silo = (TileMissileSilo) core;
			}
		}
		if (silo != null) {

			if (context.getLevel().isClientSide) {
				context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.setfrequency", silo.frequency.get()), false);
			} else {
				CompoundNBT nbt = stack.getOrCreateTag();
				nbt.putInt(FREQUENCY_KEY, silo.frequency.get());
			}

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
		if (tile instanceof TileMissileSilo || tile instanceof TileMultiSubnode) {
			return ActionResult.pass(playerIn.getItemInHand(handIn));
		}

		int frequency = getFrequency(designator);

		int range;

		BlockPos target = trace.toBlockPos();

		double distance;

		for (TileMissileSilo silo : SiloRegistry.getSilos(frequency, worldIn)) {

			range = silo.range.get();

			distance = TileMissileSilo.calculateDistance(silo.getBlockPos(), target);

			if (range == 0 || (range > 0 && range < distance)) {
				continue;
			}

			silo.target.set(trace.toBlockPos());

			silo.shouldLaunch = true;

			extractPower(designator, USAGE, false);

		}

		playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launch", frequency), false);
		playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launchsend", trace), false);

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
			PlayerEntity player = (PlayerEntity) entityIn;
			player.displayClientMessage(BallistixTextUtils.chatMessage("radargun.text", trace.toBlockPos().toShortString()), true);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (stack.hasTag()) {
			CompoundNBT nbt = stack.getTag();
			if (nbt.contains(FREQUENCY_KEY)) {
				int freq = getFrequency(stack);
				tooltip.add(BallistixTextUtils.tooltip("laserdesignator.frequency", freq));
			} else {
				tooltip.add(BallistixTextUtils.tooltip("laserdesignator.nofrequency"));
			}
		}
	}

	public static int getFrequency(ItemStack stack) {
		return stack.getOrCreateTag().getInt(FREQUENCY_KEY);
	}
	
}