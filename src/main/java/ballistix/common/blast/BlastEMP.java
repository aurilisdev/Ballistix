package ballistix.common.blast;

import java.util.Iterator;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.api.capability.types.electrodynamic.ICapabilityElectrodynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class BlastEMP extends Blast implements IHasCustomRenderer {

	public BlastEMP(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadSimpleBlast(world, position, (int) Constants.EXPLOSIVE_EMP_RADIUS, Integer.MAX_VALUE, null, true);
			thread.start();
		}
	}

	private ThreadSimpleBlast thread;
	private int pertick = -1;

	@Override
	public boolean shouldRender() {
		return pertick > 0;
	}

	private Iterator<BlockPos> cachedIterator;

	@Override
	public boolean doExplode(int callCount) {
		if (!world.isClientSide) {
			if (thread == null) {
				return true;
			}
			if (thread.isComplete) {
				hasStarted = true;
				if (pertick == -1) {
					pertick = (int) (thread.results.size() / Constants.EXPLOSIVE_ANTIMATTER_DURATION + 1);
					cachedIterator = thread.results.iterator();
				}
				int finished = pertick;
				while (cachedIterator.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(cachedIterator.next()).offset(position);
					BlockEntity entity = world.getBlockEntity(p);
					if (entity != null) {
						for (Direction dir : Direction.values()) {
							if (entity.getCapability(ElectrodynamicsCapabilities.ELECTRODYNAMIC, dir).isPresent()) {
								LazyOptional<ICapabilityElectrodynamic> c = entity.getCapability(ElectrodynamicsCapabilities.ELECTRODYNAMIC, dir);
								c.resolve().get().setJoulesStored(0);
							} else if (entity.getCapability(CapabilityEnergy.ENERGY, dir).isPresent()) {
								LazyOptional<IEnergyStorage> c = entity.getCapability(CapabilityEnergy.ENERGY, dir);
								c.resolve().get().extractEnergy(Integer.MAX_VALUE, false);
							}
						}
					} // TODO: Implement player inventory energy clearing
				}
				if (!cachedIterator.hasNext()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.emp;
	}

}
