package ballistix.common.blast;

import java.util.Iterator;

import ballistix.SoundRegister;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import electrodynamics.api.sound.SoundAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlastAntimatter extends Blast implements IHasCustomRenderer {

	public BlastAntimatter(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadSimpleBlast(world, position, (int) Constants.EXPLOSIVE_ANTIMATTER_RADIUS, Integer.MAX_VALUE, null, true);
			thread.start();
		} else {
			SoundAPI.playSound(SoundRegister.SOUND_ANTIMATTEREXPLOSION.get(), SoundSource.BLOCKS, 25, 1, position);
		}
	}

	private ThreadSimpleBlast thread;
	private int pertick = -1;

	@Override
	public boolean shouldRender() {
		return pertick > 0;
	}

	private Iterator<BlockPos> forJDAWG;

	@Override
	public boolean doExplode(int callCount) {
		if (!world.isClientSide) {
			if (thread == null) {
				return true;
			}
			Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
					(float) Constants.EXPLOSIVE_ANTIMATTER_RADIUS, false, BlockInteraction.BREAK);
			if (thread.isComplete) {
				hasStarted = true;
				if (pertick == -1) {
					pertick = (int) (thread.results.size() * 1.5 / Constants.EXPLOSIVE_ANTIMATTER_DURATION + 1);
					forJDAWG = thread.results.iterator();
				}
				int finished = pertick;
				while (forJDAWG.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(forJDAWG.next()).offset(position);
					BlockState state = world.getBlockState(p);
					Block block = state.getBlock();

					if (state != Blocks.AIR.defaultBlockState() && state != Blocks.VOID_AIR.defaultBlockState()
							&& state.getDestroySpeed(world, p) >= 0) {
						block.wasExploded(world, p, ex);
						world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
					}
				}
				if (!forJDAWG.hasNext()) {
					position = position.above().above();
					attackEntities((float) Constants.EXPLOSIVE_ANTIMATTER_RADIUS * 2);
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
