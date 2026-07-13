package ballistix.common.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.Ballistix;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.common.world.TrackerSecurityData;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import ballistix.registers.BallistixDataComponentTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.GAME)
public class ItemTracker extends ItemElectric {

    public static final double USAGE = 150;

    public ItemTracker() {
	super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667)
		.receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120))
		.extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1),
		BallistixCreativeTabs.MAIN, item -> Items.AIR);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {

	if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
	    return super.onItemUseFirst(stack, context);
	}

	if (!stack.has(BallistixDataComponentTypes.TRACKER_UUID)
		|| !stack.has(BallistixDataComponentTypes.TRACKER_TARGET)) {
	    return super.onItemUseFirst(stack, context);
	}

	BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());

	ILauncherControlPanel silo = null;

	if (tile instanceof ILauncherControlPanel controlPanel) {
	    silo = controlPanel;
	} else if (tile instanceof TileMultiSubnode subnode && subnode.getLevel() != null && subnode.getLevel()
		.getBlockEntity(subnode.parentPos.getValue()) instanceof ILauncherControlPanel controlPanel) {
	    silo = controlPanel;
	}

	/*
	 * Do not interfere with ordinary block interactions.
	 */
	if (silo == null) {
	    return super.onItemUseFirst(stack, context);
	}

	if (!isTrackerValid(stack, serverLevel)) {
	    return InteractionResult.FAIL;
	}

	Target target = stack.get(BallistixDataComponentTypes.TRACKER_TARGET);

	if (target == null) {
	    return InteractionResult.FAIL;
	}

	silo.setTarget(new BlockPos(Mth.floor(target.x()), 0, Mth.floor(target.z())));

	return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {

	Component name = BallistixTextUtils.tooltip("tracker.none");

	Level level = context.level();
	UUID targetUuid = stack.get(BallistixDataComponentTypes.TRACKER_UUID);
	Integer targetId = stack.get(BallistixDataComponentTypes.TRACKER_ID);

	if (level != null && targetUuid != null && targetId != null) {

	    Entity entity = level.getEntity(targetId);

	    if (entity != null && targetUuid.equals(entity.getUUID())) {
		name = entity.getName();
	    }
	}

	tooltip.add(BallistixTextUtils.tooltip("tracker.tracking", name.copy().withStyle(ChatFormatting.GRAY))
		.withStyle(ChatFormatting.DARK_GRAY));

	super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity holder, int slot, boolean selected) {

	super.inventoryTick(stack, level, holder, slot, selected);

	if (!(level instanceof ServerLevel serverLevel)) {
	    return;
	}
	if (!(holder instanceof Player) || !stack.has(BallistixDataComponentTypes.TRACKER_UUID)) {
	    return;
	}

	UUID targetUuid = stack.get(BallistixDataComponentTypes.TRACKER_UUID);

	if (targetUuid == null) {
	    return;
	}

	if (!isTrackerValid(stack, serverLevel)) {
	    return;
	}

	Entity target = findTrackedEntity(serverLevel.getServer(), targetUuid);

	if (target == null) {
	    /*
	     * Keep the UUID and last known coordinates. The entity may merely be offline or
	     * inside an unloaded chunk.
	     *
	     * The runtime entity ID is no longer trustworthy.
	     */
	    stack.remove(BallistixDataComponentTypes.TRACKER_ID);
	    return;
	}

	stack.set(BallistixDataComponentTypes.TRACKER_TARGET, new Target(target.getX(), target.getZ()));

	/*
	 * This ID is only a temporary convenience for client-side tooltip/render
	 * lookup. The UUID remains the real identity.
	 */

	if (target.level() == serverLevel) {
	    stack.set(BallistixDataComponentTypes.TRACKER_ID, target.getId());
	} else {
	    stack.remove(BallistixDataComponentTypes.TRACKER_ID);
	}
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
	return !oldStack.is(newStack.getItem());
    }

    @Nullable
    private static Entity findTrackedEntity(MinecraftServer server, UUID targetUuid) {

	/*
	 * Players are easy to resolve globally, regardless of their current dimension.
	 */
	ServerPlayer player = server.getPlayerList().getPlayer(targetUuid);

	if (player != null && !player.isRemoved()) {
	    return player;
	}

	/*
	 * Other entities can only be found while their chunk and entity are loaded.
	 */
	for (ServerLevel level : server.getAllLevels()) {

	    Entity entity = level.getEntity(targetUuid);

	    if (entity != null && !entity.isRemoved()) {
		return entity;
	    }
	}

	return null;
    }

    public InteractionResult bindToEntity(ItemStack stack, Player player, LivingEntity entity) {

	if (getJoulesStored(stack) < USAGE) {
	    return InteractionResult.PASS;
	}

	if (player.level() instanceof ServerLevel serverLevel) {

	    UUID targetUuid = entity.getUUID();

	    long currentRevision = TrackerSecurityData.get(serverLevel.getServer()).getRevision(targetUuid);

	    stack.set(BallistixDataComponentTypes.TRACKER_UUID, targetUuid);
	    stack.set(BallistixDataComponentTypes.TRACKER_REVISION, currentRevision);
	    stack.set(BallistixDataComponentTypes.TRACKER_ID, entity.getId());
	    stack.set(BallistixDataComponentTypes.TRACKER_TARGET, new Target(entity.getX(), entity.getZ()));

	    extractPower(stack, USAGE, false);
	}

	return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {

	Player player = event.getEntity();
	ItemStack stack = player.getItemInHand(event.getHand());

	if (!(stack.getItem() instanceof ItemTracker tracker)) {
	    return;
	}

	if (!(event.getTarget() instanceof LivingEntity target)) {
	    return;
	}

	InteractionResult result = tracker.bindToEntity(stack, player, target);

	if (result.consumesAction()) {
	    event.setCancellationResult(result);
	    event.setCanceled(true);
	}
    }

    // We have this method so we can check every use case; both in tick and
    // onItemUse.
    private static boolean isTrackerValid(ItemStack stack, ServerLevel level) {

	UUID targetUuid = stack.get(BallistixDataComponentTypes.TRACKER_UUID);

	if (targetUuid == null) {
	    return false;
	}

	long trackerRevision = stack.getOrDefault(BallistixDataComponentTypes.TRACKER_REVISION, 0L);

	long currentRevision = TrackerSecurityData.get(level.getServer()).getRevision(targetUuid);

	if (trackerRevision < currentRevision) {
	    stack.remove(BallistixDataComponentTypes.TRACKER_UUID);
	    stack.remove(BallistixDataComponentTypes.TRACKER_REVISION);
	    stack.remove(BallistixDataComponentTypes.TRACKER_ID);
	    stack.remove(BallistixDataComponentTypes.TRACKER_TARGET);
	    return false;
	}

	return true;
    }

    public static record Target(double x, double z) {

	public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance
		.group(Codec.DOUBLE.fieldOf("x").forGetter(Target::x), Codec.DOUBLE.fieldOf("z").forGetter(Target::z))
		.apply(instance, Target::new));

	public static final StreamCodec<ByteBuf, Target> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.DOUBLE,
		Target::x, ByteBufCodecs.DOUBLE, Target::z, Target::new);

    }

}
