package ballistix.common.blast.thread.raycast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ballistix.common.blast.thread.ThreadBlast;
import electrodynamics.prefab.block.HashDistanceBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ThreadRaycastBlast extends ThreadBlast {

    public final IResistanceCallback callBack;
    public final HashSet<ThreadRaySideBlast> underBlasts = new HashSet<>();
    public final Set<BlockPos> resultsSync = Collections.synchronizedSet(new HashSet<>());
    public boolean locked = false;

    public ThreadRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source, IResistanceCallback cb) {
        super(world, position, range, energy, source);
        callBack = cb;
        setName("RaycastBlast Main Thread");
    }

    public ThreadRaycastBlast(Level world, BlockPos position, int range, float energy, Entity source) {
        this(world, position, range, energy, source, new IResistanceCallbackImp(new Explosion(world, source, null, null, position.getX(), position.getY(), position.getZ(), range, false, BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE)));

    }

    @Override
    @SuppressWarnings("java:S2184")
    public void run() {
        results.add(new HashDistanceBlockPos(position.getX(), position.getY(), position.getZ(), 0));
        for (Direction dir : Direction.values()) {
            ThreadRaySideBlast sideBlast = new ThreadRaySideBlast(this, dir);
            sideBlast.start();
            underBlasts.add(sideBlast);
        }
        while (!underBlasts.isEmpty()) {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        super.run();
    }

    public static record IResistanceCallbackImp(Explosion explosion) implements IResistanceCallback {

        @Override
        public float getResistance(Level world, BlockPos position, BlockPos targetPosition, Entity source, BlockState block) {

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