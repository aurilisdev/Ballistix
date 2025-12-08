package ballistix.common.blast.tier3;

import java.util.Iterator;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.shake.CameraShakeEffect;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConfig;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.prefab.utils.ParticleUtilities;
import ballistix.registers.BallistixSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

public class BlastAntimatter extends BlastLasting implements IHasCustomRender {

	public BlastAntimatter(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadSimpleBlast(world, position, (int) BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_RADIUS.getAsDouble(), Integer.MAX_VALUE, null, getBlastType().id());
			thread.start();
			world.playSound(null, position, BallistixSounds.SOUND_ANTIMATTEREXPLOSION.get(), SoundSource.BLOCKS, 25, 1);
		}
	}

	private ThreadSimpleBlast thread;
	private int pertick = -1;

	@Override
	public boolean shouldRender() {
		return pertick > 0;
	}

	private Iterator<BlockPos> iterator;

	@Override
	public boolean doExplode(int callCount) {
		super.doExplode(callCount);
		if (thread == null) {
			return !world.isClientSide;
		}
		if (world.isClientSide || !thread.isComplete) {
			return false;
		}
		Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), (float) BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_RADIUS.getAsDouble(), false, BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
		if (pertick == -1) {
			hasStarted = true;
			pertick = (int) (thread.results.size() * 1.5 / BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_DURATION.get() + 1);
			iterator = thread.results.iterator();
		}
		int finished = pertick;
		while (iterator.hasNext()) {
			if (finished-- < 0) {
				break;
			}
			BlockPos p = new BlockPos(iterator.next()).offset(position);
			BlockState state = world.getBlockState(p);
			Block block = state.getBlock();

			if (!state.isAir() && state.getDestroySpeed(world, p) >= 0) {

				switch (griefPreventionMethod) {
				case NONE:
					block.wasExploded(world, p, ex);
					world.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_NEIGHBORS
					              | Block.UPDATE_CLIENTS
					              | Block.UPDATE_SUPPRESS_DROPS);
					break;
				case GRIEF_DEFENDER:
					GriefDefenderHandler.destroyBlock(block, ex, p, world);
					break;
				case SABER_FACTIONS:

					break;
				}
				if (world.random.nextFloat() < 1 / 75.0 && world instanceof ServerLevel serverlevel) {
					serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> PacketDistributor.sendToPlayer(pl, new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK)));
				}

			}
		}
		if (!iterator.hasNext()) {
			position = position.above().above();
			attackEntities((float) BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_RADIUS.getAsDouble() * 2, ex);
			return true;
		}
		return false;
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	private boolean hasShaken;

	@Override
	@OnlyIn(Dist.CLIENT)
	public void produceParticles() {
		if (ticksSinceBlastStart < 2)
			return;
		double x = position.getX() + 0.5;
		double y = position.getY() + 0.5;
		double z = position.getZ() + 0.5;
		// Fireball
		if (ticksSinceBlastStart < 10) {

		ParticleOptions particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 15f, 0.015f, 750, true, true, 120, 0.999);
		ParticleUtilities.spawnParticleSphere(particle, x, y, z, 50, -90, 90, BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_RADIUS.get() / BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_DURATION.get() * 2.5, true);
		}
		// Shockwave
		double spawnSize = 3;
		double endSize = BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.get() * 5;
		int diff = (int) (endSize - spawnSize);
		if (ticksSinceBlastStart > diff)
			return;
		double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart * 5 / (double) diff, spawnSize, endSize, 0.25);
		if (hasShaken)
			return;
		Vec3 pos = new Vec3(x, y, z);
		double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
		double dist = Mth.abs((float) (realDistance - size));
		if (dist < 3) {
			hasShaken = true;
			CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_DURATION.get(), endSize, world.getGameTime(), pos);
			CameraShakeManager.addShake(effect);
		}
	}

	@Override
	public IBlast getBlastType() {
		return SubtypeBlast.antimatter;
	}

	@Override
	public boolean isDoneCalculating() {
		if (world.isClientSide) {
			return shouldRenderCustomClient;
		}
		return thread == null || thread.isComplete;
	}

}
