package ballistix.common.tile.antimissile.turret;

import javax.annotation.Nullable;

import ballistix.common.tile.radar.TileFireControlRadar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;

public abstract class GenericTileAMTurret extends GenericTile {

    private BlockPos boundFireControl = BlockEntityUtils.OUT_OF_REACH;
    @Nullable
    private TileFireControlRadar radar;
    public boolean hasTarget = false;
    private final double range;
    private final double rotationSpeedRadians;
    private final double usage;
    boolean canFire = false;

    public final SingleProperty<Vec3> turretRotation = property(
	    new SingleProperty<>(PropertyTypes.VEC3, "turrot", getDefaultOrientation()));
    public final SingleProperty<Vec3> desiredRotation = property(
	    new SingleProperty<>(PropertyTypes.VEC3, "currot", getDefaultOrientation()));
    public final SingleProperty<Vec3> targetMovement = property(
	    new SingleProperty<>(PropertyTypes.VEC3, "movevec", Vec3.ZERO));

    public GenericTileAMTurret(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState,
	    double usage, double range, double rotationSpeedRadians) {
	super(tileEntityTypeIn, worldPos, blockState);
	addComponent(new ComponentElectrodynamic(this, false, true)
		.setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM)
		.voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).maxJoules(usage * 20));
	addComponent(new ComponentTickable(this).tickServer(this::tickServer));
	this.usage = usage;
	this.range = range;
	this.rotationSpeedRadians = rotationSpeedRadians;
    }

    public void tickServer(ComponentTickable tickable) {

	ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

	if (electro.getJoulesStored() < usage || level.getBrightness(LightLayer.SKY, getBlockPos()) <= 0) {
	    return;
	}

	if (tickable.getTicks() % 10 == 0 && radar == null) {
	    if (level.getBlockEntity(boundFireControl) instanceof TileFireControlRadar fire) {
		radar = fire;
	    }
	}
	hasTarget = radar != null && radar.tracking != null && !radar.tracking.hasExploded();

	canFire = false;

	double distanceToTarget = 0;

	if (hasTarget) {

	    float trackingSpeed = 0F;// radar.tracking.speed;
	    Vec3 trackingVector = radar.tracking.deltaMovement;

	    double timeToIntercept = TileFireControlRadar.getTimeToIntercept(radar.tracking.position, trackingVector,
		    trackingSpeed, getProjectileSpeed(), getProjectileLaunchPosition());

	    if (timeToIntercept >= 0) {

		Vec3 interceptPos = radar.tracking.position
			.add(trackingVector.scale(trackingSpeed).scale(timeToIntercept));

		Vec3 launchPos = getProjectileLaunchPosition();

		double deltaX = interceptPos.x - launchPos.x;
		double deltaY = interceptPos.y - launchPos.y;
		double deltaZ = interceptPos.z - launchPos.z;

		double sumXZ = deltaX * deltaX + deltaZ * deltaZ;

		double magXZ = Math.sqrt(sumXZ);

		if (magXZ <= 0) {
		    magXZ = 1;
		}

		double thetaY = Math.atan(deltaY / magXZ);

		targetMovement.setValue(new Vec3(deltaX, deltaY, deltaZ).normalize());

		desiredRotation.setValue(new Vec3(deltaX / magXZ, Math.sin(thetaY), deltaZ / magXZ));

		distanceToTarget = TileFireControlRadar.getDistanceToMissile(launchPos, interceptPos);

	    }

	} else {
	    desiredRotation.setValue(getDefaultOrientation());
	}

	if (turretRotation.getValue().equals(desiredRotation.getValue())) {

	    canFire = hasTarget && distanceToTarget > 0 && distanceToTarget <= range;

	} else {

	    double thetaDesiredXZ = getXZAngleRadians(desiredRotation.getValue());
	    double thetaCurrXZ = getXZAngleRadians(turretRotation.getValue());

	    double angleDifXZ = thetaDesiredXZ - thetaCurrXZ;

	    double deltaY = desiredRotation.getValue().y - turretRotation.getValue().y;

	    if (deltaY < 0) {
		turretRotation.setValue(turretRotation.getValue().add(0, -Math.cos(rotationSpeedRadians) * 0.125, 0));
		if (turretRotation.getValue().y < getMinElevation()) {
		    turretRotation.setValue(
			    new Vec3(turretRotation.getValue().x, getMinElevation(), turretRotation.getValue().z));
		} else if (turretRotation.getValue().y < desiredRotation.getValue().y) {
		    turretRotation.setValue(new Vec3(turretRotation.getValue().x, desiredRotation.getValue().y,
			    turretRotation.getValue().z));
		}
	    } else if (deltaY > 0) {
		turretRotation.setValue(turretRotation.getValue().add(0, Math.cos(rotationSpeedRadians) * 0.125, 0));

		if (turretRotation.getValue().y > getMaxElevation()) {
		    turretRotation.setValue(
			    new Vec3(turretRotation.getValue().x, getMaxElevation(), turretRotation.getValue().z));
		} else if (turretRotation.getValue().y > desiredRotation.getValue().y) {
		    turretRotation.setValue(new Vec3(turretRotation.getValue().x, desiredRotation.getValue().y,
			    turretRotation.getValue().z));
		}
	    }

	    if (angleDifXZ >= 0) {

		thetaCurrXZ += rotationSpeedRadians;

	    } else {

		thetaCurrXZ -= rotationSpeedRadians;

	    }

	    // thetaCurrXZ = getXZAngleRadians(turretRotation.getValue());

	    if ((angleDifXZ >= 0 && thetaCurrXZ > thetaDesiredXZ) || (angleDifXZ < 0 && thetaCurrXZ < thetaDesiredXZ)) {

		turretRotation.setValue(new Vec3(desiredRotation.getValue().x, turretRotation.getValue().y,
			desiredRotation.getValue().z));

	    } else {
		turretRotation
			.setValue(new Vec3(Math.cos(thetaCurrXZ), turretRotation.getValue().y, Math.sin(thetaCurrXZ)));
	    }

	    canFire = hasTarget && turretRotation.getValue().equals(desiredRotation.getValue()) && distanceToTarget > 0
		    && distanceToTarget <= range;

	}

	if (canFire) {
	    fireTickServer();
	}

    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
	super.saveAdditional(compound, registries);
	compound.put("bound", NbtUtils.writeBlockPos(boundFireControl));
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
	super.loadAdditional(compound, registries);
	boundFireControl = NbtUtils.readBlockPos(compound, "bound").orElse(BlockEntityUtils.OUT_OF_REACH);
    }

    public abstract void fireTickServer();

    public void bindFireControlRadar(BlockPos pos) {
	boundFireControl = pos;
    }

    public Vec3 getDefaultOrientation() {
	Direction facing = getFacing();
	double mag = Math.sqrt(facing.getStepX() * facing.getStepX() + facing.getStepZ() * facing.getStepZ());
	if (mag <= 0) {
	    mag = 1;
	}
	return new Vec3(facing.getStepX() / mag, 0, facing.getStepZ() / mag);
    }

    public abstract Vec3 getProjectileLaunchPosition();

    // speed in units of ticks
    public abstract float getProjectileSpeed();

    public abstract double getMinElevation();

    public abstract double getMaxElevation();

    public static double getXZAngleRadians(Vec3 vector) {
	return Math.atan2(vector.z, vector.x);
    }
}
