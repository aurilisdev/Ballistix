package ballistix.common.blast.util.thread.raycast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ballistix.common.blast.util.thread.ThreadBlast;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
	this(world, position, range, energy, source, new IResistanceCallbackImp(new Explosion(world, source, null, null,
		position.getX(), position.getY(), position.getZ(), range, false, BlockInteraction.DESTROY)));
    }

    @Override
    public void run() {
	results.add(new HashDistanceBlockPos(position.getX(), position.getY(), position.getZ(), 0));
	for (Direction dir : Direction.values()) {
	    ThreadDynamicRaySideBlast sideBlast = new ThreadDynamicRaySideBlast(this, dir);
	    underBlasts.add(sideBlast);
	    if (BallistixConstants.SHOULD_MULTITHREAD_RAYTRACING) {
		sideBlast.start();
	    } else {
		sideBlast.run();
	    }
	}
	if (BallistixConstants.SHOULD_MULTITHREAD_RAYTRACING) {
	    while (!underBlasts.isEmpty()) {
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