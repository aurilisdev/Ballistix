package ballistix.common.tile;

import java.util.HashSet;

import org.jetbrains.annotations.NotNull;

import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.entity.EntityMissile;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.item.ItemMissile;
import ballistix.common.network.SiloRegistry;
import ballistix.common.settings.Constants;
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
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class TileMissileSilo extends GenericTile implements IMultiblockTileNode {

	public Property<Integer> range = property(new Property<>(PropertyType.Integer, "range", 0));
	public Property<Integer> prevFrequency = property(new Property<>(PropertyType.Integer, "previousfrequency", 0));
	public Property<Integer> frequency = property(new Property<>(PropertyType.Integer, "frequency", 0).onChange(prop -> {

		if (level.isClientSide) {
			return;
		}

		int currFreq = prop.get();
		int prevFreq = prevFrequency.get();
		
		SiloRegistry.unregisterSilo(prevFreq, this);
		SiloRegistry.registerSilo(currFreq, this);
		
		prevFrequency.set(prevFreq);

	}));
	public Property<BlockPos> target = property(new Property<>(PropertyType.BlockPos, "target", BlockPos.ZERO));

	private int cooldown = 100;
	public boolean shouldLaunch = false;

	public TileMissileSilo(BlockPos pos, BlockState state) {
		super(BallistixBlockTypes.TILE_MISSILESILO.get(), pos, state);
		addComponent(new ComponentTickable().tickServer(this::tickServer));
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(2)).faceSlots(Direction.UP, 0, 1).valid(this::isItemValidForSlot));
		addComponent(new ComponentPacketHandler());
		addComponent(new ComponentContainerProvider("container.missilesilo").createMenu((id, player) -> new ContainerMissileSilo(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));

	}

	protected void tickServer(ComponentTickable tickable) {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		if (target.get() == null) {
			target.set(getBlockPos());
		}
		if(cooldown > 0) {
			cooldown --;
		} else if(level.getLevelData().getDayTime() % 20 == 0) {
			ItemStack exp = inv.getItem(1);
			if (exp.getItem() instanceof BlockItemDescriptable des) {
				if (des.getBlock() instanceof BlockExplosive && range.get() != 0 && exp.getCount() > 0) {
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
	}

	private void launch() {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		ItemStack explosive = inv.getItem(1);
		ItemStack mis = inv.getItem(0);

		if (mis.isEmpty()) {
			return;
		}

		if (explosive.getItem() instanceof BlockItemDescriptable des) {
			double dist = Math.sqrt(Math.pow(worldPosition.getX() - target.get().getX(), 2) + Math.pow(worldPosition.getY() - target.get().getY(), 2) + Math.pow(worldPosition.getZ() - target.get().getZ(), 2));

			if (range.get() < 0 || range.get() >= dist) {

				EntityMissile missile = new EntityMissile(level);
				missile.setPos(getBlockPos().getX() + 1.0, getBlockPos().getY(), getBlockPos().getZ() + 1.0);
				missile.range = ((ItemMissile) mis.getItem()).missile.ordinal();
				missile.target = target.get();
				missile.blastOrdinal = ((BlockExplosive) des.getBlock()).explosive.ordinal();
				explosive.shrink(1);
				mis.shrink(1);
				level.addFreshEntity(missile);

			}
			cooldown = 100;
		}
	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		Item item = stack.getItem();

		if (index == 0) {
			return item instanceof ItemMissile;
		} else if (index == 1) {
			return item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive;
		}
		return false;
	}

	@Override
	public void onBlockDestroyed() {
		if(!level.isClientSide) {
			SiloRegistry.unregisterSilo(frequency.get(), this);
		}
		
	}

	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public HashSet<Subnode> getSubNodes() {
		return BlockMissileSilo.subnodes;
	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int index) {
		if (index == 0 || index == -1) {

			ItemStack missile = inv.getItem(0);

			if (missile.isEmpty()) {
				range.set(0);
			}

			if (missile.getItem() instanceof ItemMissile item) {
				
				switch(item.missile) {
				
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

	@Override
	public void onLoad() {
		super.onLoad();
		if(!level.isClientSide) {
			SiloRegistry.registerSilo(frequency.get(), this);
		}
	}

	@Override
	public void saveAdditional(@NotNull CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putInt("silocooldown", cooldown);
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		cooldown = nbt.getInt("silocooldown");
	}

	@Override
	public InteractionResult use(Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.getItem() == BallistixItems.ITEM_RADARGUN.get() || handStack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {
			return InteractionResult.FAIL;
		}
		return super.use(player, hand, result);
	}
}
