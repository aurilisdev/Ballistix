package ballistix.common.blast.tier2;

import java.util.Iterator;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.shake.CameraShakeEffect;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.raycast.ThreadRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.prefab.utils.ParticleUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlastBreaching extends BlastLasting implements IHasCustomRender {

	private ThreadRaycastBlast thread;
	private Iterator<BlockPos> iterator;
	private int pertick = -1;

	public BlastBreaching(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadRaycastBlast(world, position, (int) BallistixConstants.EXPLOSIVE_BREACHING_SIZE, (float) BallistixConstants.EXPLOSIVE_BREACHING_ENERGY, null);
			thread.start();
			world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, (float) BallistixConstants.EXPLOSIVE_BREACHING_SIZE, ExplosionInteraction.BLOCK);
			world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 25, 1);
		}
	}

	@Override
	public boolean doExplode(int callCount) {
		hasStarted = true;
		super.doExplode(callCount);
		if (thread == null) {
			return ticksSinceBlastStart > BallistixConstants.EXPLOSIVE_BREACHING_SIZE * 3;
		}
		if (world.isClientSide || !thread.isComplete) {
			return ticksSinceBlastStart > BallistixConstants.EXPLOSIVE_BREACHING_SIZE * 3;
		}

		if (pertick == -1) {
			hasStarted = true;
			pertick = (int) (thread.results.size() * 1.5 / BallistixConstants.EXPLOSIVE_BREACHING_DURATION + 1);
			iterator = thread.results.iterator();
		}
		int finished = pertick;
		while (iterator.hasNext()) {
			if (finished-- < 0) {
				break;
			}
			BlockPos p = new BlockPos(iterator.next()).offset(position);
			BlockState state = world.getBlockState(p);

			if (state.isAir()) {
				continue;
			}

			boolean shouldDestroy = true;

			switch (griefPreventionMethod) {
			case NONE:
				break;
			case GRIEF_DEFENDER:
				shouldDestroy = GriefDefenderHandler.shouldHarmBlock(p);
				break;
			case SABER_FACTIONS:
				break;
			}

			if (!shouldDestroy) {
				continue;
			}

			world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
		}

		return ticksSinceBlastStart > BallistixConstants.EXPLOSIVE_BREACHING_SIZE * 3;
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.breaching;
	}

	private boolean hasShaken;

	@Override
	@OnlyIn(Dist.CLIENT)
	public void produceParticles() {
		double x = position.getX() + 0.5;
		double y = position.getY() - 2;
		double z = position.getZ() + 0.5;
		if (ticksSinceBlastStart == 1) {
			double initialSpeed = 0.4;
			// Fireball
			ParticleOptions particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, -0.045f, 200, true, true, 20, 0.95);
			ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 40, 90, initialSpeed, true);

			// Centersmokes
			initialSpeed = 0.4;
			particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, 0.033f, 200, true, 0.95);
			ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 0, 20, initialSpeed, true);

			// Centersmokes
			initialSpeed = 0.4;
			particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, -0.033f, 200, true, 0.95);
			ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 0, 20, initialSpeed, true);
		}
		// Shockwave
		double spawnSize = 3;
		double endSize = BallistixConstants.EXPLOSIVE_BREACHING_SIZE * 5;
		int diff = (int) (endSize - spawnSize);
		if (ticksSinceBlastStart > diff)
			return;
		double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart * 2 / (double) diff, spawnSize, endSize, 0.2);
		if (hasShaken)
			return;
		Vec3 pos = new Vec3(x, y, z);
		double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
		double dist = Mth.abs((float) (realDistance - size));
		if (dist < 3) {
			hasShaken = true;
			CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(20.0, endSize, world.getGameTime(), pos);
			CameraShakeManager.addShake(effect);
		}
	}

	@Override
	public boolean isDoneCalculating() {
		if (world.isClientSide) {
			return shouldRenderCustomClient;
		}
		return true;
	}
}
