package ballistix.common.blast.tier2;

import java.util.Iterator;

import javax.annotation.Nullable;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.effect.BlastEffects.Style;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import voltaic.prefab.utilities.object.Location;

public class BlastBreaching extends BlastLasting implements IHasCustomRender {

    private @Nullable ThreadDynamicRaycastBlast thread;
    private @Nullable Iterator<BlockPos> iterator;
    private int pertick = -1;

    public BlastBreaching(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadDynamicRaycastBlast(world, position,
		    (int) BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_SIZE.getAsDouble(),
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_ENERGY.getAsDouble(), null);
	    thread.run();

	    world.explode(blastEntity, world.damageSources().explosion(blastEntity, owner), null, position.getX() + 0.5,
		    position.getY() + 0.5, position.getZ() + 0.5,
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_SIZE.getAsDouble(), false,
		    ExplosionInteraction.BLOCK, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		    SoundEvents.GENERIC_EXPLODE);
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	hasStarted = true;
	super.doExplode(callCount);
	ThreadDynamicRaycastBlast thread = this.thread;
	if (thread == null || world.isClientSide || !thread.isComplete) {
	    return ticksSinceBlastStart > BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_SIZE.getAsDouble() * 3;
	}
	Explosion ex = new Explosion(world, blastEntity, world.damageSources().explosion(blastEntity, owner), null,
		position.getX(), position.getY(), position.getZ(),
		(float) BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_SIZE.getAsDouble() * 3, false,
		BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
		SoundEvents.GENERIC_EXPLODE);
	Iterator<BlockPos> iterator = this.iterator;
	if (pertick == -1) {
	    hasStarted = true;
	    pertick = (int) (thread.results.size() * 1.5
		    / BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_DURATION.getAsDouble() + 1);
	    this.iterator = iterator = thread.results.iterator();
	}
	int finished = pertick;
	if (iterator == null) {
	    return ticksSinceBlastStart > BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_SIZE.getAsDouble() * 3;
	}
	while (iterator.hasNext()) {
	    if (finished-- < 0) {
		break;
	    }
	    BlockPos p = iterator.next();
	    BlockState state = world.getBlockState(p);
	    if (!canBreakBlockState(world, state, p, owner)) {
		continue;
	    }
	    Block block = state.getBlock();
	    BlockState toPlace = Blocks.AIR.defaultBlockState();
	    double dis = new Location(p.getX(), 0, p.getZ())
		    .distance(new Location(position.getX(), 0, position.getZ()));
	    if (world.random.nextFloat() < 1 / (3 * Math.sqrt(dis))) {
		BlockPos offset = p.relative(Direction.DOWN);
		if (!thread.results.contains(offset)) {
		    toPlace = Blocks.FIRE.defaultBlockState();
		}
	    }
	    block.wasExploded(world, p, ex);
	    world.setBlock(p, toPlace, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS);
	}

	return ticksSinceBlastStart > BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_SIZE.getAsDouble() * 3;
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.breaching;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
	playDetonationEffects(Style.BREACHING, BallistixConfig.INSTANCE.EXPLOSIVE_BREACHING_SIZE.getAsDouble());
    }

    @Override
    public boolean isDoneCalculating() {
	if (world.isClientSide) {
	    return shouldRenderCustomClient;
	}
	return true;
    }
}
