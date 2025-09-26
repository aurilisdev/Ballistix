package ballistix.common.blast.util.thread.raycast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ballistix.common.blast.util.thread.ThreadBlast;
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

public class ThreadDynamicRaycastBlast extends ThreadBlast {

	public final IResistanceCallback callBack;
	public final HashSet<ThreadDynamicRaySideBlast> underBlasts = new HashSet<>();
	protected final Set<BlockPos> intermediateResults = Collections.synchronizedSet(new HashSet<>());
	public final Set<BlockPos> finishedBlocks = Collections.synchronizedSet(new HashSet<>());
	public boolean locked = false;

	public ThreadDynamicRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source,
			IResistanceCallback cb) {
		super(world, position, range, energy, source);
		callBack = cb;
		setName("RaycastBlast Main Thread");
	}

	public ThreadDynamicRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source) {
		this(world, position, range, energy, source,
				new IResistanceCallbackImp(new Explosion(world, source, null, null, position.getX(), position.getY(),
						position.getZ(), range, false, BlockInteraction.DESTROY, ParticleTypes.EXPLOSION,
						ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE)));

	}

	@Override
	public void run() {
		results.add(new HashDistanceBlockPos(position.getX(), position.getY(), position.getZ(), 0));
		for (Direction dir : Direction.values()) {
			ThreadDynamicRaySideBlast sideBlast = new ThreadDynamicRaySideBlast(this, dir);
			sideBlast.start();
			underBlasts.add(sideBlast);
		}
		while (!underBlasts.isEmpty()) {
			HashSet<BlockPos> current = new HashSet<BlockPos>();
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