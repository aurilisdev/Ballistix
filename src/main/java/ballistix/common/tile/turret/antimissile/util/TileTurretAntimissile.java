package ballistix.common.tile.turret.antimissile.util;

import javax.annotation.Nullable;

import ballistix.api.turret.ITarget;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.GenericTileTurret;
import electrodynamics.common.tile.machines.quarry.TileQuarry;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class TileTurretAntimissile extends GenericTileTurret {

	public final Property<Boolean> isNotLinked = property(new Property<>(PropertyType.Boolean, "islinked", false));
	public final Property<BlockPos> boundFireControl = property(new Property<>(PropertyType.BlockPos, "bound", TileQuarry.OUT_OF_REACH));
	@Nullable
	private TileFireControlRadar radar;

	public TileTurretAntimissile(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
		super(tileEntityTypeIn, worldPos, blockState, range, minRange, usage, rotationSpeedRadians, inaccuracy);
	}

	public boolean bindFireControlRadar(BlockPos pos) {
		double deltaX = pos.getX() - getBlockPos().getX();
		double deltaY = pos.getY() - getBlockPos().getY();
		double deltaZ = pos.getZ() - getBlockPos().getZ();
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
		if (distance > Constants.MAX_DISTANCE_FROM_RADAR) {
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
		if (mag <= 0) {
			mag = 1;
		}
		return new Vec3(facing.getStepX() / mag, 0, facing.getStepZ() / mag);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public ITarget getTarget(long ticks) {
		if (ticks % 10 == 0) {
			if (level.getBlockEntity(boundFireControl.get()) instanceof TileFireControlRadar fire) {
				radar = fire;
			} else {
				radar = null;
				boundFireControl.set(TileQuarry.OUT_OF_REACH);
			}
		}
		isNotLinked.set(radar == null);

		if (isNotLinked.get()) {
			return null;
		}

		if (radar.tracking == null || radar.tracking.hasExploded()) {
			return null;
		}
		return new ITarget.TargetMissile(radar.tracking);
	}

}
