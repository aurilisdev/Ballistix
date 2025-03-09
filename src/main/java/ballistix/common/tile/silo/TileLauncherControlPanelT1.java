package ballistix.common.tile.silo;

import ballistix.Ballistix;
import ballistix.References;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.ILauncherSupportFrame;
import ballistix.api.silo.SiloRegistry;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT1;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT2;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT3;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixDataComponentTypes;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixTiles;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import electrodynamics.prefab.utilities.object.CachedTileOutput;
import electrodynamics.prefab.utilities.object.TransferPack;
import electrodynamics.registers.ElectrodynamicsDataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;

public class TileLauncherControlPanelT1 extends GenericTile implements ILauncherControlPanel {

	public Property<Integer> frequency = property(new Property<>(PropertyTypes.INTEGER, "frequency", 0).onChange((prop, prevFreq) -> {

		if (level == null || level.isClientSide) {
			return;
		}

		int newFreq = prop.get();

		SiloRegistry.unregisterSilo(prevFreq, this);
		SiloRegistry.registerSilo(newFreq, this);

	}));

	public Property<BlockPos> target = property(new Property<>(PropertyTypes.BLOCK_POS, "target", BlockPos.ZERO));

	private int cooldown = 100;
	public boolean shouldLaunch = false;
	public CachedTileOutput launcherPlatform;
	public CachedTileOutput supportFrame;

	public TileLauncherControlPanelT1(BlockPos pos, BlockState state) {
		this(BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER1.get(), pos, state);
	}

	public TileLauncherControlPanelT1(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		int tier = getTier();
		addComponent(new ComponentTickable(this).tickServer(this::tickServer));
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(120 * Math.pow(2, tier - 1)).maxJoules(Constants.MISSILESILO_USAGE * 20 * tier).setInputDirections(BlockEntityUtils.MachineDirection.values()));
		if (tier == 3) {
			addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(1)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).setDirectionsBySlot(1, BlockEntityUtils.MachineDirection.values()).valid(this::isItemValidForSlot));
		} else {
			addComponent(new ComponentInventory(this));
		}
		addComponent(new ComponentPacketHandler(this));
		if (tier == 1) {
			addComponent(new ComponentContainerProvider("container.launchercontrolpaneltier" + tier, this).createMenu((id, player) -> new ContainerLauncherControlPanelT1(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		} else if (tier == 2) {
			addComponent(new ComponentContainerProvider("container.launchercontrolpaneltier" + tier, this).createMenu((id, player) -> new ContainerLauncherControlPanelT2(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		} else if (tier == 3) {
			addComponent(new ComponentContainerProvider("container.launchercontrolpaneltier" + tier, this).createMenu((id, player) -> new ContainerLauncherControlPanelT3(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		}

	}

	protected void tickServer(ComponentTickable tickable) {
		Direction facing = getFacing();
		if (launcherPlatform == null) {
			launcherPlatform = new CachedTileOutput(level, worldPosition.relative(facing.getOpposite()));
		}
		if (supportFrame == null) {
			supportFrame = new CachedTileOutput(level, worldPosition.relative(facing.getOpposite(), 2));
		}
		if (tickable.getTicks() % 20 == 0) {
			launcherPlatform.update(worldPosition.relative(facing.getOpposite()));
			supportFrame.update(worldPosition.relative(facing.getOpposite(), 2));
		}
		if (target.get() == null) {
			target.set(getBlockPos());
		}

		ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

		if (cooldown > 0 || electro.getJoulesStored() < Constants.MISSILESILO_USAGE * getTier()) {
			cooldown--;
			return;
		}

		boolean hasRedstone = level.hasNeighborSignal(getBlockPos());

		if (!launcherPlatform.valid()) {
			return;
		}

		ILauncherPlatform platform = launcherPlatform.getSafe();

		if (platform == null) { // Should really update the cachedtileoutput so this cant occur. As of before
								// the getsafe, the platform wasnt null, but as it was removed inworld, the
								// output made it null and returns a null on getsafe.
			return;
		}

		if (!platform.hasMissile() || (platform.hasExplosive() && platform.hasSAM()) || (!platform.hasExplosive() && !platform.hasSAM()) || (!hasRedstone && !shouldLaunch)) {
			return;
		}

		int inaccuracy = Constants.LAUNCH_PLATFORM_DEFAULT_INACCURACY;

		if(supportFrame.valid() && supportFrame.getSafe() instanceof ILauncherSupportFrame frame) {
			inaccuracy = frame.getInaccuracy();
		}

		shouldLaunch = false;

		double dist = calculateDistance(worldPosition, target.get());

		if (platform.getRange() < dist) {
			return;
		}

		int newCool = platform.launch(this, hasRedstone, inaccuracy);
		if (newCool != -1) {
			cooldown = newCool;
		}
		electro.extractPower(TransferPack.joulesVoltage(Constants.MISSILESILO_USAGE * getTier(), electro.getVoltage()), false);
	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		return stack.is(BallistixItems.ITEM_RADARGUN) || stack.is(BallistixItems.ITEM_LASERDESIGNATOR);
	}

	@Override
	public void onBlockDestroyed() {
		if (level.isClientSide) {
			return;
		}
		SiloRegistry.unregisterSilo(frequency.get(), this);

		ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

		ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level, worldPosition, chunkPos.x, chunkPos.z, false, true);

	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		if (level.isClientSide) {
			return;
		}
		ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

		ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level, worldPosition, chunkPos.x, chunkPos.z, true, true);
	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int index) {
		handleSync(inv, index);
	}

	private void handleSync(ComponentInventory inv, int index) {
		ItemStack sync = inv.getItem(0);

		if (sync.isEmpty()) {
			return;
		}

		if (sync.is(BallistixItems.ITEM_LASERDESIGNATOR)) {

			sync.set(BallistixDataComponentTypes.BOUND_FREQUENCY, frequency.get());

		} else if (sync.is(BallistixItems.ITEM_RADARGUN)) {

			if (sync.has(ElectrodynamicsDataComponentTypes.BLOCK_POS)) {
				target.set(sync.get(ElectrodynamicsDataComponentTypes.BLOCK_POS));
			}

		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide) {
			SiloRegistry.registerSilo(frequency.get(), this);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.saveAdditional(compound, registries);
		compound.putInt("silocooldown", cooldown);
		compound.putBoolean("shouldlaunch", shouldLaunch);
	}

	@Override
	protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
		super.loadAdditional(compound, registries);
		cooldown = compound.getInt("silocooldown");
		shouldLaunch = compound.getBoolean("shouldlaunch");
	}

	@Override
	public ItemInteractionResult useWithItem(ItemStack used, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.getItem() == BallistixItems.ITEM_RADARGUN.get() || handStack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {
			return ItemInteractionResult.FAIL;
		}
		return super.useWithItem(used, player, hand, hit);
	}

	@Override
	public int getTier() {
		return 1;
	}

	@Override
	public BlockPos getPos() {
		return getBlockPos();
	}

	@Override
	public void launch() {
		shouldLaunch = true;
	}

	@Override
	public void setTarget(BlockPos blockPos) {
		target.set(blockPos);
	}

	@Override
	public BlockPos getTarget() {
		return target.get();
	}

	@Override
	public int getFrequency() {
		return frequency.get();
	}

	@Override
	public CachedTileOutput getPlatform() {
		return launcherPlatform;
	}

	@Override
	public CachedTileOutput getSupportFrame() {
		return supportFrame;
	}

	public static double calculateDistance(BlockPos fromPos, BlockPos toPos) {
		double deltaX = fromPos.getX() - toPos.getX();
		double deltaY = fromPos.getY() - toPos.getY();
		double deltaZ = fromPos.getZ() - toPos.getZ();

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	@EventBusSubscriber(modid = References.ID, bus = EventBusSubscriber.Bus.MOD)
	private static final class ChunkloaderManager {

		private static final TicketController TICKET_CONTROLLER = new TicketController(Ballistix.rl("chunkloadercontroller"));

		@SubscribeEvent
		public static void register(RegisterTicketControllersEvent event) {
			event.register(TICKET_CONTROLLER);
		}

	}

}
