package ballistix.common.tile;

import java.util.HashSet;

import ballistix.DeferredRegisters;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.entity.EntityMissile;
import ballistix.common.inventory.container.ContainerMissileSilo;
import electrodynamics.api.tile.ITickableTileBase;
import electrodynamics.api.utilities.CachedTileOutput;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.multiblock.IMultiblockTileNode;
import electrodynamics.common.multiblock.Subnode;
import electrodynamics.common.tile.generic.GenericTileInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileMissileSilo extends GenericTileInventory implements ITickableTileBase, IMultiblockTileNode {
    public static final int[] SLOTS_INPUT = new int[] { 0, 1 };

    protected CachedTileOutput output1;
    protected CachedTileOutput output2;
    public int range = -1;
    private int cooldown = 100;
    public BlockPos target;

    public TileMissileSilo() {
	super(DeferredRegisters.TILE_MISSILESILO.get());
    }

    @Override
    public void tickServer() {
	if (target == null) {
	    target = getPos();
	}
	ItemStack it = getStackInSlot(0);
	if (it.getItem() == DeferredRegisters.ITEM_MISSILECLOSERANGE.get()) {
	    if (range != 0) {
		range = 0;
		sendCustomPacket();
	    }
	} else if (it.getItem() == DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get()) {
	    if (range != 1) {
		range = 1;
		sendCustomPacket();
	    }
	} else if (it.getItem() == DeferredRegisters.ITEM_MISSILELONGRANGE.get()) {
	    if (range != 2) {
		range = 2;
		sendCustomPacket();
	    }
	} else if (range != -1) {
	    range = -1;
	    sendCustomPacket();
	}
	cooldown--;
	if (target != null && cooldown < 0 && world.getWorldInfo().getDayTime() % 20 == 0) {
	    ItemStack exp = getStackInSlot(1);
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
			double dist = Math.sqrt(Math.pow(pos.getX() - target.getX(), 2)
				+ Math.pow(pos.getY() - target.getY(), 2) + Math.pow(pos.getZ() - target.getZ(), 2));
			if (range == 0 && dist < 3000 || range == 1 && dist < 10000 || range == 2) {
			    EntityMissile missile = new EntityMissile(world);
			    missile.setPosition(getPos().getX() + 1.0, getPos().getY(), getPos().getZ() + 1.0);
			    missile.range = range;
			    missile.target = new BlockPos(target);
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

    @Override
    public void readCustomPacket(CompoundNBT nbt) {
	super.readCustomPacket(nbt);
	range = nbt.getInt("range");
    }

    @Override
    public CompoundNBT writeCustomPacket() {
	CompoundNBT tag = super.writeCustomPacket();
	tag.putInt("range", range);
	return tag;
    }

    @Override
    public int getSizeInventory() {
	return 2;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
	return INFINITE_EXTENT_AABB;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
	return side == Direction.UP ? SLOTS_INPUT : SLOTS_EMPTY;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
	return new ContainerMissileSilo(id, player, this, getInventoryData());
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
	Item it = stack.getItem();
	if (index == 1) {
	    if (it instanceof BlockItemDescriptable) {
		BlockItemDescriptable des = (BlockItemDescriptable) it;
		if (des.getBlock() instanceof BlockExplosive) {
		    return true;
		}
	    }
	} else if (index == 0) {
	    return it == DeferredRegisters.ITEM_MISSILECLOSERANGE.get()
		    || it == DeferredRegisters.ITEM_MISSILELONGRANGE.get()
		    || it == DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get();
	}
	return false;
    }

    @Override
    public ITextComponent getDisplayName() {
	return new TranslationTextComponent("container.missilesilo");
    }

    @Override
    public HashSet<Subnode> getSubNodes() {
	return BlockMissileSilo.subnodes;
    }
}
