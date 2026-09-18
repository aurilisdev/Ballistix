package ballistix.common.tile.silo;

import ballistix.Ballistix;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.ILauncherSupportFrame;
import ballistix.api.silo.SiloRegistry;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT1;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT2;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT3;
import ballistix.common.settings.BallistixConfig;
import ballistix.registers.BallistixDataComponentTypes;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentForgeEnergy;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;
import voltaic.registers.VoltaicDataComponentTypes;

public class TileLauncherControlPanelT1 extends GenericTile implements ILauncherControlPanel {

    public SingleProperty<Integer> frequency = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.INTEGER, "frequency", 0)
		    .onChange((prop, prevFreq) -> {
			Level level = this.level;
			if (level == null || level.isClientSide) {
			    return;
			}

			int newFreq = prop.getValue();

			SiloRegistry.unregisterSilo(prevFreq, this);
			SiloRegistry.registerSilo(newFreq, this);

		    }))
	    .setUpdateServer();

    public SingleProperty<BlockPos> target = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.BLOCK_POS, "target", BlockPos.ZERO))
	    .setUpdateServer();

    private int cooldown = 100;
    public final SingleProperty<Boolean> shouldLaunch = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.BOOLEAN, "shouldlaunch", false)).setUpdateServer();

    public TileLauncherControlPanelT1(BlockPos pos, BlockState state) {
	this(BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER1.get(), pos, state);
    }

    public TileLauncherControlPanelT1(BlockEntityType<?> type, BlockPos pos, BlockState state) {
	super(type, pos, state);
	int tier = getTier();
	addComponent(new ComponentTickable(this).tickServer(this::tickServer));
	addComponent(new ComponentElectrodynamic(this, false, true)
		.voltage(VoltaicCapabilities.DEFAULT_VOLTAGE * Math.pow(2, tier - 1))
		.maxJoules(BallistixConfig.INSTANCE.MISSILESILO_USAGE.get() * 20 * tier)
		.setInputDirections(BlockEntityUtils.MachineDirection.values()));
	if (tier == 3) {
	    addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(2))
		    .setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values())
		    .setDirectionsBySlot(1, BlockEntityUtils.MachineDirection.values())
		    .valid(this::isItemValidForSlot));
	} else {
	    addComponent(new ComponentInventory(this));
	}
	if (tier == 1) {
	    addComponent(new ComponentContainerProvider("launchercontrolpaneltier" + tier, this)
		    .createMenu((id, player) -> new ContainerLauncherControlPanelT1(id, player,
			    requireComponent(IComponentType.Inventory), getCoordsArray())));
	} else if (tier == 2) {
	    addComponent(new ComponentContainerProvider("launchercontrolpaneltier" + tier, this)
		    .createMenu((id, player) -> new ContainerLauncherControlPanelT2(id, player,
			    requireComponent(IComponentType.Inventory), getCoordsArray())));
	} else if (tier == 3) {
	    addComponent(new ComponentContainerProvider("launchercontrolpaneltier" + tier, this)
		    .createMenu((id, player) -> new ContainerLauncherControlPanelT3(id, player,
			    requireComponent(IComponentType.Inventory), getCoordsArray())));
	}
	addComponent(new ComponentForgeEnergy(this));

    }

    protected void tickServer(Level level, ComponentTickable tickable) {
	ComponentElectrodynamic electro = requireComponent(IComponentType.Electrodynamic);

	if (cooldown > 0 || electro.getJoulesStored() < BallistixConfig.INSTANCE.MISSILESILO_USAGE.get() * getTier()) {
	    cooldown--;
	    return;
	}

	boolean hasRedstone = level.hasNeighborSignal(getBlockPos());

	ILauncherPlatform platform = getPlatform();
	if (platform == null)
	    return;

	if (!platform.hasMissile() || platform.hasExplosive() && platform.hasSAM()
		|| !platform.hasExplosive() && !platform.hasSAM() || !hasRedstone && !shouldLaunch.getValue()) {
	    return;
	}

	int inaccuracy = BallistixConfig.INSTANCE.LAUNCH_PLATFORM_DEFAULT_INACCURACY.get();

	ILauncherSupportFrame frame = getSupportFrame();
	if (frame != null) {
	    inaccuracy = frame.getInaccuracy();
	}

	shouldLaunch.setValue(false);

	double dist = calculateDistance(worldPosition, target.getValue());

	if (platform.getRange() < dist) {
	    return;
	}

	int newCool = platform.launch(level, this, hasRedstone, inaccuracy);

	if (newCool > 0) {
	    cooldown = newCool;
	    electro.joules(electro.getJoulesStored() - BallistixConfig.INSTANCE.MISSILESILO_USAGE.get() * getTier());
	}
    }

    protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
	return stack.is(BallistixItems.ITEM_RADARGUN) || stack.is(BallistixItems.ITEM_LASERDESIGNATOR);
    }

    @Override
    public void onBlockDestroyed(Level level) {
	if (level.isClientSide) {
	    return;
	}
	SiloRegistry.unregisterSilo(frequency.getValue(), this);

	ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

	ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level, worldPosition, chunkPos.x, chunkPos.z,
		false, true);

    }

    @Override
    public void onPlace(Level level, BlockState oldState, boolean isMoving) {
	super.onPlace(level, oldState, isMoving);
	if (level.isClientSide)
	    return;

	ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

	ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level, worldPosition, chunkPos.x, chunkPos.z,
		true, true);
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

	    sync.set(BallistixDataComponentTypes.BOUND_FREQUENCY, frequency.getValue());

	} else if (sync.is(BallistixItems.ITEM_RADARGUN)) {

	    if (sync.has(VoltaicDataComponentTypes.BLOCK_POS)) {
		target.setValue(sync.get(VoltaicDataComponentTypes.BLOCK_POS));
	    }

	}
    }

    @Override
    public void onLoad() {
	super.onLoad();
	Level level = this.level;
	if (level == null)
	    return;

	if (!level.isClientSide) {
	    SiloRegistry.registerSilo(frequency.getValue(), this);
	}
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
	super.saveAdditional(compound, registries);
	compound.putInt("silocooldown", cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
	super.loadAdditional(compound, registries);
	cooldown = compound.getInt("silocooldown");
    }

    @Override
    public ItemInteractionResult useWithItem(Level level, ItemStack used, Player player, InteractionHand hand,
	    BlockHitResult hit) {
	ItemStack handStack = player.getItemInHand(hand);
	if (handStack.getItem() == BallistixItems.ITEM_RADARGUN.get()
		|| handStack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {
	    return ItemInteractionResult.FAIL;
	}
	return super.useWithItem(level, used, player, hand, hit);
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
	shouldLaunch.setValue(true);
    }

    @Override
    public void setTarget(BlockPos blockPos) {
	target.setValue(blockPos);
    }

    @Override
    public void setTargetFromDesignator(BlockPos target) {
	setTarget(new BlockPos(target.getX(), this.target.getValue().getY(), target.getZ()));
    }

    @Override
    public BlockPos getTarget() {
	return target.getValue();
    }

    @Override
    public int getFrequency() {
	return frequency.getValue();
    }

    @Override
    public ILauncherPlatform getPlatform() {
	Level level = this.level;
	if (level == null)
	    return null;

	BlockEntity tile = level.getBlockEntity(worldPosition.relative(getFacing().getOpposite()));
	if (!(tile instanceof ILauncherPlatform platform) || tile.isRemoved())
	    return null;

	return platform;
    }

    @Override
    public ILauncherSupportFrame getSupportFrame() {
	Level level = this.level;
	if (level == null)
	    return null;

	BlockEntity tile = level.getBlockEntity(worldPosition.relative(getFacing().getOpposite(), 2));
	if (!(tile instanceof ILauncherSupportFrame frame) || tile.isRemoved())
	    return null;

	return frame;
    }

    public static double calculateDistance(BlockPos fromPos, BlockPos toPos) {
	double deltaX = fromPos.getX() - toPos.getX();
	double deltaY = fromPos.getY() - toPos.getY();
	double deltaZ = fromPos.getZ() - toPos.getZ();

	return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    @EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
    private static final class ChunkloaderManager {

	private static final TicketController TICKET_CONTROLLER = new TicketController(
		Ballistix.rl("chunkloadercontroller"));

	@SubscribeEvent
	public static void register(RegisterTicketControllersEvent event) {
	    event.register(TICKET_CONTROLLER);
	}

    }

}
