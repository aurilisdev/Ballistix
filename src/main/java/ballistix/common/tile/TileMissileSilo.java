package ballistix.common.tile;

import ballistix.DeferredRegisters;
import electrodynamics.api.tile.ITickableTileBase;
import electrodynamics.api.tile.electric.IElectricTile;
import electrodynamics.api.tile.electric.IPowerProvider;
import electrodynamics.api.utilities.CachedTileOutput;
import electrodynamics.api.utilities.TransferPack;
import electrodynamics.common.tile.generic.GenericTileInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileMissileSilo extends GenericTileInventory implements ITickableTileBase, IPowerProvider, IElectricTile {
	public static final int[] SLOTS_INPUT = new int[] { 0 };

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
		return 1;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.UP ? SLOTS_INPUT : SLOTS_EMPTY;
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
//		return new ContainerRadioisotopeGenerator(id, player, this, inventorydata);
		return null;
	}

	protected final IIntArray inventorydata = new IIntArray() {
		@Override
		public int get(int index) {
			switch (index) {
			default:
				return 0;
			}
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int size() {
			return 0;
		}
	};

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
//		return RadiationRegister.get(stack.getItem()) != RadiationRegister.NULL;
		return true;
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
