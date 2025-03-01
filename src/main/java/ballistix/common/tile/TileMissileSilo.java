package ballistix.common.tile;

import javax.annotation.Nonnull;

import ballistix.References;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMissile;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.network.SiloRegistry;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.world.ForgeChunkManager;

public class TileMissileSilo extends GenericTile implements IMultiblockParentTile {

	public static final int MISSILE_SLOT = 0;
	public static final int EXPLOSIVE_SLOT = 1;

	public static final int COOLDOWN = 100;

	public Property<Integer> range = property(new Property<>(PropertyType.Integer, "range", 0));
	public Property<Boolean> hasExplosive = property(new Property<>(PropertyType.Boolean, "hasexplosive", false));
	public Property<Integer> frequency = property(new Property<>(PropertyType.Integer, "frequency", 0).onChange((prop, prevFreq) -> {

		if (level.isClientSide) {
			return;
		}

		int newFreq = prop.get();

		SiloRegistry.unregisterSilo(prevFreq, this);
		SiloRegistry.registerSilo(newFreq, this);

	}));
	public Property<BlockPos> target = property(new Property<>(PropertyType.BlockPos, "target", BlockPos.ZERO));

	private int cooldown = 100;
	public boolean shouldLaunch = false;

	public TileMissileSilo() {
		super(BallistixBlockTypes.TILE_MISSILESILO.get());

		addComponent(new ComponentTickable(this).tickServer(this::tickServer));
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(3)).valid(this::isItemValidForSlot));
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(120).maxJoules(Constants.MISSILESILO_USAGE * 20).setInputDirections(Direction.values()));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentContainerProvider("container.missilesilo", this).createMenu((id, player) -> new ContainerMissileSilo(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));

	}

	protected void tickServer(ComponentTickable tickable) {

		if (target.get() == null) {
			target.set(getBlockPos());
		}

		ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

		if (cooldown > 0 || electro.getJoulesStored() < Constants.MISSILESILO_USAGE) {
			cooldown--;
			return;
		}

		boolean hasRedstone = level.hasNeighborSignal(getBlockPos());

		for (Subnode subnode : getSubNodes()) {
			hasRedstone |= level.hasNeighborSignal(getBlockPos().offset(subnode.pos));
			if (hasRedstone) {
				break;
			}
		}

		if (range.get() == 0 || !hasExplosive.get() || (!hasRedstone && !shouldLaunch)) {
			return;
		}

		shouldLaunch = false;

		double dist = calculateDistance(worldPosition, target.get());

		if (range.get() == 0 || (range.get() > 0 && range.get() < dist)) {
			return;
		}

		ComponentInventory inv = getComponent(IComponentType.Inventory);
		ItemStack explosive = inv.getItem(EXPLOSIVE_SLOT);
		ItemStack mis = inv.getItem(MISSILE_SLOT);

		int ordinal = ((ItemMissile) mis.getItem()).missile.ordinal();

		VirtualMissile missile = new VirtualMissile(
				//
				new Vector3d(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5),
				//
				new Vector3d(0, 1, 0),
				//
				0.0F,
				//
				false,
				//
				getBlockPos().getX() + 0.5F,
				//
				getBlockPos().getZ() + 0.5F,
				//
				target.get(),
				//
				ordinal,
				//
				((BlockExplosive) ((BlockItemDescriptable) explosive.getItem()).getBlock()).explosive.ordinal(),
				//
				false,
				//
				frequency.get()
		//
		);

		MissileManager.addMissile(level.dimension(), missile);

		electro.joules(electro.getJoulesStored() - Constants.MISSILESILO_USAGE);

		inv.removeItem(MISSILE_SLOT, 1);
		inv.removeItem(EXPLOSIVE_SLOT, 1);

		level.playSound(null, getBlockPos(), BallistixSounds.SOUND_MISSILE_SILO.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);

		cooldown = COOLDOWN;

	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		Item item = stack.getItem();

		if (index == 0) {
			return item instanceof ItemMissile;
		}
		if (index == 1) {
			return item instanceof BlockItemDescriptable && ((BlockItemDescriptable) item).getBlock() instanceof BlockExplosive;
		} else if (index == 2) {
			return stack.getItem() == BallistixItems.ITEM_RADARGUN.get() || stack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get();
		}
		return false;
	}

	@Override
	public void onBlockDestroyed() {
		if (level.isClientSide) {
			return;
		}
		SiloRegistry.unregisterSilo(frequency.get(), this);

		ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerWorld) level, References.ID, worldPosition, chunkPos.x, chunkPos.z, false, true);

	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		if (level.isClientSide) {
			return;
		}
		ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerWorld) level, References.ID, worldPosition, chunkPos.x, chunkPos.z, true, true);
	}

	@Override
	public Subnode[] getSubNodes() {

		switch (getFacing()) {
		case EAST:
			return BlockMissileSilo.SUBNODES_EAST;
		case WEST:
			return BlockMissileSilo.SUBNODES_WEST;
		case NORTH:
			return BlockMissileSilo.SUBNODES_NORTH;
		case SOUTH:
		default:
			return BlockMissileSilo.SUBNODES_SOUTH;
		}

	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int index) {

		handleMissile(inv, index);

		handleExplosive(inv, index);
		
		handleSync(inv, index);

	}

	private void handleMissile(ComponentInventory inv, int index) {
		if (index == 0 || index == -1) {

			ItemStack missile = inv.getItem(0);

			if (missile.isEmpty()) {
				range.set(0);
				return;
			}

			if (missile.getItem() instanceof ItemMissile) {
				ItemMissile item = (ItemMissile) missile.getItem();

				switch (item.missile) {

				case closerange:
					range.set(Constants.CLOSERANGE_MISSILE_RANGE);
					break;
				case mediumrange:
					range.set(Constants.MEDIUMRANGE_MISSILE_RANGE);
					break;
				case longrange:
					range.set(Constants.LONGRANGE_MISSILE_RANGE);
					break;
				default:
					range.set(0);
					break;
				}

			} else {
				range.set(0);
			}

		}
	}

	private void handleExplosive(ComponentInventory inv, int index) {
		if (index == 1 || index == -1) {

			ItemStack explosive = inv.getItem(1);

			if (!explosive.isEmpty() && explosive.getItem() instanceof BlockItemDescriptable && ((BlockItemDescriptable) explosive.getItem()).getBlock() instanceof BlockExplosive) {
				hasExplosive.set(true);
			} else {
				hasExplosive.set(false);
			}

		}
	}

	private void handleSync(ComponentInventory inv, int index) {
		if (index == 2 || index == -1) {

			ItemStack sync = inv.getItem(2);

			if (sync.isEmpty()) {
				return;
			}

			if (sync.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {

				sync.getOrCreateTag().putInt(ItemLaserDesignator.FREQUENCY_KEY, frequency.get());

			} else if (sync.getItem() == BallistixItems.ITEM_RADARGUN.get()) {

				if (sync.getOrCreateTag().contains("xCoord")) {
					target.set(ItemRadarGun.getCoordiantes(sync));
					target.forceDirty();
				}

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
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putInt("silocooldown", cooldown);
		nbt.putBoolean("shouldlaunch", shouldLaunch);
		return super.save(nbt);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		cooldown = nbt.getInt("silocooldown");
		shouldLaunch = nbt.getBoolean("shouldlaunch");
	}

	@Override
	public ActionResultType use(PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.getItem() == BallistixItems.ITEM_RADARGUN.get() || handStack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {
			return ActionResultType.FAIL;
		}
		return super.use(player, hand, result);
	}

	@Override
	public void onSubnodeDestroyed(TileMultiSubnode arg0) {
		level.destroyBlock(worldPosition, true);
	}

	@Override
	public ActionResultType onSubnodeUse(PlayerEntity player, Hand hand, BlockRayTraceResult hit, TileMultiSubnode subnode) {
		return use(player, hand, hit);
	}
	
	@Override
	public <T> LazyOptional<T> getSubnodeCapability(@Nonnull Capability<T> cap, Direction side) {
		return getCapability(cap, side);
	}

	public static double calculateDistance(BlockPos fromPos, BlockPos toPos) {
		double deltaX = fromPos.getX() - toPos.getX();
		double deltaY = fromPos.getY() - toPos.getY();
		double deltaZ = fromPos.getZ() - toPos.getZ();

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

}