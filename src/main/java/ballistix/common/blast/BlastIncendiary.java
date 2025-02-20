package ballistix.common.blast;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlastIncendiary extends Blast implements IHasCustomRender {

    public BlastIncendiary(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 25, 1);
	}
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	hasStarted = true;
	if (world.isClientSide) {
	    produceParticles();
	    return true;
	}
	int radius = (int) Constants.EXPLOSIVE_INCENDIARY_RADIUS;
	for (int x = -radius; x <= radius; x++) {
	    for (int y = -radius; y <= radius; y++) {
		for (int z = -radius; z <= radius; z++) {
		    if (x * x + y * y + z * z < radius * radius) {
			int xActual = position.getX() + x;
			int yActual = position.getY() + y;
			int zActual = position.getZ() + z;
			BlockPos pos = new BlockPos(xActual, yActual, zActual);

			boolean add = switch (griefPreventionMethod) {
			case GRIEF_DEFENDER -> GriefDefenderHandler.shouldHarmBlock(pos);
			default -> true;
			};

			if (add && world.isEmptyBlock(pos) && !world.isEmptyBlock(pos.relative(Direction.DOWN))) {
			    world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
			}
		    }
		}
	    }
	}
	return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void produceParticles() {
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.incendiary;
    }

}
