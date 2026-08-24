package ballistix.common.item;

import java.util.List;
import java.util.Set;

import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.SiloRegistry;
import ballistix.compatibility.TessellateCompat;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import ballistix.registers.BallistixDataComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

    public ItemLaserDesignator() {
	super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667)
		.receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120))
		.extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1),
		BallistixCreativeTabs.MAIN, item -> Items.AIR);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
	BlockEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
	ILauncherControlPanel silo = ent instanceof ILauncherControlPanel s ? s : null;
	if (ent instanceof TileMultiSubnode node) {
	    BlockEntity core = node.getLevel().getBlockEntity(node.parentPos.getValue());
	    if (core instanceof ILauncherControlPanel c) {
		silo = c;
	    }
	}
	if (silo != null && !context.getLevel().isClientSide) {

	    context.getPlayer().displayClientMessage(
		    BallistixTextUtils.chatMessage("laserdesignator.setfrequency", silo.getFrequency()), false);

	    stack.set(BallistixDataComponentTypes.BOUND_FREQUENCY, silo.getFrequency());

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
	if (tile instanceof ILauncherControlPanel || tile instanceof TileMultiSubnode) {
	    return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}

	int frequency = designator.get(BallistixDataComponentTypes.BOUND_FREQUENCY);

	BlockPos target = trace.toBlockPos();
	Set<BlockPos> siloPositions = SiloRegistry.getSiloPositions(frequency);

	if (siloPositions.isEmpty()) {
	    return super.use(worldIn, playerIn, handIn);
	}

	extractPower(designator, USAGE, false);
	ServerLevel level = (ServerLevel) worldIn;
	for (BlockPos siloPos : siloPositions) {
	    TessellateCompat.runOnRegion(level, siloPos, () -> {
		if (!(level.getBlockEntity(siloPos) instanceof ILauncherControlPanel silo)) {
		    return;
		}

		ILauncherPlatform platform = silo.getPlatform();

		if (platform == null) {
		    return;
		}

		int range = platform.getRange();
		double distance = TileLauncherControlPanelT1.calculateDistance(silo.getPos(), target);

		if (range == 0 || range > 0 && range < distance
			|| distance > BallistixConfig.INSTANCE.LASER_DESIGNATOR_RANGE.get()) {
		    return;
		}

		silo.setTargetFromDesignator(target);

		silo.launch();

	    });
	}

	playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launch", frequency), false);
	playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launchsend", trace.toString()),
		false);

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
	    player.displayClientMessage(
		    BallistixTextUtils.chatMessage("radargun.text", trace.toBlockPos().toShortString()), true);
	}
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
	super.appendHoverText(stack, context, tooltip, flagIn);
	if (stack.has(BallistixDataComponentTypes.BOUND_FREQUENCY)) {
	    tooltip.add(BallistixTextUtils
		    .tooltip("laserdesignator.frequency", stack.get(BallistixDataComponentTypes.BOUND_FREQUENCY))
		    .withStyle(ChatFormatting.GRAY));
	} else {
	    tooltip.add(BallistixTextUtils.tooltip("laserdesignator.nofrequency").withStyle(ChatFormatting.GRAY));
	}
	tooltip.add(BallistixTextUtils.tooltip("laserdesignator.signalrange", Component
		.literal("" + BallistixConfig.INSTANCE.LASER_DESIGNATOR_RANGE.get()).withStyle(ChatFormatting.GRAY))
		.withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
	return !oldStack.is(newStack.getItem());
    }
}
