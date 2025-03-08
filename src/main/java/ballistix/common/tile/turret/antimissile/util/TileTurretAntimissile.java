package ballistix.common.tile.turret.antimissile.util;

import javax.annotation.Nullable;

import ballistix.api.turret.ITarget;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.GenericTileTurret;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class TileTurretAntimissile extends GenericTileTurret {

    public final Property<Boolean> isNotLinked = property(new Property<>(PropertyTypes.BOOLEAN, "islinked", false));
    public final Property<BlockPos> boundFireControl = property(new Property<>(PropertyTypes.BLOCK_POS, "bound", BlockEntityUtils.OUT_OF_REACH));
    @Nullable
    private TileFireControlRadar radar;

    public TileTurretAntimissile(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
        super(tileEntityTypeIn, worldPos, blockState, range, minRange, usage, rotationSpeedRadians, inaccuracy);
    }

    public boolean bindFireControlRadar(BlockPos pos) {
        double distance = getDistanceToPos(getBlockPos(), pos);
        if(distance > Constants.MAX_DISTANCE_FROM_RADAR) {
            return false;
        }
        boundFireControl.set(pos);
        return true;
    }

    @Override
    public boolean isValidPlacement() {
        return level.getBrightness(LightLayer.SKY, getBlockPos()) > 0;
    }

    @Override
    public Vec3 getDefaultOrientation() {
        Direction facing = getFacing();
        double mag = Math.sqrt(facing.getStepX() * facing.getStepX() + facing.getStepZ() * facing.getStepZ());
        if(mag <= 0) {
            mag = 1;
        }
        return new Vec3(facing.getStepX() / mag, 0, facing.getStepZ() / mag);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ITarget getTarget(long ticks) {
        if(ticks % 10 == 0) {
            if(level.getBlockEntity(boundFireControl.get()) instanceof TileFireControlRadar fire) {
                radar = fire;
            } else {
                radar = null;
                boundFireControl.set(BlockEntityUtils.OUT_OF_REACH);
            }
        }
        isNotLinked.set(radar == null);

        if(isNotLinked.get()) {
            return null;
        }

        if(radar.tracking == null  || radar.tracking.hasExploded()) {
            return null;
        }
        return new ITarget.TargetMissile(radar.tracking);
    }

    public static double getDistanceToPos(BlockPos start, BlockPos end) {
        double deltaX = end.getX() - start.getX();
        double deltaY = end.getY() - start.getY();
        double deltaZ = end.getZ() - start.getZ();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

}
