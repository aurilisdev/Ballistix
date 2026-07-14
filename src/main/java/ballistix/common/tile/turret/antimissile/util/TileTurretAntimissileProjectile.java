package ballistix.common.tile.turret.antimissile.util;

import org.jetbrains.annotations.Nullable;

import ballistix.api.turret.ITarget;
import ballistix.common.tile.radar.TileFireControlRadar;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import voltaic.Voltaic;

public abstract class TileTurretAntimissileProjectile extends TileTurretAntimissile {

	public TileTurretAntimissileProjectile(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
		super(tileEntityTypeIn, worldPos, blockState, range, minRange, usage, rotationSpeedRadians, inaccuracy);
	}

	@Nullable
	@Override
	public Vec3 getTargetPosition(ITarget target) {

		float trackingSpeed = 0F;// radar.tracking.speed;
		Vec3 trackingVector = target.getTargetMovement();

		double timeToIntercept = TileFireControlRadar.getTimeToIntercept(target.getTargetLocation(), trackingVector, trackingSpeed, getProjectileSpeed(), getProjectileLaunchPosition());

		if (timeToIntercept <= 0) {
			return null;
		}

		return target.getTargetLocation().add(trackingVector.scale(trackingSpeed).scale(timeToIntercept));
	}

	// speed in units of ticks
	public abstract float getProjectileSpeed();

	public Vec3 getProjectileTrajectoryFromInaccuracy(double inaccuracy, double baseRange, double inaccuracyMultiplier, Vec3 launchPos, Vec3 interceptionPos) {

		double distanceToTarget = TileFireControlRadar.getDistanceToMissile(launchPos, interceptionPos);

		double deltaX = interceptionPos.x - launchPos.x;
		double deltaY = interceptionPos.y - launchPos.y;
		double deltaZ = interceptionPos.z - launchPos.z;

		double rangePenalty = 1.0;

		if (distanceToTarget > baseRange) {

			rangePenalty = (distanceToTarget - baseRange) / baseRange * inaccuracyMultiplier * Voltaic.RANDOM.nextDouble();

		}

		if (Voltaic.RANDOM.nextBoolean()) {
			deltaX = deltaX * (1.0 + inaccuracy * Voltaic.RANDOM.nextDouble());
		} else {
			deltaZ = deltaZ * (1.0 + inaccuracy * Voltaic.RANDOM.nextDouble());
		}

		if (rangePenalty < 1.0) {
			if (Voltaic.RANDOM.nextBoolean()) {
				deltaZ = deltaZ * (1.0 + rangePenalty);
			} else {
				deltaX = deltaX * (1.0 + rangePenalty);
			}

		}

		return new Vec3(deltaX, deltaY, deltaZ).normalize();

	}

}
