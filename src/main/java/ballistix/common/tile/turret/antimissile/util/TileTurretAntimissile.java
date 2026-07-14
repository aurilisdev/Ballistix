package ballistix.common.tile.turret.antimissile.util;

import javax.annotation.Nullable;

import ballistix.api.turret.ITarget;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.GenericTileTurret;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.utilities.BlockEntityUtils;

public abstract class TileTurretAntimissile extends GenericTileTurret {

    public final SingleProperty<Boolean> isNotLinked = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "islinked", false));
    public final SingleProperty<BlockPos> boundFireControl = property(new SingleProperty<>(PropertyTypes.BLOCK_POS, "bound", BlockEntityUtils.OUT_OF_REACH));
    @Nullable
    private TileFireControlRadar radar;

    public TileTurretAntimissile(TileEntityType<?> tileEntityTypeIn, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
        super(tileEntityTypeIn, range, minRange, usage, rotationSpeedRadians, inaccuracy);
    }

    public boolean bindFireControlRadar(BlockPos pos) {
        double distance = getDistanceToPos(getBlockPos(), pos);
        if(distance > BallistixConstants.MAX_DISTANCE_FROM_RADAR) {
            return false;
        }
        boundFireControl.setValue(pos);
        return true;
    }

    @Override
    public boolean isValidPlacement() {
        return level.getBrightness(LightType.SKY, getBlockPos()) > 0;
    }

    @Override
    public Vector3d getDefaultOrientation() {
        Direction facing = getFacing();
        double mag = Math.sqrt(facing.getStepX() * facing.getStepX() + facing.getStepZ() * facing.getStepZ());
        if(mag <= 0) {
            mag = 1;
        }
        return new Vector3d(facing.getStepX() / mag, 0, facing.getStepZ() / mag);
    }

    @Nullable
    @Override
    public ITarget getTarget(long ticks) {
        if(ticks % 10 == 0) {
        	TileEntity tileentity = level.getBlockEntity(boundFireControl.getValue());
            if(tileentity instanceof TileFireControlRadar) {
                radar = (TileFireControlRadar) tileentity;
            } else {
                radar = null;
                boundFireControl.setValue(BlockEntityUtils.OUT_OF_REACH);
            }
        }
        isNotLinked.setValue(radar == null);

        if(isNotLinked.getValue() || radar.tracking == null  || radar.tracking.hasExploded()) {
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
