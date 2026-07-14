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
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.prefab.utils.ParticleUtilities;
import ballistix.registers.BallistixSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;

public class BlastAntimatter extends BlastLasting implements IHasCustomRender {

	public BlastAntimatter(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_ANTIMATTER_RADIUS, Integer.MAX_VALUE, null, getBlastType().id());
			thread.start();
			world.playSound(null, position, BallistixSounds.SOUND_ANTIMATTEREXPLOSION.get(), SoundCategory.BLOCKS, 25, 1);
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
		Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_ANTIMATTER_RADIUS, false, Explosion.Mode.DESTROY);
		if (pertick == -1) {
			hasStarted = true;
			pertick = (int) (thread.results.size() * 1.5 / BallistixConstants.EXPLOSIVE_ANTIMATTER_DURATION + 1);
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
					world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
					break;
				case GRIEF_DEFENDER:
					GriefDefenderHandler.destroyBlock(block, ex, p, world);
					break;
				case SABER_FACTIONS:

					break;
				}
				if (world.random.nextFloat() < 1 / 30.0 && world instanceof ServerWorld) {
					ServerWorld serverlevel = (ServerWorld) world;
					serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK), pl.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
				}

			}
		}
		if (!iterator.hasNext()) {
			position = position.above().above();
			attackEntities((float) BallistixConstants.EXPLOSIVE_ANTIMATTER_RADIUS * 2, ex);
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
		IParticleData particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2f, -0.01f, 750, true, true, 120, 0.999);
		ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, -90, 90, BallistixConstants.EXPLOSIVE_ANTIMATTER_RADIUS / BallistixConstants.EXPLOSIVE_ANTIMATTER_DURATION * 2, true);
		// Shockwave
		double spawnSize = 3;
		double endSize = BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 5;
		int diff = (int) (endSize - spawnSize);
		if (ticksSinceBlastStart > diff)
			return;
		double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart * 5 / (double) diff, spawnSize, endSize, 0.4);
		if (hasShaken)
			return;
		Vector3d pos = new Vector3d(x, y, z);
		double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
		double dist = MathHelper.abs((float) (realDistance - size));
		if (dist < 3) {
			hasShaken = true;
			CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(BallistixConstants.EXPLOSIVE_ANTIMATTER_DURATION, endSize, world.getGameTime(), pos);
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
