package ballistix.common.blast;

import java.util.Iterator;

import ballistix.SoundRegister;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.prefab.utilities.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class BlastLargeAntimatter extends Blast implements IHasCustomRenderer {

	public BlastLargeAntimatter(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadSimpleBlast(world, position, (int) Constants.EXPLOSIVE_LARGEANTIMATTER_RADIUS, Integer.MAX_VALUE, null, true);
			thread.start();
		} else {
			SoundAPI.playSound(SoundRegister.SOUND_ANTIMATTEREXPLOSION.get(), SoundSource.BLOCKS, 50, 1, position);
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
					pertick = (int) (thread.results.size() / Constants.EXPLOSIVE_LARGEANTIMATTER_DURATION + 1);
					cachedIterator = thread.results.iterator();
				}
				int finished = pertick;
				while (cachedIterator.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(cachedIterator.next()).offset(position);
					WorldUtils.fastRemoveBlockExplosion((ServerLevel) world, p);
				}
				if (!cachedIterator.hasNext()) {
					WorldUtils.clearChunkCache();
					position = position.above().above();
					attackEntities((float) Constants.EXPLOSIVE_LARGEANTIMATTER_RADIUS * 2, false);
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
		return SubtypeBlast.largeantimatter;
	}

}
