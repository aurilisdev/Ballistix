package ballistix.common.item;

import java.util.List;

import ballistix.api.silo.ILauncherControlPanel;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTUtil;
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
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.utilities.math.MathUtils;
import voltaic.prefab.utilities.object.Location;
import voltaic.prefab.utilities.object.TransferPack;

public class ItemRadarGun extends ItemElectric {

    public static final double USAGE = 150.0;

    public ItemRadarGun() {
        super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), () -> BallistixCreativeTabs.MAIN);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        if (context.getLevel().isClientSide || !stack.getOrCreateTag().contains(NBTUtils.LOCATION)) {
            return super.onItemUseFirst(stack, context);
        }
        TileEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());

        if (tile instanceof ILauncherControlPanel) {

            ((ILauncherControlPanel) tile).setTargetFromDesignator(getCoordiantes(stack));

        } else if (tile instanceof TileMultiSubnode && tile.getLevel().getBlockEntity(((TileMultiSubnode) tile).parentPos.getValue()) instanceof ILauncherControlPanel) {

        	((ILauncherControlPanel) tile.getLevel().getBlockEntity(((TileMultiSubnode) tile).parentPos.getValue())).setTargetFromDesignator(getCoordiantes(stack));

        } else if (tile instanceof TileTurretAntimissile) {
            if (((TileTurretAntimissile) tile).bindFireControlRadar(getCoordiantes(stack))) {
                context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turretsucess"), true);
            } else {
                context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turrettoofar"), true);
            }
        }

        return super.onItemUseFirst(stack, context);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        return super.useOn(context);
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

        //prevents using the radar gun on missile silo from overriding the stored coords

        TileEntity tileentity = trace.getTile(playerIn.level);
        
        if (tileentity instanceof ILauncherControlPanel || tileentity instanceof TileMultiSubnode && tileentity.getLevel().getBlockEntity(((TileMultiSubnode) tileentity).parentPos.getValue()) instanceof ILauncherControlPanel || tileentity instanceof TileTurretAntimissile) {
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
    public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (stack.hasTag() && stack.getTag().contains(NBTUtils.LOCATION)) {
            tooltip.add(BallistixTextUtils.tooltip("radargun.pos", getCoordiantes(stack).toShortString()).withStyle(TextFormatting.GRAY));
        } else {
            tooltip.add(BallistixTextUtils.tooltip("radargun.notag").withStyle(TextFormatting.GRAY));
        }
    }
    
    public static void storeCoordiantes(ItemStack stack, BlockPos pos) {
		stack.getOrCreateTag().put(NBTUtils.LOCATION, NBTUtil.writeBlockPos(pos));
	}

	public static BlockPos getCoordiantes(ItemStack stack) {
		return NBTUtil.readBlockPos(stack.getOrCreateTag().getCompound(NBTUtils.LOCATION));
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

}
