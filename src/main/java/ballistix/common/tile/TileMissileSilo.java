package ballistix.common.tile;

import java.util.HashSet;

import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.entity.EntityMissile;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.network.SiloRegistry;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixItems;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.multiblock.IMultiblockTileNode;
import electrodynamics.common.multiblock.Subnode;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.object.CachedTileOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileMissileSilo extends GenericTile implements IMultiblockTileNode {
	public static final int[] SLOTS_INPUT = new int[] { 0, 1 };

	protected CachedTileOutput output1;
	protected CachedTileOutput output2;
	public Property<Integer> range = property(new Property<Integer>(PropertyType.Integer, "range")).set(-1).save();
	public Property<Integer> frequency = property(new Property<Integer>(PropertyType.Integer, "frequency")).set(-1).save();
	public Property<BlockPos> target = property(new Property<BlockPos>(PropertyType.BlockPos, "target")).save();

	private int cooldown = 100;
	public boolean shouldLaunch;

	public TileMissileSilo(BlockPos pos, BlockState state) {
		super(BallistixBlockTypes.TILE_MISSILESILO.get(), pos, state);
		addComponent(new ComponentTickable().tickServer(this::tickServer));
		addComponent(new ComponentInventory(this).size(2).faceSlots(Direction.UP, 0, 1).valid(this::isItemValidForSlot).shouldSendInfo());
		addComponent(new ComponentPacketHandler());
		addComponent(new ComponentContainerProvider("container.missilesilo").createMenu((id, player) -> new ContainerMissileSilo(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));

	}

	protected void tickServer(ComponentTickable tickable) {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		if (target.get() == null) {
			target.set(getBlockPos());
		}
		ItemStack it = inv.getItem(0);
		if (it.getItem() == BallistixItems.ITEM_MISSILECLOSERANGE.get()) {
			if (range.get() != 0) {
				range.set(0);
			}
		} else if (it.getItem() == BallistixItems.ITEM_MISSILEMEDIUMRANGE.get()) {
			if (range.get() != 1) {
				range.set(1);
			}
		} else if (it.getItem() == BallistixItems.ITEM_MISSILELONGRANGE.get()) {
			if (range.get() != 2) {
				range.set(2);
			}
		} else if (range.get() != -1) {
			range.set(-1);
		}
		cooldown--;
		if (cooldown < 0 && level.getLevelData().getDayTime() % 20 == 0) {
			ItemStack exp = inv.getItem(1);
			if (exp.getItem() instanceof BlockItemDescriptable des) {
				if (des.getBlock() instanceof BlockExplosive && range.get() >= 0 && exp.getCount() > 0) {
					boolean hasSignal = false;
					if (level.getBestNeighborSignal(getBlockPos()) > 0) {
						hasSignal = true;
					}
					if (!hasSignal) {
						for (Subnode node : getSubNodes()) {
							BlockPos off = worldPosition.offset(node.pos);
							if (level.getBestNeighborSignal(off) > 0) {
								hasSignal = true;
								break;
							}
						}
					}
					if (hasSignal || shouldLaunch) {
						launch();
						shouldLaunch = false;
					}
				}
			}
		}
		SiloRegistry.registerSilo(this);
	}

	private void launch() {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		ItemStack exp = inv.getItem(1);
		ItemStack it = inv.getItem(0);
		if (exp.getItem() instanceof BlockItemDescriptable des) {
			double dist = Math.sqrt(Math.pow(worldPosition.getX() - target.get().getX(), 2) + Math.pow(worldPosition.getY() - target.get().getY(), 2) + Math.pow(worldPosition.getZ() - target.get().getZ(), 2));
			if (range.get() == 0 && dist < 3000 || range.get() == 1 && dist < 10000 || range.get() == 2) {
				EntityMissile missile = new EntityMissile(level);
				missile.setPos(getBlockPos().getX() + 1.0, getBlockPos().getY(), getBlockPos().getZ() + 1.0);
				missile.range = range.get();
				missile.target = target.get();
				missile.blastOrdinal = ((BlockExplosive) des.getBlock()).explosive.ordinal();
				exp.shrink(1);
				it.shrink(1);
				level.addFreshEntity(missile);
			}
			cooldown = 100;
		}
	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		Item it = stack.getItem();
		if (index == 1) {
			if (it instanceof BlockItemDescriptable des) {
				if (des.getBlock() instanceof BlockExplosive) {
					return true;
				}
			}
		} else if (index == 0) {
			return it == BallistixItems.ITEM_MISSILECLOSERANGE.get() || it == BallistixItems.ITEM_MISSILELONGRANGE.get() || it == BallistixItems.ITEM_MISSILEMEDIUMRANGE.get();
		}
		return false;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		SiloRegistry.unregisterSilo(this);
	}

	public void setFrequency(int frequency) {
		if (level != null) {
			if (!level.isClientSide) {
				SiloRegistry.unregisterSilo(this);
			}
		}
		this.frequency.set(frequency);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public HashSet<Subnode> getSubNodes() {
		return BlockMissileSilo.subnodes;
	}
}
