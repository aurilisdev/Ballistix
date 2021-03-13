package ballistix.common.inventory.container;

import ballistix.DeferredRegisters;
import ballistix.common.block.BlockExplosive;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.PacketSetMissileData;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.inventory.container.GenericContainer;
import electrodynamics.common.inventory.container.slot.SlotRestricted;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerMissileSilo extends GenericContainer<TileMissileSilo> {

    public ContainerMissileSilo(int id, PlayerInventory playerinv) {
	this(id, playerinv, new Inventory(2));
    }

    public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory) {
	this(id, playerinv, inventory, new IntArray(7));
    }

    public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
	super(DeferredRegisters.CONTAINER_MISSILESILO.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
	addSlot(new SlotRestricted(inv, nextIndex(), 103, 14, DeferredRegisters.ITEM_MISSILECLOSERANGE.get(),
		DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get(), DeferredRegisters.ITEM_MISSILELONGRANGE.get()));
	addSlot(new SlotRestricted(inv, nextIndex(), 103, 50) {
	    @Override
	    public boolean isItemValid(ItemStack stack) {
		Item it = stack.getItem();
		if (it instanceof BlockItemDescriptable) {
		    BlockItemDescriptable des = (BlockItemDescriptable) it;
		    if (des.getBlock() instanceof BlockExplosive) {
			return true;
		    }
		}
		return super.isItemValid(stack);
	    }
	});
    }

    @OnlyIn(Dist.CLIENT)
    public void setCoord(String valX, String valY, String valZ) {
	Integer triedX = 0;
	try {
	    triedX = Integer.parseInt(valX);
	} catch (Exception e) {
	    // Filler
	}
	Integer triedY = 0;
	try {
	    triedY = Integer.parseInt(valY);
	} catch (Exception e) {
	    // Filler
	}
	Integer triedZ = 0;
	try {
	    triedZ = Integer.parseInt(valZ);
	} catch (Exception e) {
	    // Filler
	}
	if (getHostFromIntArray() != null) {
	    NetworkHandler.CHANNEL.sendToServer(
		    new PacketSetMissileData(getHostFromIntArray().getPos(), new BlockPos(triedX, triedY, triedZ)));
	}
    }
}
