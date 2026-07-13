package ballistix.common.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.common.world.TrackerSecurityData;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
public class ItemTracker extends ItemElectric {

    public static final double USAGE = 150.0;

    /*
     * These names are already used by BallistixClientRegister for the tracker-angle
     * item property.
     */
    public static final String X = "target_x";
    public static final String Z = "target_z";

    /*
     * Persistent entity identity and scanner revision.
     */
    private static final String TARGET_UUID = "target_uuid";
    private static final String TRACKER_REVISION = "tracker_revision";

    /*
     * Temporary runtime entity ID used only for client-side entity-name lookup.
     */
    private static final String TARGET_ID = "target_id";

    /*
     * Key used by the previous implementation for an integer runtime ID.
     */
    private static final String LEGACY_TARGET_ID = "uuid";

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

	if (!hasTarget(stack) || !hasTargetCoords(stack)) {
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

	/*
	 * Check the revision immediately before supplying the target to the silo. This
	 * also handles trackers stored in chests, which do not receive inventoryTick().
	 */
	if (!isTrackerValid(stack, serverLevel)) {
	    return InteractionResult.FAIL;
	}

	silo.setTarget(new BlockPos(Mth.floor(getX(stack)), 0, Mth.floor(getZ(stack))));

	return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

	Component name = BallistixTextUtils.tooltip("tracker.none");

	UUID targetUuid = getTargetUuid(stack);

	if (level != null && targetUuid != null && hasTargetId(stack)) {

	    Entity entity = level.getEntity(getTargetId(stack));

	    /*
	     * Integer runtime IDs may be reused, so always verify the UUID.
	     */
	    if (entity != null && targetUuid.equals(entity.getUUID())) {

		name = entity.getName();
	    }
	}

	tooltip.add(BallistixTextUtils.tooltip("tracker.tracking", name.copy().withStyle(ChatFormatting.GRAY))
		.withStyle(ChatFormatting.DARK_GRAY));

	super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity holder, int slot, boolean selected) {

	super.inventoryTick(stack, level, holder, slot, selected);

	if (!(level instanceof ServerLevel serverLevel)) {
	    return;
	}

	/*
	 * Update trackers anywhere in a player's inventory, not only while held.
	 *
	 * Stagger updates using the slot number so multiple trackers do not all perform
	 * a global lookup during the same tick.
	 */
	if (!(holder instanceof Player) || !hasTarget(stack) || (holder.tickCount + slot) % 10 != 0) {

	    return;
	}

	if (!isTrackerValid(stack, serverLevel)) {
	    return;
	}

	UUID targetUuid = getTargetUuid(stack);

	if (targetUuid == null) {
	    return;
	}

	Entity target = findTrackedEntity(serverLevel.getServer(), targetUuid);

	if (target == null) {

	    /*
	     * Preserve the UUID and last-known coordinates. The target could merely be
	     * offline or inside an unloaded chunk.
	     *
	     * The temporary runtime ID is no longer trustworthy.
	     */
	    removeTargetId(stack);
	    return;
	}

	setX(stack, target.getX());
	setZ(stack, target.getZ());

	/*
	 * Entity IDs are only meaningful in the dimension that assigned them. Only
	 * preserve the ID when the target and tracker holder are in the same dimension.
	 */
	if (target.level() == serverLevel) {
	    setTargetId(stack, target.getId());
	} else {
	    removeTargetId(stack);
	}
    }

    public InteractionResult bindToEntity(ItemStack stack, Player player, LivingEntity target) {

	if (getJoulesStored(stack) < USAGE) {
	    return InteractionResult.PASS;
	}

	if (player.level() instanceof ServerLevel serverLevel) {

	    UUID targetUuid = target.getUUID();

	    long currentRevision = TrackerSecurityData.get(serverLevel.getServer()).getRevision(targetUuid);

	    setTargetUuid(stack, targetUuid);
	    setTrackerRevision(stack, currentRevision);
	    setTargetId(stack, target.getId());
	    setX(stack, target.getX());
	    setZ(stack, target.getZ());

	    extractPower(stack, USAGE, false);
	}

	return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    /*
     * Fallback for entities that allow the normal item interaction callback.
     */
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
	    InteractionHand hand) {

	return bindToEntity(stack, player, target);
    }

    /*
     * This is required because many entities handle right-clicking before
     * Item#interactLivingEntity is reached.
     */
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

    /*
     * Called both during inventory updates and immediately before the tracker is
     * used on a launcher control panel.
     */
    private static boolean isTrackerValid(ItemStack stack, ServerLevel level) {

	UUID targetUuid = getTargetUuid(stack);

	if (targetUuid == null) {
	    return false;
	}

	long trackerRevision = getTrackerRevision(stack);

	long currentRevision = TrackerSecurityData.get(level.getServer()).getRevision(targetUuid);

	if (trackerRevision < currentRevision) {
	    wipeData(stack);
	    return false;
	}

	return true;
    }

    @Nullable
    private static Entity findTrackedEntity(MinecraftServer server, UUID targetUuid) {

	/*
	 * Players can be found globally, including across dimensions.
	 */
	ServerPlayer player = server.getPlayerList().getPlayer(targetUuid);

	if (player != null && !player.isRemoved()) {
	    return player;
	}

	/*
	 * Other entities can only be found while their entity and chunk are loaded.
	 */
	for (ServerLevel level : server.getAllLevels()) {

	    Entity entity = level.getEntity(targetUuid);

	    if (entity != null && !entity.isRemoved()) {
		return entity;
	    }
	}

	return null;
    }

    public static double getX(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	return tag == null ? 0.0 : tag.getDouble(X);
    }

    public static double getZ(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	return tag == null ? 0.0 : tag.getDouble(Z);
    }

    public static void setX(ItemStack stack, double x) {

	stack.getOrCreateTag().putDouble(X, x);
    }

    public static void setZ(ItemStack stack, double z) {

	stack.getOrCreateTag().putDouble(Z, z);
    }

    public static boolean hasTargetCoords(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	return tag != null && tag.contains(X, Tag.TAG_DOUBLE) && tag.contains(Z, Tag.TAG_DOUBLE);
    }

    @Nullable
    public static UUID getTargetUuid(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	if (tag == null || !tag.hasUUID(TARGET_UUID)) {
	    return null;
	}

	return tag.getUUID(TARGET_UUID);
    }

    public static void setTargetUuid(ItemStack stack, UUID targetUuid) {

	CompoundTag tag = stack.getOrCreateTag();

	/*
	 * Remove the old integer-ID key when rebinding an old tracker.
	 */
	tag.remove(LEGACY_TARGET_ID);
	tag.putUUID(TARGET_UUID, targetUuid);
    }

    public static boolean hasTarget(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	return tag != null && tag.hasUUID(TARGET_UUID);
    }

    public static int getTargetId(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	return tag == null ? 0 : tag.getInt(TARGET_ID);
    }

    public static void setTargetId(ItemStack stack, int targetId) {

	stack.getOrCreateTag().putInt(TARGET_ID, targetId);
    }

    public static boolean hasTargetId(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	return tag != null && tag.contains(TARGET_ID, Tag.TAG_INT);
    }

    public static void removeTargetId(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	if (tag != null) {
	    tag.remove(TARGET_ID);
	}
    }

    public static long getTrackerRevision(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	return tag == null ? 0L : tag.getLong(TRACKER_REVISION);
    }

    public static void setTrackerRevision(ItemStack stack, long revision) {

	stack.getOrCreateTag().putLong(TRACKER_REVISION, revision);
    }

    public static void wipeData(ItemStack stack) {

	CompoundTag tag = stack.getTag();

	if (tag == null) {
	    return;
	}

	tag.remove(TARGET_UUID);
	tag.remove(TRACKER_REVISION);
	tag.remove(TARGET_ID);
	tag.remove(LEGACY_TARGET_ID);
	tag.remove(X);
	tag.remove(Z);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

	/*
	 * Prevent the tracker from repeatedly playing the equip animation whenever its
	 * position NBT changes.
	 */
	return !oldStack.is(newStack.getItem());
    }
}