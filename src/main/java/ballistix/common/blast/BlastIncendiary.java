package ballistix.common.blast;

import ballistix.common.block.SubtypeBlast;
import ballistix.common.settings.Constants;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlastIncendiary extends Blast {

    public BlastIncendiary(World world, BlockPos position) {
	super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
	if (!world.isRemote) {
	    int radius = (int) Constants.EXPLOSIVE_INCENDIARY_RADIUS;
	    for (int x = -radius; x <= radius; x++) {
		for (int y = -radius; y <= radius; y++) {
		    for (int z = -radius; z <= radius; z++) {
			if (x * x + y * y + z * z < radius * radius) {
			    int xActual = position.getX() + x;
			    int yActual = position.getY() + y;
			    int zActual = position.getZ() + z;
			    BlockPos pos = new BlockPos(xActual, yActual, zActual);
			    if (world.isAirBlock(pos) && !world.isAirBlock(pos.offset(Direction.DOWN))) {
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			    }
			}
		    }
		}
	    }
	}
	return true;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.incendiary;
    }

}
