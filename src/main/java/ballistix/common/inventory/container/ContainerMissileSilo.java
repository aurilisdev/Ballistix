package ballistix.common.inventory.container;

import ballistix.DeferredRegisters;
import ballistix.common.block.BlockExplosive;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.PacketSetMissileData;
import ballistix.common.tile.TileMissileSilo;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.prefab.inventory.container.GenericContainer;
import electrodynamics.prefab.inventory.container.slot.SlotRestricted;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerMissileSilo extends GenericContainer<TileMissileSilo> {

	public ContainerMissileSilo(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(2));
	}

	public ContainerMissileSilo(int id, Inventory playerinv, Container inventory) {
		this(id, playerinv, inventory, new SimpleContainerData(7));
	}

	public ContainerMissileSilo(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
		super(DeferredRegisters.CONTAINER_MISSILESILO.get(), id, playerinv, inventory, inventorydata);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		addSlot(new SlotRestricted(inv, nextIndex(), 60, 17, DeferredRegisters.ITEM_MISSILECLOSERANGE.get(),
				DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get(), DeferredRegisters.ITEM_MISSILELONGRANGE.get()));
		addSlot(new SlotRestricted(inv, nextIndex(), 60, 50) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				Item it = stack.getItem();
				if (it instanceof BlockItemDescriptable des) {
					if (des.getBlock() instanceof BlockExplosive) {
						return true;
					}
				}
				return super.mayPlace(stack);
			}
		});
	}

	@OnlyIn(Dist.CLIENT)
	public void setCoord(String valX, String valY, String valZ, String valFrequency) {
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
		Integer frequency = -1;
		try {
			frequency = Integer.parseInt(valFrequency);
		} catch (Exception e) {
			// Filler
		}
		if (getHostFromIntArray() != null) {
			NetworkHandler.CHANNEL
					.sendToServer(new PacketSetMissileData(getHostFromIntArray().getBlockPos(), new BlockPos(triedX, triedY, triedZ), frequency));
		}
	}

}
