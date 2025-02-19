package ballistix.common.tile.turret.antimissile.util;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import ballistix.api.turret.ITarget;
import ballistix.common.tile.radar.TileFireControlRadar;
import electrodynamics.Electrodynamics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class TileTurretAntimissileProjectile extends TileTurretAntimissile {

    public TileTurretAntimissileProjectile(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
        super(tileEntityTypeIn, worldPos, blockState, range, minRange, usage, rotationSpeedRadians, inaccuracy);
    }

    @Nullable
    @Override
    public Vec3 getTargetPosition(ITarget target) {

        float trackingSpeed = 0F;//radar.tracking.speed;
        Vec3 trackingVector = target.getTargetMovement();

        double timeToIntercept = TileFireControlRadar.getTimeToIntercept(target.getTargetLocation(), trackingVector, trackingSpeed, getProjectileSpeed(), getProjectileLaunchPosition());

        if (timeToIntercept <= 0) {
            return null;
        }

        return target.getTargetLocation().add(trackingVector.scale(trackingSpeed).scale(timeToIntercept));
    }

    // speed in units of ticks
    public abstract float getProjectileSpeed();

    public static Pair<Vec3, Vec3> getProjectileTrajectoryFromInaccuracy(double inaccuracy, double baseRange, double inaccuracyMultiplier, Vec3 launchPos, Vec3 interceptionPos) {

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

        return Pair.of(new Vec3(deltaX, deltaY, deltaZ).normalize(), new Vec3(deltaX / magXZ, Math.sin(thetaY), deltaZ / magXZ));

    }

}
