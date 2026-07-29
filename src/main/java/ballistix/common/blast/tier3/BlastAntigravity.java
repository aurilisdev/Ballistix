package ballistix.common.blast.tier3;

import java.util.HashSet;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBallistixFallingBlock;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlastAntigravity extends BlastLasting {

    public BlastAntigravity(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public IBlast getBlastType() {
	return SubtypeBlast.antigravity;
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    world.playSound(null, position, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);

	if (world.isClientSide) {
	    return false;
	}

	liftEntities();
	launchBlock();

	return ticksSinceBlastStart >= BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_CHUNKDURATION.get();
    }

    private void liftEntities() {
	int radius = BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_RADIUS.get();
	double radiusSquared = radius * radius;

	Vec3 center = Vec3.atCenterOf(position);

	double minY = Math.max(world.getMinBuildHeight(), center.y - radius);

	double maxY = Math.min(world.getMaxBuildHeight(), center.y + Math.max(radius, 100));

	AABB bounds = new AABB(center.x - radius, minY, center.z - radius, center.x + radius, maxY, center.z + radius);
	for (Entity entity : world.getEntitiesOfClass(Entity.class, bounds)) {
	    if (!entity.isAlive()) {
		continue;
	    }

	    Vec3 entityCenter = entity.getBoundingBox().getCenter();

	    double deltaX = entityCenter.x - center.x;
	    double deltaZ = entityCenter.z - center.z;

	    if (deltaX * deltaX + deltaZ * deltaZ > radiusSquared) {
		continue;
	    }

	    double lift = Math.max(entity.getGravity(), 0.04)
		    * BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR.get();

	    entity.push(0, lift, 0);
	    entity.hurtMarked = true;
	}
    }

    private void launchBlock() {
	int radius = Math.max(1, (int) (BallistixConfig.INSTANCE.EXPLOSIVE_ANTIGRAVITY_RADIUS.get() * 0.8F));

	for (int attempt = 0; attempt < 16; attempt++) {
	    int x = world.random.nextIntBetweenInclusive(-radius, radius);
	    int y = world.random.nextIntBetweenInclusive(-radius, radius);
	    int z = world.random.nextIntBetweenInclusive(-radius, radius);

	    if (x * x + z * z > radius * radius) {
		continue;
	    }

	    BlockPos pos = position.offset(x, y, z);
	    BlockState state = world.getBlockState(pos);

	    if (state.isAir() || state.liquid() || state.getDestroySpeed(world, pos) < 0) {
		continue;
	    }

	    BlockState above = world.getBlockState(pos.above());

	    if (!above.isAir() && !above.liquid()) {
		continue;
	    }

	    EntityBallistixFallingBlock block = new EntityBallistixFallingBlock(world, pos.getX() + 0.5,
		    pos.getY() + 0.5, pos.getZ() + 0.5, state, new HashSet<>(), owner);

	    block.setDeltaMovement(0, 1.0, 0);

	    if (world.addFreshEntity(block)) {
		world.setBlock(pos, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
	    }

	    break;
	}
    }

    @Override
    public boolean isDoneCalculating() {
	return true;
    }

    @Override
    public boolean isInstantaneous() {
	return false;
    }
}