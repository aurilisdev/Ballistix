package ballistix.common.tile.turret;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.turret.ITarget;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tags.BallistixTags;
import ballistix.common.tile.radar.TileFireControlRadar;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.world.ForgeChunkManager;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentForgeEnergy;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;

public abstract class GenericTileTurret extends GenericTile {

    public final SingleProperty<Vec3> turretRotation = property(
	    new SingleProperty<>(PropertyTypes.VEC3, "turrot", getDefaultOrientation()));
    public final SingleProperty<Vec3> desiredRotation = property(
	    new SingleProperty<>(PropertyTypes.VEC3, "currot", getDefaultOrientation()));
    public final SingleProperty<Vec3> targetMovement = property(
	    new SingleProperty<>(PropertyTypes.VEC3, "movevec", Vec3.ZERO));
    public final SingleProperty<Boolean> hasTarget = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "hastarget", false)).onChange((prop, val) -> {

		if (level == null || level.isClientSide) {
		    return;
		}

		if (prop.getValue() && val != prop.getValue()) {
		    movementCooldown = 0;
		} else if (prop.getValue() != val) {
		    movementCooldown = 20;
		}

	    });
    public final SingleProperty<Boolean> hasNoPower = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "haspower", false));
    public final SingleProperty<Boolean> inRange = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "isrange", false));
    public final SingleProperty<Double> currentRange;
    public final SingleProperty<Double> inaccuracyMultiplier = property(
	    new SingleProperty<>(PropertyTypes.DOUBLE, "inaccuracymultiplier", 1.0));
    public final SingleProperty<Boolean> canFire = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "canfire", false));
    public final ListProperty<String> whitelistedPlayers = property(
	    new ListProperty<>(PropertyTypes.STRING_LIST, "whitelistedplayers", new ArrayList<>()));
    public final SingleProperty<Integer> entityTargetingMode = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "entitytargetingmode", 0));

    public final double baseRange;
    public final double rotationSpeedRadians;
    public final double usage;
    public final double minimumRange;
    public final double inaccuracy;
    @Nullable
    public ITarget target;

    private int movementCooldown = 0;

    public GenericTileTurret(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState,
	    double baseRange, double minimumRange, double usage, double rotationSpeedRadians, double inaccuracy) {
	super(tileEntityTypeIn, worldPos, blockState);
	addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
	addComponent(new ComponentElectrodynamic(this, false, true)
		.setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM)
		.voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).maxJoules(usage * 20));
	addComponent(getInventory().validUpgrades(SubtypeItemUpgrade.range));
	addComponent(getContainer());
	addComponent(new ComponentForgeEnergy(this));
	this.usage = usage;
	this.baseRange = baseRange;
	this.minimumRange = minimumRange;
	this.rotationSpeedRadians = rotationSpeedRadians;
	this.inaccuracy = inaccuracy;
	currentRange = property(new SingleProperty<>(PropertyTypes.DOUBLE, "currentrange", baseRange));
    }

    public void tickServer(ComponentTickable tickable) {

	ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

	hasNoPower.setValue(electro.getJoulesStored() < usage);

	if (hasNoPower.getValue()) {
	    return;
	}

	electro.setJoulesStored(electro.getJoulesStored() - usage);

	tickServerActive(tickable);

	target = getTarget(tickable.getTicks());

	if (!isValidPlacement()) {
	    return;
	}

	hasTarget.setValue(target != null);

	double distanceToTarget = 0;

	if (hasTarget.getValue()) {

	    Vec3 interceptionPos = getTargetPosition(target);

	    if (interceptionPos != null) {

		Vec3 launchPos = getProjectileLaunchPosition();

		distanceToTarget = TileFireControlRadar.getDistanceToMissile(launchPos, interceptionPos);

		double deltaX = interceptionPos.x - launchPos.x;
		double deltaY = interceptionPos.y - launchPos.y;
		double deltaZ = interceptionPos.z - launchPos.z;

		double sumXZ = deltaX * deltaX + deltaZ * deltaZ;

		double magXZ = Math.sqrt(sumXZ);

		if (magXZ <= 0) {
		    magXZ = 1;
		}

		double thetaY = Math.atan(deltaY / magXZ);

		targetMovement.setValue(new Vec3(deltaX, deltaY, deltaZ).normalize());

		desiredRotation.setValue(new Vec3(deltaX / magXZ, Math.sin(thetaY), deltaZ / magXZ));

	    }

	} else if (movementCooldown <= 0) {
	    desiredRotation.setValue(getDefaultOrientation());
	} else {
	    movementCooldown--;
	}

	inRange.setValue(distanceToTarget >= minimumRange && distanceToTarget <= currentRange.getValue());

	if (turretRotation.getValue().equals(desiredRotation.getValue())) {

	    canFire.setValue(hasTarget.getValue() && inRange.getValue());

	} else if (movementCooldown <= 0) {

	    Vec3 desiredRot = new Vec3(desiredRotation.getValue().x, 0, desiredRotation.getValue().z);
	    desiredRot = desiredRot.normalize();

	    Vec3 currRot = new Vec3(turretRotation.getValue().x, 0, turretRotation.getValue().z);
	    currRot = currRot.normalize();

	    double cosAngle = desiredRot.dot(currRot);
	    cosAngle = Math.max(-1.0, Math.min(1.0, cosAngle));

	    Vec3 newXZ;

	    if (Math.acos(cosAngle) <= rotationSpeedRadians) {

		newXZ = desiredRot;

	    } else {

		Vec3 perpVector = currRot.cross(desiredRot).cross(currRot);

		if (perpVector.lengthSqr() < 1.0E-7) {
		    perpVector = new Vec3(-currRot.z, 0, currRot.x);
		} else {
		    perpVector = perpVector.normalize();
		}

		newXZ = currRot.scale(Math.cos(rotationSpeedRadians))
			.add(perpVector.scale(Math.sin(rotationSpeedRadians))).normalize();

	    }

	    double deltaY = desiredRotation.getValue().y - turretRotation.getValue().y;

	    if (deltaY < 0) {
		turretRotation.setValue(turretRotation.getValue().add(0, -Math.cos(rotationSpeedRadians) * 0.125, 0));
		if (turretRotation.getValue().y < getMinElevation()) {
		    turretRotation.setValue(new Vec3(turretRotation.getValue().x,
			    Math.max(getMinElevation(), desiredRotation.getValue().y), turretRotation.getValue().z));
		} else if (turretRotation.getValue().y < desiredRotation.getValue().y) {
		    turretRotation.setValue(new Vec3(turretRotation.getValue().x, desiredRotation.getValue().y,
			    turretRotation.getValue().z));
		}
	    } else if (deltaY > 0) {
		turretRotation.setValue(turretRotation.getValue().add(0, Math.cos(rotationSpeedRadians) * 0.125, 0));

		if (turretRotation.getValue().y > getMaxElevation()) {
		    turretRotation.setValue(new Vec3(turretRotation.getValue().x,
			    Math.min(getMaxElevation(), desiredRotation.getValue().y), turretRotation.getValue().z));
		} else if (turretRotation.getValue().y > desiredRotation.getValue().y) {
		    turretRotation.setValue(new Vec3(turretRotation.getValue().x, desiredRotation.getValue().y,
			    turretRotation.getValue().z));
		}
	    }
	    turretRotation.setValue(new Vec3(newXZ.x, turretRotation.getValue().y, newXZ.z));


	    canFire.setValue(hasTarget.getValue() && turretRotation.getValue().equals(desiredRotation.getValue())
		    && inRange.getValue());

	} else {
	    canFire.setValue(false);
	}

	if (canFire.getValue()) {
	    fireTickServer(tickable.getTicks());
	}

    }

    @Nullable
    protected LivingEntity findLivingTarget(TargetingMode mode) {

	if (mode == TargetingMode.NONE) {
	    return null;
	}

	LivingEntity selected = null;
	double selectedDistSqr = Double.MAX_VALUE;

	double range = currentRange.getValue();
	double rangeSqr = range * range;
	double minRangeSqr = minimumRange * minimumRange;

	Class<? extends LivingEntity> type = mode == TargetingMode.ONLY_PLAYERS ? Player.class : LivingEntity.class;

	Vec3 turretCenter = Vec3.atCenterOf(getBlockPos());

	for (LivingEntity entity : level.getEntitiesOfClass(type, new AABB(getBlockPos()).inflate(range / 4))) {

	    if (entity.isDeadOrDying() || entity.isRemoved()) {
		continue;
	    }

	    if (entity instanceof Player player
		    && (player.isCreative() || whitelistedPlayers.getValue().contains(player.getName().getString()))) {
		continue;
	    }

	    double distSqr = entity.distanceToSqr(turretCenter);

	    if (distSqr > rangeSqr || distSqr < minRangeSqr) {
		continue;
	    }

	    if (!canRaycastTo(level, getProjectileLaunchPosition(), entity.position().add(0, entity.getEyeHeight(), 0),
		    getBlockPos())) {
		continue;
	    }

	    if (distSqr < selectedDistSqr) {
		selected = entity;
		selectedDistSqr = distSqr;
	    }
	}

	return selected;
    }

    protected boolean isLivingTargetValid(@Nullable LivingEntity entity, TargetingMode mode) {

	if (entity == null || mode == TargetingMode.NONE) {
	    return false;
	}

	if (entity.isRemoved() || entity.isDeadOrDying()) {
	    return false;
	}

	if (mode == TargetingMode.ONLY_PLAYERS && !(entity instanceof Player)) {
	    return false;
	}

	if (entity instanceof Player player
		&& (player.isCreative() || whitelistedPlayers.getValue().contains(player.getName().getString()))) {
	    return false;
	}

	Vec3 launchPos = getProjectileLaunchPosition();
	Vec3 targetPos = entity.position().add(0, entity.getEyeHeight(), 0);

	double deltaX = targetPos.x - launchPos.x;
	double deltaY = targetPos.y - launchPos.y;
	double deltaZ = targetPos.z - launchPos.z;

	double distSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

	double range = currentRange.getValue() / 4;
	double rangeSqr = range * range;
	double minRangeSqr = minimumRange * minimumRange;

	return distSqr <= rangeSqr && distSqr >= minRangeSqr;
    }

    public void tickClient(ComponentTickable tickable) {

    }

    public abstract ComponentInventory getInventory();

    public abstract ComponentContainerProvider getContainer();

    public abstract void tickServerActive(ComponentTickable tickable);

    public abstract void fireTickServer(long ticks);

    public abstract Vec3 getDefaultOrientation();

    public abstract Vec3 getProjectileLaunchPosition();

    @Nullable
    public abstract Vec3 getTargetPosition(@Nonnull ITarget target);

    public abstract double getMinElevation();

    public abstract double getMaxElevation();

    @Nullable
    public abstract ITarget getTarget(long ticks);

    public abstract boolean isValidPlacement();

    @Override
    public void onInventoryChange(ComponentInventory inv, int slot) {

	super.onInventoryChange(inv, slot);

	if (slot >= inv.getUpgradeSlotStartIndex() || slot == -1) {

	    int rangeUpgrades = 0;

	    for (ItemStack stack : inv.getUpgradeContents()) {

		if (stack.getItem() instanceof ItemUpgrade upgrade && upgrade.subtype == SubtypeItemUpgrade.range) {
		    rangeUpgrades += stack.getCount();
		}

	    }

	    double inaccuracyMulitplier = 1;
	    double range = baseRange;

	    for (int i = 0; i < rangeUpgrades; i++) {
		inaccuracyMulitplier *= BallistixConstants.RANGE_INCREASE_INACCURACY_MULTIPLIER;
		range += 5.55;
	    }

	    range = Math.min(range, BallistixConstants.FIRE_CONTROL_RADAR_RANGE);

	    currentRange.setValue(range);
	    inaccuracyMultiplier.setValue(inaccuracyMulitplier);

	}

    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
	super.saveAdditional(compound);
	compound.putInt("turncooldown", movementCooldown);
    }

    @Override
    public void load(CompoundTag compound) {
	super.load(compound);
	movementCooldown = compound.getInt("turncooldown");
    }

    public static double getXZAngleRadians(Vec3 vector) {
	return Math.atan2(vector.z, vector.x);
    }

    public static boolean canRaycastTo(Level world, Vec3 start, Vec3 end, BlockPos... ignoredPositions) {

	BlockPos originCheck = new BlockPos(start);
	BlockPos endCheck = new BlockPos(end);

	BlockHitResult hit = BlockGetter.traverseBlocks(start, end, ignoredPositions, (ignored, pos) -> {

	    if (pos.equals(originCheck) || pos.equals(endCheck) || isIgnoredRaycastPos(pos, ignored)) {
		return null;
	    }

	    BlockState state = world.getBlockState(pos);

	    if (!willStopTurrret(state)) {
		return null;
	    }

	    VoxelShape shape = state.getCollisionShape(world, pos);

	    if (shape.isEmpty()) {
		return null;
	    }

	    return shape.clip(start, end, pos);

	}, ignored -> null);

	return hit == null;
    }

    private static boolean isIgnoredRaycastPos(BlockPos pos, BlockPos... ignoredPositions) {

	for (BlockPos ignored : ignoredPositions) {
	    if (pos.equals(ignored)) {
		return true;
	    }
	}

	return false;
    }

    @Override
    public void setPlacedBy(LivingEntity player, ItemStack stack) {
	super.setPlacedBy(player, stack);
	if (player instanceof Player pl) {
	    whitelistedPlayers.addValue(pl.getName().getString());
	}
    }

    @Override
    public void onBlockDestroyed() {
	super.onBlockDestroyed();

	if (!level.isClientSide) {
	    ChunkPos pos = level.getChunk(getBlockPos()).getPos();
	    ForgeChunkManager.forceChunk((ServerLevel) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, false, true);
	}

    }

    @Override
    public void onPlace(BlockState oldState, boolean isMoving) {
	super.onPlace(oldState, isMoving);
	if (!level.isClientSide) {
	    ChunkPos pos = level.getChunk(getBlockPos()).getPos();
	    ForgeChunkManager.forceChunk((ServerLevel) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, true, true);
	}
    }

    public static boolean willStopTurrret(BlockState state) {
	if (state.isAir() || state.is(Blocks.LIGHT)) {
	    return false;
	}
	if (state.is(Blocks.SNOW) && state.getValue(SnowLayerBlock.LAYERS) < 4) {
	    return false;
	}
	if (state.is(BallistixTags.Blocks.WHITELISTED_TURRET_BLOCKS)) {
	    return false;
	}
	return true;
    }

    public static enum TargetingMode {
	ALL, ONLY_PLAYERS, NONE;
    }

}
