package ballistix.common.tile;

import ballistix.DeferredRegisters;
import ballistix.common.block.BlockExplosive;
import ballistix.common.inventory.container.ContainerMissileSilo;
import electrodynamics.api.tile.ITickableTileBase;
import electrodynamics.api.tile.electric.IElectricTile;
import electrodynamics.api.tile.electric.IPowerProvider;
import electrodynamics.api.utilities.CachedTileOutput;
import electrodynamics.api.utilities.TransferPack;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.common.tile.generic.GenericTileInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileMissileSilo extends GenericTileInventory implements ITickableTileBase, IPowerProvider, IElectricTile {
	public static final int[] SLOTS_INPUT = new int[] { 0, 1 };

	protected CachedTileOutput output1;
	protected CachedTileOutput output2;

	public TileMissileSilo() {
		super(DeferredRegisters.TILE_MISSILESILO.get());
	}

	@Override
	public double getVoltage(Direction arg0) {
		return 120;
	}

	@Override
	public void tickServer() {
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.UP ? SLOTS_INPUT : SLOTS_EMPTY;
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new ContainerMissileSilo(id, player, this, inventorydata);
	}

	protected final IIntArray inventorydata = new IIntArray() {
		@Override
		public int get(int index) {
			switch (index) {
			case 0:
				return 0;
			default:
				return 0;
			}
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return 1;
		}
	};

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
			return it == DeferredRegisters.ITEM_MISSILECLOSERANGE.get() || it == DeferredRegisters.ITEM_MISSILELONGRANGE.get() || it == DeferredRegisters.ITEM_MISSILEMEDIUMRANGE.get();
		}
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.missilesilo");
	}

	@Override
	public TransferPack extractPower(TransferPack transfer, Direction from, boolean debug) {
		return TransferPack.EMPTY;
	}

	@Override
	public boolean canConnectElectrically(Direction direction) {
		return direction == Direction.UP || direction == Direction.DOWN;
	}

}
