package ballistix.common.inventory.container;

import ballistix.DeferredRegisters;
import ballistix.common.block.BlockExplosive;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.inventory.container.GenericContainerInventory;
import electrodynamics.common.inventory.container.slot.GenericSlot;
import electrodynamics.common.inventory.container.slot.SlotRestricted;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerMissileSilo extends GenericContainerInventory {

	public ContainerMissileSilo(int id, PlayerInventory playerinv) {
		this(id, playerinv, new Inventory(2));
	}

	public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory) {
		this(id, playerinv, inventory, new IntArray(1));
	}

	public ContainerMissileSilo(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
		super(DeferredRegisters.CONTAINER_MISSILESILO.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
		addSlot(new SlotRestricted(inv, nextIndex(), 98, 14, DeferredRegisters.ITEM_MISSILECLOSERANGE.get(), DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get(), DeferredRegisters.ITEM_MISSILELONGRANGE.get()));
		addSlot(new GenericSlot(inv, nextIndex(), 98, 50) {
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
	public int getMissileType() {
		return inventorydata.get(0);
	}

}
