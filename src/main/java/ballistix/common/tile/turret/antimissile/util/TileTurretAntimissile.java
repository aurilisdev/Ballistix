package ballistix.common.tile.turret.antimissile.util;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.turret.ITarget;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.turret.GenericTileTurret;
import electrodynamics.prefab.block.GenericEntityBlock;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;

public abstract class TileTurretAntimissile extends GenericTileTurret {

	public final Property<Boolean> isNotLinked = property(new Property<>(PropertyType.Boolean, "islinked", false));
	public final Property<BlockPos> boundFireControl = property(new Property<>(PropertyType.BlockPos, "bound", Ballistix.OUT_OF_REACH));
	@Nullable
	private TileFireControlRadar radar;

	public TileTurretAntimissile(TileEntityType<?> tileEntityTypeIn, double range, double minRange, double usage, double rotationSpeedRadians, double inaccuracy) {
		super(tileEntityTypeIn, range, minRange, usage, rotationSpeedRadians, inaccuracy);
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
		return level.getBrightness(LightType.SKY, getBlockPos()) > 0;
	}

	@Override
	public Vector3d getDefaultOrientation() {
		Direction facing = getFacing();
		double mag = Math.sqrt(facing.getStepX() * facing.getStepX() + facing.getStepZ() * facing.getStepZ());
		if (mag <= 0) {
			mag = 1;
		}
		return new Vector3d(facing.getStepX() / mag, 0, facing.getStepZ() / mag);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		
		Direction facing = state.hasProperty(GenericEntityBlock.FACING) ? state.getValue(GenericEntityBlock.FACING) : Direction.NORTH;
		double mag = Math.sqrt(facing.getStepX() * facing.getStepX() + facing.getStepZ() * facing.getStepZ());
		if (mag <= 0) {
			mag = 1;
		}
		Vector3d vec = new Vector3d(facing.getStepX() / mag, 0, facing.getStepZ() / mag);
		
		turretRotation.set(vec);
    	desiredRotation.set(vec);
	}

	@Override
	public ITarget getTarget(long ticks) {
		if (ticks % 10 == 0) {
			TileEntity tile = level.getBlockEntity(boundFireControl.get()); 
			if (tile instanceof TileFireControlRadar) {
				radar = (TileFireControlRadar) tile;
			} else {
				radar = null;
				boundFireControl.set(Ballistix.OUT_OF_REACH);
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
