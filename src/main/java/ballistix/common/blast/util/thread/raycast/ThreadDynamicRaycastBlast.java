package ballistix.common.blast.util.thread.raycast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ballistix.common.blast.util.thread.ThreadBlast;
import ballistix.common.settings.BallistixConfig;
import ballistix.compatibility.TessellateCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.block.HashDistanceBlockPos;

/**
 * 
 * The fact that this is multithreaded is legacy. This was optimal in 1.16 but
 * after minecraft made world changes more thread safe, using multithreading
 * (with inworld access) is actually ALOT slower. It is only worth it for pure
 * math (Like antimatter explosions).
 * 
 * This could probably be more optimised in newer versions if we remove the
 * sided explosion part completely, like how it was previously.
 * 
 **/

public class ThreadDynamicRaycastBlast extends ThreadBlast {

    public final IResistanceCallback callBack;
    public final HashSet<ThreadDynamicRaySideBlast> underBlasts = new HashSet<>();
    protected final Set<BlockPos> intermediateResults = Collections.synchronizedSet(new HashSet<>());
    public final Set<BlockPos> finishedBlocks = Collections.synchronizedSet(new HashSet<>());
    public boolean locked = false;

    public final long totalRayCount;
    public final ConcurrentHashMap<BlockPos, AtomicInteger> fortronRayHits = new ConcurrentHashMap<>();

    public ThreadDynamicRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source,
	    IResistanceCallback cb) {

	super(world, position, range, energy, source);

	callBack = cb;
	totalRayCount = 24L * range * range;

	setName("RaycastBlast Main Thread");
    }

    public ThreadDynamicRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source) {
	this(world, position, range, energy, source,
		new IResistanceCallbackImp(new Explosion(world, source, null, null, position.getX(), position.getY(),
			position.getZ(), range, false, BlockInteraction.DESTROY, ParticleTypes.EXPLOSION,
			ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE)));

    }

    @Override
    public synchronized void start() {
	if (TessellateCompat.isLoaded()) {
	    run();
	} else {
	    super.start();
	}
    }

    @Override
    public void run() {
	results.add(new HashDistanceBlockPos(position.getX(), position.getY(), position.getZ(), 0));
	for (Direction dir : Direction.values()) {
	    ThreadDynamicRaySideBlast sideBlast = new ThreadDynamicRaySideBlast(this, dir);
	    synchronized (underBlasts) {
		underBlasts.add(sideBlast);
	    }
	    if (shouldMultithread()) {
		sideBlast.start();
	    } else {
		sideBlast.run();
	    }
	}
	if (shouldMultithread()) {
	    while (hasUnderBlasts()) {
		HashSet<BlockPos> current = new HashSet<>();
		synchronized (intermediateResults) {
		    current.addAll(intermediateResults);
		    intermediateResults.clear();
		}
		synchronized (finishedBlocks) {
		    finishedBlocks.addAll(current);
		}
		try {
		    sleep(25);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		    break;
		}
	    }
	} else {
	    finishedBlocks.addAll(intermediateResults);
	}
	super.run();
    }

    private static boolean shouldMultithread() {
	return BallistixConfig.INSTANCE.SHOULD_MULTITHREAD_RAYTRACING.isTrue() && !TessellateCompat.isLoaded();
    }

    private boolean hasUnderBlasts() {
	synchronized (underBlasts) {
	    return !underBlasts.isEmpty();
	}
    }

    public static record IResistanceCallbackImp(Explosion explosion) implements IResistanceCallback {

	@Override
	public float getResistance(Level world, BlockPos position, BlockPos targetPosition, Entity source,
		BlockState block) {

	    if (!block.getFluidState().isEmpty()) {
		return 0.25f;
	    }
	    float resistance = block.getExplosionResistance(world, position, explosion);
	    if (resistance > 200) {
		resistance = 0.75f * (float) Math.sqrt(resistance);
	    }
	    return resistance;

	}
    }

}