package ballistix.common.blast.tier1;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlastIncendiary extends Blast implements IHasCustomRender {

	public BlastIncendiary(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 25, 1);
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
		int radius = (int) BallistixConstants.EXPLOSIVE_INCENDIARY_RADIUS;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					if (x * x + y * y + z * z < radius * radius) {
						int xActual = position.getX() + x;
						int yActual = position.getY() + y;
						int zActual = position.getZ() + z;
						BlockPos pos = new BlockPos(xActual, yActual, zActual);

						boolean add = false;
						switch (griefPreventionMethod) {
						case GRIEF_DEFENDER:
							add = GriefDefenderHandler.shouldHarmBlock(pos);
							break;
						default:
							add = true;
							break;
						}

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
