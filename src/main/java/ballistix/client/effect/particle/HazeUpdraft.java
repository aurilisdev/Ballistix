package ballistix.client.effect.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Pulls a haze particle towards a rising column while it follows the ground and stays below a ceiling. */
final class HazeUpdraft {

    private static final double PULL_SPEED_SCALE = 0.5;
    private static final int FLOOR_STEP_DEPTH = 4;

    final Vec3 centre;
    final double liftRadius;
    final double pullSpeed;
    final double riseSpeed;
    final double top;
    private final BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
    private double floor;
    private float lift;
    private float previousLift;

    HazeUpdraft(Vec3 centre, double liftRadius, double floor, double top, double x, double z, int lifetime) {
	this.centre = centre;
	this.liftRadius = liftRadius;
	this.floor = floor;
	this.top = top;
	// Gives the outer haze enough time to reach the stem before it fades.
	double distance = Math.hypot(centre.x - x, centre.z - z);
	pullSpeed = Math.min(0.75, Math.max(distance, liftRadius * 0.5) / (lifetime * 0.45)) * PULL_SPEED_SCALE;
	riseSpeed = liftRadius / (lifetime * 0.14);
    }

    double groundHeight(float size) {
	return floor + size * BlastParticle.GROUND_HAZE_OFFSET;
    }

    float lift() {
	return lift;
    }

    float lift(float partialTick) {
	return Mth.lerp(partialTick, previousLift, lift);
    }

    void easeLiftTowards(float target) {
	previousLift = lift;
	lift = Mth.lerp(0.1f, lift, target);
    }

    /** Moves the tracked floor to the block column the haze is entering. Returns false if it cannot enter it. */
    boolean followFloor(ClientLevel level, int x, int z, float size) {
	if (!level.hasChunk(x >> 4, z >> 4))
	    return false;

	int floorBlock = Mth.ceil(floor) - 1;
	int bottom = Math.max(level.getMinBuildHeight(), floorBlock - FLOOR_STEP_DEPTH);
	for (int y = Math.min(level.getMaxBuildHeight() - 2, floorBlock + 1); y >= bottom; y--) {
	    probe.set(x, y, z);
	    BlockState state = level.getBlockState(probe);
	    if (!state.getFluidState().isEmpty())
		return false;
	    VoxelShape support = state.getCollisionShape(level, probe);
	    if (support.isEmpty())
		continue;
	    double supportTop = y + support.max(Direction.Axis.Y);

	    probe.move(Direction.UP);
	    BlockState above = level.getBlockState(probe);
	    if (supportTop + size * BlastParticle.GROUND_HAZE_OFFSET >= top || !above.getFluidState().isEmpty()
		    || !above.getCollisionShape(level, probe).isEmpty())
		return false;
	    floor = supportTop;
	    return true;
	}
	return false;
    }
}
