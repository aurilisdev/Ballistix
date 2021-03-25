package ballistix.common.tile;

import java.util.HashSet;

import ballistix.DeferredRegisters;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.entity.EntityMissile;
import ballistix.common.inventory.container.ContainerMissileSilo;
import electrodynamics.api.tile.GenericTileTicking;
import electrodynamics.api.tile.components.ComponentType;
import electrodynamics.api.tile.components.type.ComponentContainerProvider;
import electrodynamics.api.tile.components.type.ComponentInventory;
import electrodynamics.api.tile.components.type.ComponentPacketHandler;
import electrodynamics.api.tile.components.type.ComponentTickable;
import electrodynamics.api.utilities.object.CachedTileOutput;
import electrodynamics.api.utilities.object.Location;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.multiblock.IMultiblockTileNode;
import electrodynamics.common.multiblock.Subnode;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileMissileSilo extends GenericTileTicking implements IMultiblockTileNode {
    public static final int[] SLOTS_INPUT = new int[] { 0, 1 };

    protected CachedTileOutput output1;
    protected CachedTileOutput output2;
    public int range = -1;
    private int cooldown = 100;
    public Location target;

    public TileMissileSilo() {
	super(DeferredRegisters.TILE_MISSILESILO.get());
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
	    target = new Location(getPos());
	    packet.sendCustomPacket();
	}
	ItemStack it = inv.getStackInSlot(0);
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
	if (target != null && cooldown < 0 && world.getWorldInfo().getDayTime() % 20 == 0) {
	    ItemStack exp = inv.getStackInSlot(1);
	    if (exp.getItem() instanceof BlockItemDescriptable) {
		BlockItemDescriptable des = (BlockItemDescriptable) exp.getItem();
		if (des.getBlock() instanceof BlockExplosive && range >= 0 && exp.getCount() > 0) {
		    boolean hasSignal = false;
		    if (world.getRedstonePowerFromNeighbors(getPos()) > 0) {
			hasSignal = true;
		    }
		    if (!hasSignal) {
			for (Subnode node : getSubNodes()) {
			    BlockPos off = pos.add(node.pos);
			    if (world.getRedstonePowerFromNeighbors(off) > 0) {
				hasSignal = true;
				break;
			    }
			}
		    }
		    if (hasSignal) {
			double dist = Math.sqrt(
				Math.pow(pos.getX() - target.x(), 2) + Math.pow(pos.getY() - target.y(), 2) + Math.pow(pos.getZ() - target.x(), 2));
			if (range == 0 && dist < 3000 || range == 1 && dist < 10000 || range == 2) {
			    EntityMissile missile = new EntityMissile(world);
			    missile.setPosition(getPos().getX() + 1.0, getPos().getY(), getPos().getZ() + 1.0);
			    missile.range = range;
			    missile.target = target.toBlockPos();
			    missile.blastOrdinal = ((BlockExplosive) des.getBlock()).explosive.ordinal();
			    exp.shrink(1);
			    it.shrink(1);
			    world.addEntity(missile);
			}
			cooldown = 100;
		    }
		}
	    }
	}
    }

    protected void readPacket(CompoundNBT nbt) {
	range = nbt.getInt("range");
	target = Location.readFromNBT(nbt, "target");
    }

    protected void writePacket(CompoundNBT tag) {
	tag.putInt("range", range);
	if (target != null) {
	    target.writeToNBT(tag, "target");
	}
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
	readPacket(compound);
	return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
	super.read(state, compound);
	write(compound);
    }

    protected boolean isItemValidForSlot(int index, ItemStack stack) {
	Item it = stack.getItem();
	if (index == 1) {
	    if (it instanceof BlockItemDescriptable) {
		BlockItemDescriptable des = (BlockItemDescriptable) it;
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
    public AxisAlignedBB getRenderBoundingBox() {
	return INFINITE_EXTENT_AABB;
    }

    @Override
    public HashSet<Subnode> getSubNodes() {
	return BlockMissileSilo.subnodes;
    }
}
