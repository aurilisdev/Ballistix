package ballistix.common.blast.tier3;

import java.util.Iterator;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.particle.ParticleOptionsShockwave;
import ballistix.client.shake.CameraShakeEffect;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.compatibility.nuclearscience.RadiationHandler;
import ballistix.prefab.utils.ParticleUtilities;
import ballistix.registers.BallistixSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;
import voltaic.api.radiation.RadiationSystem;
import voltaic.api.radiation.SimpleRadiationSource;
import voltaic.prefab.utilities.object.Location;

public class BlastNuclear extends BlastLasting implements IHasCustomRender {
    public BlastNuclear(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    threadRay = new ThreadDynamicRaycastBlast(world, position, (int) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE,
		    (float) BallistixConstants.EXPLOSIVE_NUCLEAR_ENERGY, null);
	    threadSimple = new ThreadSimpleBlast(world, position, (int) (BallistixConstants.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS),
		    Integer.MAX_VALUE, null, getBlastType().id());
	    threadSimple.strictnessAtEdges = 1.7;
	    threadRay.start();
	    threadSimple.start();
	    Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		    (float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE, false, BlockInteraction.DESTROY);

	    attackEntities((float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 2, ex);
	}
    }

    private Iterator<BlockPos> cachedIteratorRay;
    private Iterator<BlockPos> cachedIterator;

    private ThreadDynamicRaycastBlast threadRay;
    private ThreadSimpleBlast threadSimple;
    private int pertick = -1;
    private int perticksimple = -1;
    private boolean hasShaken;
    private boolean secondDamage = false;
    private boolean thirdDamage = false;
    private boolean sounded = false;

    @Override
    public boolean shouldRender() {
	return pertick > 0;
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	if (threadRay == null) {
	    return !world.isClientSide;
	}
	Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(),
		(float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE*3, false, BlockInteraction.DESTROY);
	if (callCount % 2 == 0) {
	    synchronized (threadRay.finishedBlocks) {
		if (pertick == -1) {
		    hasStarted = true;
		    attackEntities((float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 2, ex);
		    world.playSound(null, position, BallistixSounds.SOUND_NUCLEAREXPLOSION.get(), SoundSource.BLOCKS,
			    25, 1);
		    pertick = (int) (2400.0 * 360.0 / BallistixConstants.EXPLOSIVE_NUCLEAR_DURATION);
		}
		int finished = pertick;
		cachedIteratorRay = threadRay.finishedBlocks.iterator();
		while (cachedIteratorRay.hasNext()) {
		    if (finished-- < 0) {
			break;
		    }
		    BlockPos p = cachedIteratorRay.next();

		    switch (griefPreventionMethod) {
		    case GRIEF_DEFENDER:
			if (!GriefDefenderHandler.shouldHarmBlock(p)) {
			    continue;
			}
			break;
		    default:
			break;
		    }

		    BlockState state = Blocks.AIR.defaultBlockState();
		    double dis = new Location(p.getX(), 0, p.getZ())
			    .distance(new Location(position.getX(), 0, position.getZ()));
		    if (world.random.nextFloat() < 1 / 5.0 && dis < 15) {
			BlockPos offset = p.relative(Direction.DOWN);
			if (!threadRay.results.contains(offset) && world.random.nextFloat() < (15.0f - dis) / 15.0f) {
			    state = Blocks.FIRE.defaultBlockState();
			}
		    }
		    world.getBlockState(p).getBlock().wasExploded(world, p, ex);
		    world.setBlock(p, state, 3);
		    if (world instanceof ServerLevel serverlevel) {
			if (!sounded) {
			    serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(player -> {
				serverlevel.playSound(null, player.getX(), player.getY(), player.getZ(),
					BallistixSounds.SOUND_NUCLEAREXPLOSION.get(), // Change to your sound event
					SoundSource.PLAYERS, 25, 1.0F);
			    });
			    sounded = true;
			}
			if (world.random.nextFloat() < 1 / 20.0) {
			    serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false)
				    .forEach(pl -> NetworkHandler.CHANNEL.sendTo(
					    new PacketSpawnBlastParticle(p,
						    BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK),
					    ((ServerPlayer) pl).connection.connection,
					    NetworkDirection.PLAY_TO_CLIENT));
			}
		    }
		    cachedIteratorRay.remove();
		}
	    }
	}
	if (threadRay.isComplete && !secondDamage) {
	    attackEntities((float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 2, ex);
	    secondDamage = true;
	}
	if (threadSimple.isComplete && callCount % 2 == 0) {
	    if (!thirdDamage) {
		attackEntities((float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 2, ex);
		thirdDamage=true;
	    }
	    boolean add = switch (griefPreventionMethod) {
	    case GRIEF_DEFENDER -> GriefDefenderHandler.shouldAddParticle(position);
	    default -> true;
	    };

	    if (add) {
		RadiationSystem.addRadiationSource(world,
			new SimpleRadiationSource(150000.0, 2,
				(int) (BallistixConstants.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS), false, 86400 * 20,
				position, true, false));
	    }
	    if (perticksimple == -1) {
		cachedIterator = threadSimple.results.iterator();
	    }	    
	    perticksimple = (int) (4 * Math.PI * 0.5 * (Mth.clamp(callCount*callCount, 0, (int)(BallistixConstants.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS*BallistixConstants.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS))));

	    int finished = perticksimple;
	    while (cachedIterator.hasNext()) {
		if (finished-- < 0) {
		    break;
		}

		BlockPos pos = cachedIterator.next().offset(position);

		switch (griefPreventionMethod) {
		case GRIEF_DEFENDER:
		    if (!GriefDefenderHandler.shouldHarmBlock(pos)) {
			continue;
		    }
		    break;
		default:
		    break;
		}
		if (ModList.get().isLoaded(Ballistix.NUCLEAR_SCIENCE_ID) && pos.distSqr(position)
			/ (BallistixConstants.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS * BallistixConstants.EXPLOSIVE_NUCLEAR_RADIATION_RADIUS
				* 4) < 0.6 + 0.2 * world.random.nextDouble()) {
		    RadiationHandler.addNuclearExplosiveIrradidatedBlock(pos, world);
		}
	    }
	    if (!cachedIterator.hasNext()) {
		if (threadRay.isComplete)
		    attackEntities((float) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 2, ex);
		return ticksSinceBlastStart > 1500;
	    }
	}
	return false;

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
	double x = position.getX() + 0.5;
	double y = position.getY() + 0.5;
	double z = position.getZ() + 0.5;

	double initialSpeed = 1.5;
	if (ticksSinceBlastStart < 5) {
	    // Fireball
	    ParticleOptions particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 3f, -0.045f,
		    1500, true, true, 200, 0.98);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 500, 10, 90, initialSpeed, true);

	    // Centersmokes fast falling
	    initialSpeed = 2;
	    particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2.5f, 0.045f, 1500, true, 0.99);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 75, 0, 20, initialSpeed, true);
	    // Centersmokes veryslowfalling
	    initialSpeed = 2;
	    particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2.5f, 0.045f, 1500, true, 0.995);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 125, 0, 20, initialSpeed, true);
	    // Centersmokes living longer center
	    initialSpeed = 2;
	    particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2.5f, 0.015f, 1500, true, 0.97);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 125, 0, 20, initialSpeed, true);
	    // Centersmokes rising
	    initialSpeed = 2;
	    particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2.5f, -0.015f, 300, true, 0.97);
	    ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 0, 20, initialSpeed, true);

	    // Shockwave
	    if (ticksSinceBlastStart == 2) {
		initialSpeed = 1.5;
		particle = new ParticleOptionsShockwave().setParameters(1, 1, 1, 1, 3, 150, false, 1);
		ParticleUtilities.spawnParticleRing(particle, x, y + 25, z, 200, initialSpeed, false);
	    }
	} else if (ticksSinceBlastStart < 1500) {
	    // Centersmokes rising
	    initialSpeed = 0.7;
	    ParticleOptions particle = new ParticleOptionsBlastSmoke().setParameters(0.40625f / 0.8f, 0.40625f / 0.8f,
		    0.40625f / 0.8f, 3f, -0.045f, Mth.clamp(1500 - ticksSinceBlastStart, 1, 1500), true, 0.975);
	    ParticleUtilities.spawnParticleSphere(particle, x, y + 0.024f * ticksSinceBlastStart, z, 1, -20, 20,
		    initialSpeed, true);
	    particle = new ParticleOptionsBlastSmoke().setParameters(0.40625f / 0.8f, 0.40625f / 0.8f,
		    0.40625f / 0.8f, 3f, 0, Mth.clamp(1500 - ticksSinceBlastStart, 1, 1500), true, 0.975);
	    ParticleUtilities.spawnParticleSphere(particle, x, y + 0.024f * ticksSinceBlastStart, z, 1, -20, 20,
		    initialSpeed, true);
	    if (ticksSinceBlastStart < 1400) {
		// Centerfire rising
		initialSpeed = 0.5;
		particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 3f, -0.045f,
			Mth.clamp(1400 - ticksSinceBlastStart, 1, 1400), true, true, 500, 0.98);
		ParticleUtilities.spawnParticleSphere(particle, x, y + 0.033f * ticksSinceBlastStart, z, 1, -20, 20,
			initialSpeed, true);
	    }
	}
	// Shockwave
	double spawnSize = 3;
	double endSize = BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 5;
	int diff = (int) (endSize - spawnSize);
	if (ticksSinceBlastStart > diff)
	    return;
	double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart * 5 / (double) diff,
		spawnSize, endSize, 0.4);
	if (hasShaken)
	    return;
	Vec3 pos = new Vec3(x, y, z);
	double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
	double dist = Mth.abs((float) (realDistance - size));
	if (dist < 3) {
	    hasShaken = true;
	    CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(80.0, endSize, world.getGameTime(),
		    pos);
	    CameraShakeManager.addShake(effect);
	}
    }

    @Override
    public boolean isDoneCalculating() {
	if (world.isClientSide) {
	    return shouldRenderCustomClient;
	}
	return threadRay == null || threadRay.isComplete;
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.nuclear;
    }

}
