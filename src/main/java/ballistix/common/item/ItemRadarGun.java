package ballistix.common.item;

import java.util.List;

import ballistix.api.silo.ILauncherControlPanel;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.ChatFormatting;
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
import voltaic.registers.VoltaicDataComponentTypes;

public class ItemRadarGun extends ItemElectric {

    public static final double USAGE = 150.0;

    public ItemRadarGun() {
        super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), BallistixCreativeTabs.MAIN, item -> Items.AIR);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide || !stack.has(VoltaicDataComponentTypes.BLOCK_POS)) {
            return super.onItemUseFirst(stack, context);
        }
        BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());

        if (tile instanceof ILauncherControlPanel silo) {

            silo.setTarget(stack.get(VoltaicDataComponentTypes.BLOCK_POS));

        } else if (tile instanceof TileMultiSubnode subnode && subnode.getLevel().getBlockEntity(subnode.parentPos.getValue()) instanceof ILauncherControlPanel silo) {

            silo.setTarget(stack.get(VoltaicDataComponentTypes.BLOCK_POS));

        } else if (tile instanceof TileTurretAntimissile turret) {
            if (turret.bindFireControlRadar(stack.get(VoltaicDataComponentTypes.BLOCK_POS))) {
                context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turretsucess"), true);
            } else {
                context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turrettoofar"), true);
            }
        }

        return super.onItemUseFirst(stack, context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

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

        if (trace.getTile(playerIn.level()) instanceof ILauncherControlPanel || trace.getTile(playerIn.level()) instanceof TileMultiSubnode subnode && subnode.getLevel().getBlockEntity(subnode.parentPos.getValue()) instanceof ILauncherControlPanel || trace.getTile(worldIn) instanceof TileTurretAntimissile) {
            return super.use(worldIn, playerIn, handIn);
        }

        radarGun.set(VoltaicDataComponentTypes.BLOCK_POS, trace.toBlockPos());

        extractPower(radarGun, USAGE, false);

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
        if (stack.has(VoltaicDataComponentTypes.BLOCK_POS)) {
            tooltip.add(BallistixTextUtils.tooltip("radargun.pos", stack.get(VoltaicDataComponentTypes.BLOCK_POS).toShortString()).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(BallistixTextUtils.tooltip("radargun.notag").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }
}
