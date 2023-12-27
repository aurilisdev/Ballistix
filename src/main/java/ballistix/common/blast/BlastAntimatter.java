package ballistix.common.blast;

import java.util.Iterator;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixSounds;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.prefab.utilities.WorldUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlastAntimatter extends Blast implements IHasCustomRenderer {

	public BlastAntimatter(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadSimpleBlast(world, position, (int) Constants.EXPLOSIVE_ANTIMATTER_RADIUS, Integer.MAX_VALUE, null, true);
			thread.start();
		} else {
			SoundAPI.playSound(BallistixSounds.SOUND_ANTIMATTEREXPLOSION.get(), SoundCategory.BLOCKS, 25, 1, position);
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
					WorldUtils.fastRemoveBlockExplosion((ServerWorld) world, p);
				}
				if (!cachedIterator.hasNext()) {
					position = position.above().above();
					attackEntities((float) Constants.EXPLOSIVE_ANTIMATTER_RADIUS * 2, false);
					WorldUtils.clearChunkCache();
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
		return SubtypeBlast.antimatter;
	}

}