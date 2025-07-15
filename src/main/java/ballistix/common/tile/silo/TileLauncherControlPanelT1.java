package ballistix.common.tile.silo;

import ballistix.Ballistix;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.ILauncherSupportFrame;
import ballistix.api.silo.SiloRegistry;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT1;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT2;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT3;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixTiles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.*;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.utilities.object.CachedTileOutput;
import voltaic.registers.VoltaicCapabilities;

public class TileLauncherControlPanelT1 extends GenericTile implements ILauncherControlPanel {

	public SingleProperty<Integer> frequency = property(new SingleProperty<>(PropertyTypes.INTEGER, "frequency", 0).onChange((prop, prevFreq) -> {

		if (level == null || level.isClientSide) {
			return;
		}

		int newFreq = prop.getValue();

		SiloRegistry.unregisterSilo(prevFreq, this);
		SiloRegistry.registerSilo(newFreq, this);

	}));

	public SingleProperty<BlockPos> target = property(new SingleProperty<>(PropertyTypes.BLOCK_POS, "target", BlockPos.ZERO));

	private int cooldown = 100;
	public final SingleProperty<Boolean> shouldLaunch = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "shouldlaunch", false));
	public CachedTileOutput launcherPlatform;
	public CachedTileOutput supportFrame;

	public TileLauncherControlPanelT1() {
		this(BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER1.get());
	}

	public TileLauncherControlPanelT1(TileEntityType<?> type) {
		super(type);
		int tier = getTier();
		addComponent(new ComponentTickable(this).tickServer(this::tickServer));
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE * Math.pow(2, tier - 1)).maxJoules(BallistixConstants.MISSILESILO_USAGE * 20 * tier).setInputDirections(BlockEntityUtils.MachineDirection.values()));
		if (tier == 3) {
			addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(1)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).setDirectionsBySlot(1, BlockEntityUtils.MachineDirection.values()).valid(this::isItemValidForSlot));
		} else {
			addComponent(new ComponentInventory(this));
		}
		addComponent(new ComponentPacketHandler(this));
		if (tier == 1) {
			addComponent(new ComponentContainerProvider("launchercontrolpaneltier" + tier, this).createMenu((id, player) -> new ContainerLauncherControlPanelT1(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		} else if (tier == 2) {
			addComponent(new ComponentContainerProvider("launchercontrolpaneltier" + tier, this).createMenu((id, player) -> new ContainerLauncherControlPanelT2(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		} else if (tier == 3) {
			addComponent(new ComponentContainerProvider("launchercontrolpaneltier" + tier, this).createMenu((id, player) -> new ContainerLauncherControlPanelT3(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		}
		addComponent(new ComponentForgeEnergy(this));

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
		if (target.getValue() == null) {
			target.setValue(getBlockPos());
		}

		ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

		if (cooldown > 0 || electro.getJoulesStored() < BallistixConstants.MISSILESILO_USAGE * getTier()) {
			cooldown--;
			return;
		}

		boolean hasRedstone = level.hasNeighborSignal(getBlockPos());

		if (!launcherPlatform.valid()) {
			return;
		}
		
		if(!(launcherPlatform.getSafe() instanceof ILauncherPlatform)) {
			return;
		}

		ILauncherPlatform platform = launcherPlatform.getSafe();

		if (platform == null) { // Should really update the cachedtileoutput so this cant occur. As of before
								// the getsafe, the platform wasnt null, but as it was removed inworld, the
								// output made it null and returns a null on getsafe.
			return;
		}

		if (!platform.hasMissile() || (platform.hasExplosive() && platform.hasSAM()) || (!platform.hasExplosive() && !platform.hasSAM()) || (!hasRedstone && !shouldLaunch.getValue())) {
			return;
		}

		int inaccuracy = BallistixConstants.LAUNCH_PLATFORM_DEFAULT_INACCURACY;

		if(supportFrame.valid() && supportFrame.getSafe() instanceof ILauncherSupportFrame) {
			inaccuracy = ((ILauncherSupportFrame) supportFrame.getSafe()).getInaccuracy();
		}

		shouldLaunch.setValue(false);

		double dist = calculateDistance(worldPosition, target.getValue());

		if (platform.getRange() < dist) {
			return;
		}

		int newCool = platform.launch(this, hasRedstone, inaccuracy);
		if (newCool != -1) {
			cooldown = newCool;
		}
		electro.joules(electro.getJoulesStored() - BallistixConstants.MISSILESILO_USAGE * getTier());
	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		return stack.getItem() == BallistixItems.ITEM_RADARGUN.get() || stack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get();
	}

	@Override
	public void onBlockDestroyed() {
		if (level.isClientSide) {
			return;
		}
		SiloRegistry.unregisterSilo(frequency.getValue(), this);

		ChunkPos pos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, false, true);

	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		if (level.isClientSide) {
			return;
		}
		ChunkPos pos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, true, true);
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

		if (sync.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {

			CompoundNBT nbt = sync.getOrCreateTag();
			nbt.putInt(ItemLaserDesignator.FREQUENCY_KEY, frequency.getValue());

		} else if (sync.getItem() == BallistixItems.ITEM_RADARGUN.get()) {

			if (sync.getOrCreateTag().contains(NBTUtils.LOCATION)) {
				target.setValue(ItemRadarGun.getCoordiantes(sync));
			}

		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide) {
			SiloRegistry.registerSilo(frequency.getValue(), this);
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("silocooldown", cooldown);
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		cooldown = compound.getInt("silocooldown");
	}
	
	@Override
	public ActionResultType use(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.getItem() == BallistixItems.ITEM_RADARGUN.get() || handStack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {
			return ActionResultType.FAIL;
		}
		return super.use(player, hand, hit);
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

}
