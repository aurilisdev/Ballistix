package ballistix.common.blast.tier3;

import java.util.Iterator;

import javax.annotation.Nullable;

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
import ballistix.prefab.utils.ParticleUtilities;
import ballistix.registers.BallistixSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;

public class BlastLargeAntimatter extends BlastLasting implements IHasCustomRender {

    public BlastLargeAntimatter(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_RADIUS,
		    Integer.MAX_VALUE, null, getBlastType().id());
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
	Explosion ex = new Explosion(world, blastEntity,
		DamageSource.explosion(owner instanceof LivingEntity ent ? ent : null), null, position.getX(),
		position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_RADIUS, false,
		BlockInteraction.DESTROY);
	if (pertick == -1) {
	    hasStarted = true;
	    pertick = (int) (thread.results.size() * 1.5 / BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_DURATION + 1);
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
		if (canBreakBlockState(world, state, p, owner)) {
		    block.wasExploded(world, p, ex);
		    world.setBlock(p, Blocks.AIR.defaultBlockState(),
			    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
		}
		if (world.random.nextFloat() < 1 / 120.0 && world instanceof ServerLevel serverlevel) {
		    serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
			    .forEach(pl -> NetworkHandler.CHANNEL.sendTo(
				    new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK),
				    pl.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
		}

	    }
	}
	if (!iterator.hasNext()) {
	    position = position.above().above();
	    attackEntities((float) BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_RADIUS * 2, ex);
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

	    ParticleOptions particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 20f, 0.015f, 750,
		    true, true, 120, 0.999);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 60, -90, 90,
		    BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_RADIUS
			    / BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_DURATION * 3,
		    true);

	} // Shockwave
	double spawnSize = 3;
	double endSize = BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 5;
	int diff = (int) (endSize - spawnSize);
	if (ticksSinceBlastStart > diff)
	    return;
	double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart * 5 / (double) diff,
		spawnSize, endSize, 0.275);
	if (hasShaken)
	    return;
	Vec3 pos = new Vec3(x, y, z);
	double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
	double dist = Mth.abs((float) (realDistance - size));
	if (dist < 3) {
	    hasShaken = true;
	    CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(
		    BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_DURATION, endSize, world.getGameTime(), pos);
	    CameraShakeManager.addShake(effect);
	}
    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.largeantimatter;
    }

    @Override
    public boolean isDoneCalculating() {
	if (world.isClientSide) {
	    return shouldRenderCustomClient;
	}
	return thread == null || thread.isComplete;
    }

}
