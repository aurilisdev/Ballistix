package ballistix.common.tile.turret.antimissile.util;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import ballistix.api.turret.ITarget;
import ballistix.common.tile.radar.TileFireControlRadar;
import electrodynamics.Electrodynamics;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.vector.Vector3d;

public abstract class TileTurretAntimissileProjectile extends TileTurretAntimissile {

    public TileTurretAntimissileProjectile(TileEntityType<?> tileEntityTypeIn, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
        super(tileEntityTypeIn, range, minRange, usage, rotationSpeedRadians, inaccuracy);
    }

    @Nullable
    @Override
    public Vector3d getTargetPosition(ITarget target) {

        float trackingSpeed = 0F;//radar.tracking.speed;
        Vector3d trackingVector = target.getTargetMovement();

        double timeToIntercept = TileFireControlRadar.getTimeToIntercept(target.getTargetLocation(), trackingVector, trackingSpeed, getProjectileSpeed(), getProjectileLaunchPosition());

        if (timeToIntercept <= 0) {
            return null;
        }

        return target.getTargetLocation().add(trackingVector.scale(trackingSpeed).scale(timeToIntercept));
    }

    // speed in units of ticks
    public abstract float getProjectileSpeed();

    public static Pair<Vector3d, Vector3d> getProjectileTrajectoryFromInaccuracy(double inaccuracy, double baseRange, double inaccuracyMultiplier, Vector3d launchPos, Vector3d interceptionPos) {

        double distanceToTarget = TileFireControlRadar.getDistanceToMissile(launchPos, interceptionPos);

        double deltaX = interceptionPos.x - launchPos.x;
        double deltaY = interceptionPos.y - launchPos.y;
        double deltaZ = interceptionPos.z - launchPos.z;

        double rangePenalty = 1.0;

        if (distanceToTarget > baseRange) {

            rangePenalty = ((distanceToTarget - baseRange) / baseRange) * inaccuracyMultiplier * Electrodynamics.RANDOM.nextDouble();

        }

        if(Electrodynamics.RANDOM.nextBoolean()) {
            deltaX = deltaX * (1.0 + inaccuracy * Electrodynamics.RANDOM.nextDouble());
        } else {
            deltaZ = deltaZ * (1.0 + inaccuracy * Electrodynamics.RANDOM.nextDouble());
        }

        if (rangePenalty < 1.0) {
            if(Electrodynamics.RANDOM.nextBoolean()) {
                deltaZ = deltaZ * (1.0 + rangePenalty);
            } else {
                deltaX = deltaX * (1.0 + rangePenalty);
            }

        }

        double sumXZ = deltaX * deltaX + deltaZ * deltaZ;

        double magXZ = Math.sqrt(sumXZ);

        if (magXZ <= 0) {
            magXZ = 1;
        }

        double thetaY = Math.atan(deltaY / magXZ);

        return Pair.of(new Vector3d(deltaX, deltaY, deltaZ).normalize(), new Vector3d(deltaX / magXZ, Math.sin(thetaY), deltaZ / magXZ));

    }

}
