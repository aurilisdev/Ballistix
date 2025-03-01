package ballistix.common.item;

import java.util.List;

import ballistix.References;
import ballistix.common.tile.TileMissileSilo;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
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

public class ItemRadarGun extends ItemElectric {

	public static final double USAGE = 150.0;

	public ItemRadarGun() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1).tab(References.BALLISTIXTAB));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getLevel().isClientSide) {
			return super.onItemUseFirst(stack, context);
		}
		TileEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());

		if(tile instanceof TileMissileSilo) {

			((TileMissileSilo) tile).target.set(getCoordiantes(stack));
			((TileMissileSilo) tile).target.forceDirty();

		} else if (tile instanceof TileMultiSubnode && ((TileMultiSubnode) tile).getLevel().getBlockEntity(((TileMultiSubnode) tile).parentPos.get().toBlockPos()) instanceof TileMissileSilo) {
			
			TileMissileSilo silo = (TileMissileSilo) ((TileMultiSubnode) tile).getLevel().getBlockEntity(((TileMultiSubnode) tile).parentPos.get().toBlockPos());

			silo.target.set(getCoordiantes(stack));
			silo.target.forceDirty();

		} else if (tile instanceof TileTurretAntimissile) {
			if(((TileTurretAntimissile) tile).bindFireControlRadar(getCoordiantes(stack))) {
				context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turretsucess"), true);
			} else {
				context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turrettoofar"), true);
			}
		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {

		if (worldIn.isClientSide) {
			return super.use(worldIn, playerIn, handIn);
		}

		Location trace = MathUtils.getRaytracedBlock(playerIn);

		if (trace == null) {
			return super.use(worldIn, playerIn, handIn);
		}

		ItemStack radarGun = playerIn.getItemInHand(handIn);

		if (getJoulesStored(radarGun) < USAGE) {
			return super.use(worldIn, playerIn, handIn);
		}
		
		TileEntity tile = trace.getTile(playerIn.level);
		
		if(tile instanceof TileMissileSilo || trace.getTile(playerIn.level) instanceof TileMultiSubnode && ((TileMultiSubnode) tile).getLevel().getBlockEntity(((TileMultiSubnode) tile).parentPos.get().toBlockPos()) instanceof TileMissileSilo || trace.getTile(worldIn) instanceof TileTurretAntimissile) {
			return super.use(worldIn, playerIn, handIn);
		}

		storeCoordiantes(radarGun, trace.toBlockPos());

		extractPower(radarGun, USAGE, false);

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
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (stack.hasTag() && stack.getTag().contains("xCoord")) {
			tooltip.add(BallistixTextUtils.tooltip("radargun.pos", getCoordiantes(stack).toShortString()));
		} else {
			tooltip.add(BallistixTextUtils.tooltip("radargun.notag"));
		}
	}

	public static void storeCoordiantes(ItemStack stack, BlockPos pos) {
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putInt("xCoord", pos.getX());
		nbt.putInt("yCoord", pos.getY());
		nbt.putInt("zCoord", pos.getZ());
	}

	public static BlockPos getCoordiantes(ItemStack stack) {
		CompoundNBT tag = stack.getOrCreateTag();
		int x = tag.getInt("xCoord");
		int y = tag.getInt("yCoord");
		int z = tag.getInt("zCoord");
		return new BlockPos(x, y, z);
	}

}