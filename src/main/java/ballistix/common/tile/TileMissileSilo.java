package ballistix.common.tile;

import java.util.HashSet;

import ballistix.DeferredRegisters;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.entity.EntityMissile;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.network.SiloRegistry;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.multiblock.IMultiblockTileNode;
import electrodynamics.common.multiblock.Subnode;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.object.CachedTileOutput;
import electrodynamics.prefab.utilities.object.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileMissileSilo extends GenericTile implements IMultiblockTileNode {
    public static final int[] SLOTS_INPUT = new int[] { 0, 1 };

    protected CachedTileOutput output1;
    protected CachedTileOutput output2;
    public int range = -1;
    private int cooldown = 100;
    public int frequency = -1;
    public Location target;
    public boolean shouldLaunch;

    public TileMissileSilo(BlockPos pos, BlockState state) {
	super(DeferredRegisters.TILE_MISSILESILO.get(), pos, state);
	addComponent(new ComponentTickable().tickServer(this::tickServer));
	addComponent(new ComponentInventory(this).size(2).faceSlots(Direction.UP, 0, 1).valid(this::isItemValidForSlot));
	addComponent(new ComponentPacketHandler().customPacketWriter(this::writePacket).customPacketReader(this::readPacket)
		.guiPacketReader(this::readPacket).guiPacketWriter(this::writePacket));
	addComponent(new ComponentContainerProvider("container.missilesilo")
		.createMenu((id, player) -> new ContainerMissileSilo(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));

    }

    protected void tickServer(ComponentTickable tickable) {
	ComponentInventory inv = getComponent(ComponentType.Inventory);
	ComponentPacketHandler packet = getComponent(ComponentType.PacketHandler);
	if (tickable.getTicks() % 20 == 1) {
	    packet.sendCustomPacket();
	}
	if (target == null) {
	    target = new Location(getBlockPos());
	    packet.sendCustomPacket();
	}
	ItemStack it = inv.getItem(0);
	if (it.getItem() == DeferredRegisters.ITEM_MISSILECLOSERANGE.get()) {
	    if (range != 0) {
		range = 0;
		packet.sendCustomPacket();
	    }
	} else if (it.getItem() == DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get()) {
	    if (range != 1) {
		range = 1;
		packet.sendCustomPacket();
	    }
	} else if (it.getItem() == DeferredRegisters.ITEM_MISSILELONGRANGE.get()) {
	    if (range != 2) {
		range = 2;
		packet.sendCustomPacket();
	    }
	} else if (range != -1) {
	    range = -1;
	    packet.sendCustomPacket();
	}
	cooldown--;
	if (cooldown < 0 && level.getLevelData().getDayTime() % 20 == 0) {
	    ItemStack exp = inv.getItem(1);
	    if (exp.getItem()instanceof BlockItemDescriptable des) {
		if (des.getBlock() instanceof BlockExplosive && range >= 0 && exp.getCount() > 0) {
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
	ItemStack exp = inv.getItem(1);
	ItemStack it = inv.getItem(0);
	if (exp.getItem()instanceof BlockItemDescriptable des) {
	    double dist = Math.sqrt(Math.pow(worldPosition.getX() - target.x(), 2) + Math.pow(worldPosition.getY() - target.y(), 2)
		    + Math.pow(worldPosition.getZ() - target.z(), 2));
	    if (range == 0 && dist < 3000 || range == 1 && dist < 10000 || range == 2) {
		EntityMissile missile = new EntityMissile(level);
		missile.setPos(getBlockPos().getX() + 1.0, getBlockPos().getY(), getBlockPos().getZ() + 1.0);
		missile.range = range;
		missile.target = target.toBlockPos();
		missile.blastOrdinal = ((BlockExplosive) des.getBlock()).explosive.ordinal();
		exp.shrink(1);
		it.shrink(1);
		level.addFreshEntity(missile);
	    }
	    cooldown = 100;
	}
    }

    protected void readPacket(CompoundTag nbt) {
	range = nbt.getInt("range");
	target = Location.readFromNBT(nbt, "target");
	setFrequency(nbt.getInt("frequency"));
    }

    protected void writePacket(CompoundTag tag) {
	tag.putInt("range", range);
	tag.putInt("frequency", frequency);
	if (target != null) {
	    target.writeToNBT(tag, "target");
	}
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
	readPacket(compound);
	super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
	super.load(compound);
	save(compound);
    }

    protected boolean isItemValidForSlot(int index, ItemStack stack) {
	Item it = stack.getItem();
	if (index == 1) {
	    if (it instanceof BlockItemDescriptable des) {
		if (des.getBlock() instanceof BlockExplosive) {
		    return true;
		}
	    }
	} else if (index == 0) {
	    return it == DeferredRegisters.ITEM_MISSILECLOSERANGE.get() || it == DeferredRegisters.ITEM_MISSILELONGRANGE.get()
		    || it == DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get();
	}
	return false;
    }

    @Override
    public void setRemoved() {
	super.setRemoved();
	SiloRegistry.unregisterSilo(this);
    }

    public void setFrequency(int frequency) {
	if (!level.isClientSide) {
	    SiloRegistry.unregisterSilo(this);
	    this.frequency = frequency;
	    SiloRegistry.registerSilo(this);
	} else {
	    this.frequency = frequency;
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
}
