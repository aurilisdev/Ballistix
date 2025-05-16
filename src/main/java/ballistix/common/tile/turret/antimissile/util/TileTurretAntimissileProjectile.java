package ballistix.common.tile.turret.antimissile.util;

import javax.annotation.Nullable;

import ballistix.api.turret.ITarget;
import ballistix.common.tile.radar.TileFireControlRadar;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.vector.Vector3d;
import voltaic.Voltaic;

public abstract class TileTurretAntimissileProjectile extends TileTurretAntimissile {

	public TileTurretAntimissileProjectile(TileEntityType<?> tileEntityTypeIn, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
		super(tileEntityTypeIn, range, minRange, usage, rotationSpeedRadians, inaccuracy);
	}

	@Nullable
	@Override
	public Vector3d getTargetPosition(ITarget target) {

		float trackingSpeed = 0F;// radar.tracking.speed;
		Vector3d trackingVector = target.getTargetMovement();

		double timeToIntercept = TileFireControlRadar.getTimeToIntercept(target.getTargetLocation(), trackingVector, trackingSpeed, getProjectileSpeed(), getProjectileLaunchPosition());

		if (timeToIntercept <= 0) {
			return null;
		}

		return target.getTargetLocation().add(trackingVector.scale(trackingSpeed).scale(timeToIntercept));
	}

	// speed in units of ticks
	public abstract float getProjectileSpeed();

	public Vector3d getProjectileTrajectoryFromInaccuracy(double inaccuracy, double baseRange, double inaccuracyMultiplier, Vector3d launchPos, Vector3d interceptionPos) {

		double distanceToTarget = TileFireControlRadar.getDistanceToMissile(launchPos, interceptionPos);

		double deltaX = interceptionPos.x - launchPos.x;
		double deltaY = interceptionPos.y - launchPos.y;
		double deltaZ = interceptionPos.z - launchPos.z;

		double rangePenalty = 1.0;

		if (distanceToTarget > baseRange) {

			rangePenalty = ((distanceToTarget - baseRange) / baseRange) * inaccuracyMultiplier * Voltaic.RANDOM.nextDouble();

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

		return new Vector3d(deltaX, deltaY, deltaZ).normalize();

	}

}
