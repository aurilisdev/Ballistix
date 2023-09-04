package ballistix.common.tile;

import org.jetbrains.annotations.NotNull;

import ballistix.References;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.entity.EntityMissile;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.item.ItemMissile;
import ballistix.common.network.SiloRegistry;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.common.block.VoxelShapes;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.world.ForgeChunkManager;

public class TileMissileSilo extends GenericTile implements IMultiblockParentTile {

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
	public Property<Integer> hasRedstoneSignal = property(new Property<>(PropertyType.Integer, "hasredstonesignal", 0x00000));

	private int cooldown = 100;
	public boolean shouldLaunch = false;

	public TileMissileSilo(BlockPos pos, BlockState state) {
		super(BallistixBlockTypes.TILE_MISSILESILO.get(), pos, state);
		addComponent(new ComponentDirection(this));
		addComponent(new ComponentTickable(this).tickServer(this::tickServer));
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(2)).faceSlots(Direction.UP, 0, 1).valid(this::isItemValidForSlot));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentContainerProvider("container.missilesilo", this).createMenu((id, player) -> new ContainerMissileSilo(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));

	}

	protected void tickServer(ComponentTickable tickable) {

		if (target.get() == null) {
			target.set(getBlockPos());
		}

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		if (range.get() == 0 || !hasExplosive.get() || (hasRedstoneSignal.get() == 0 && !shouldLaunch)) {
			return;
		}

		shouldLaunch = false;

		ComponentInventory inv = getComponent(ComponentType.Inventory);
		ItemStack explosive = inv.getItem(1);
		ItemStack mis = inv.getItem(0);

		double dist = Math.sqrt(Math.pow(worldPosition.getX() - target.get().getX(), 2) + Math.pow(worldPosition.getY() - target.get().getY(), 2) + Math.pow(worldPosition.getZ() - target.get().getZ(), 2));

		if (range.get() == 0 || range.get() < dist) {
			return;
		}

		EntityMissile missile = new EntityMissile(level);
		missile.setPos(getBlockPos().getX() + 1.0, getBlockPos().getY(), getBlockPos().getZ() + 1.0);
		missile.range = ((ItemMissile) mis.getItem()).missile.ordinal();
		missile.target = target.get();
		missile.blastOrdinal = ((BlockExplosive) ((BlockItemDescriptable) explosive.getItem()).getBlock()).explosive.ordinal();
		explosive.shrink(1);
		mis.shrink(1);
		inv.setChanged();
		level.addFreshEntity(missile);

		cooldown = 100;

	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		Item item = stack.getItem();

		if (index == 0) {
			return item instanceof ItemMissile;
		}
		if (index == 1) {
			return item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive;
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

		ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, worldPosition, chunkPos.x, chunkPos.z, false, true);

	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		if (level.isClientSide) {
			return;
		}
		ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, worldPosition, chunkPos.x, chunkPos.z, true, true);
	}

	@Override
	public void onNeightborChanged(BlockPos neighbor) {
		if (level.isClientSide) {
			return;
		}
		if (level.hasNeighborSignal(getBlockPos())) {
			setRedstoneSignal(0);
		} else {
			clearRedstoneSignal(0);
		}

	}

	@Override
	public void onSubnodeNeighborChange(TileMultiSubnode subnode, BlockPos subnodeChangingNeighbor) {
		if (level.isClientSide || subnodeChangingNeighbor.equals(getBlockPos())) {
			return;
		}
		if (level.hasNeighborSignal(subnode.getBlockPos())) {
			setRedstoneSignal(subnode.nodeIndex.getIndex() + 1);
		} else {
			clearRedstoneSignal(subnode.nodeIndex.getIndex() + 1);
		}
	}

	private void clearRedstoneSignal(int index) {
		int redstone = hasRedstoneSignal.get() & ~(1 << index);
		hasRedstoneSignal.set(redstone);

	}

	private void setRedstoneSignal(int index) {
		int redstone = hasRedstoneSignal.getIndex() | (1 << index);
		hasRedstoneSignal.set(redstone);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public Subnode[] getSubNodes() {

		return switch (this.<ComponentDirection>getComponent(ComponentType.Direction).getDirection()) {
		case EAST -> BlockMissileSilo.SUBNODES_EAST;
		case WEST -> BlockMissileSilo.SUBNODES_WEST;
		case NORTH -> BlockMissileSilo.SUBNODES_NORTH;
		case SOUTH -> BlockMissileSilo.SUBNODES_SOUTH;
		default -> BlockMissileSilo.SUBNODES_SOUTH;
		};

	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int index) {
		if (index == 0 || index == -1) {

			ItemStack missile = inv.getItem(0);

			if (missile.isEmpty()) {
				range.set(0);
			}

			if (missile.getItem() instanceof ItemMissile item) {

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

		if (index == 1 || index == -1) {

			ItemStack explosive = inv.getItem(1);

			if (!explosive.isEmpty() && explosive.getItem() instanceof BlockItemDescriptable blockItem && blockItem.getBlock() instanceof BlockExplosive) {
				hasExplosive.set(true);
			} else {
				hasExplosive.set(false);
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
	public void saveAdditional(@NotNull CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putInt("silocooldown", cooldown);
		nbt.putBoolean("shouldlaunch", shouldLaunch);
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		cooldown = nbt.getInt("silocooldown");
		shouldLaunch = nbt.getBoolean("shouldlaunch");
	}

	@Override
	public InteractionResult use(Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.getItem() == BallistixItems.ITEM_RADARGUN.get() || handStack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {
			return InteractionResult.FAIL;
		}
		return super.use(player, hand, result);
	}

	@Override
	public void onSubnodeDestroyed(TileMultiSubnode arg0) {
		level.destroyBlock(worldPosition, true);
	}

	@Override
	public InteractionResult onSubnodeUse(Player player, InteractionHand hand, BlockHitResult hit, TileMultiSubnode subnode) {
		return use(player, hand, hit);
	}

	static {
		VoxelShapes.registerShape(BallistixBlocks.blockMissileSilo, Block.box(0, 0, 0, 16, 1, 16), Direction.SOUTH);
	}

}
