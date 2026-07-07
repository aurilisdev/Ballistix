package ballistix.common.tile.radar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.inventory.container.ContainerFireControlRadar;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import voltaic.api.sound.SoundAPI;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentForgeEnergy;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;

public class TileFireControlRadar extends GenericTile {

    public static final long PULSE_TIME_TICKS = 25L;

    public static final Vec3 OUT_OF_REACH = new Vec3(0, -1000, 0);

    public final SingleProperty<Vec3> trackingPos = property(
	    new SingleProperty<>(PropertyTypes.VEC3, "trackingpos", OUT_OF_REACH));
    public final SingleProperty<Boolean> usingWhitelist = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "usingwhitelist", false));
    public final ListProperty<Integer> whitelistedFrequencies = property(
	    new ListProperty<>(PropertyTypes.INTEGER_LIST, "whitelistedfreqs", new ArrayList<>()));
    public final SingleProperty<Integer> missileType = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "trackingtype", -1));
    public final SingleProperty<Boolean> usingRedstone = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "usingredstone", false));
    public final SingleProperty<Boolean> redstone = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "redstone", false));
    public final SingleProperty<Boolean> running = property(
	    new SingleProperty<>(PropertyTypes.BOOLEAN, "running", false));

    public final Vec3 searchPos;
    private final AABB searchArea = new AABB(getBlockPos()).inflate(BallistixConstants.FIRE_CONTROL_RADAR_RANGE);
    @Nullable
    public VirtualMissile tracking;

    private final ArrayList<VirtualMissile> trackedMissiles = new ArrayList<>();
    private final Map<BlockPos, UUID> assignments = new HashMap<>();

    public List<VirtualMissile> getTrackedMissiles() {
	return Collections.unmodifiableList(trackedMissiles);
    }

    private boolean isValidThreat(VirtualMissile missile) {

	if (missile == null || missile.hasExploded() || missile.getId() == null) {
	    return false;
	}

	if (!missile.getBoundingBox().intersects(searchArea)) {
	    return false;
	}

	return !usingWhitelist.getValue() || !whitelistedFrequencies.getValue().contains(missile.payloadData.frequency);
    }

    @Nullable
    private VirtualMissile getMissileById(UUID id) {
	return MissileManager.getMissile(level.dimension(), id);
    }

    public double clientRotation;
    public double clientRotationSpeed;

    public TileFireControlRadar(BlockPos pos, BlockState state) {
	super(BallistixTiles.TILE_FIRECONTROLRADAR.get(), pos, state);
	addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
	addComponent(new ComponentPacketHandler(this));
	addComponent(new ComponentElectrodynamic(this, false, true).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE)
		.setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM)
		.maxJoules(BallistixConstants.FIRE_CONTROL_RADAR_USAGE * 20));
	addComponent(new ComponentContainerProvider("firecontrolradar", this).createMenu(
		(id, player) -> new ContainerFireControlRadar(id, player, new SimpleContainer(0), getCoordsArray())));
	addComponent(new ComponentForgeEnergy(this));
	searchPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public void tickServer(ComponentTickable tickable) {
	ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

	running.setValue(electro.getJoulesStored() > BallistixConstants.RADAR_USAGE / 20.0
		&& level.getBrightness(LightLayer.SKY, getBlockPos()) > 0
		&& (!usingRedstone.getValue() || usingRedstone.getValue() && redstone.getValue()));

	if (!running.getValue()) {
	    tracking = null;
	    TileESMTower.removeFireControlRadar(this);
	    return;
	}

	TileESMTower.addFireControlRadar(this);

	electro.joules(electro.getJoulesStored() - BallistixConstants.RADAR_USAGE / 20.0);

	if (tracking != null && (tracking.hasExploded() || tracking.getId() == null
		|| MissileManager.getMissile(level.dimension(), tracking.getId()) == null)) {
	    tracking = null;
	    trackingPos.setValue(OUT_OF_REACH);
	}

	trackedMissiles.clear();

	for (VirtualMissile missile : MissileManager.getMissilesForLevel(level.dimension())) {
	    if (isValidThreat(missile)) {
		trackedMissiles.add(missile);
	    }
	}

	trackedMissiles.sort(Comparator.comparingDouble(missile -> scoreThreat(missile, searchPos, 0)));

	assignments.entrySet().removeIf(entry -> {
	    VirtualMissile missile = getMissileById(entry.getValue());
	    return missile == null || !isValidThreat(missile);
	});

	tracking = trackedMissiles.isEmpty() ? null : trackedMissiles.get(0);

	if (tracking != null && !tracking.hasExploded()) {
	    trackingPos.setValue(tracking.position);
	    missileType.setValue(tracking.payloadData.missileType);
	    if (trackingPos.getValue().distanceTo(new Vec3(worldPosition.getX(), trackingPos.getValue().y,
		    worldPosition.getZ())) > BallistixConstants.FIRE_CONTROL_RADAR_RANGE) {
		tracking = null;
		trackingPos.setValue(OUT_OF_REACH);
	    }
	} else {
	    trackingPos.setValue(OUT_OF_REACH);
	    missileType.setValue(-1);
	}
    }

    private double scoreThreat(VirtualMissile missile, Vec3 requesterPos, float interceptorSpeed) {

	double distanceToRequester = missile.position.distanceTo(requesterPos);

	double interceptTime = interceptorSpeed > 0
		? getTimeToIntercept(missile.position, missile.deltaMovement, missile.speed, interceptorSpeed,
			requesterPos)
		: -1;

	if (interceptTime > 0) {
	    return interceptTime * 1000.0 + distanceToRequester;
	}

	return distanceToRequester;
    }

    @Nullable
    public VirtualMissile getTargetFor(BlockPos requesterBlockPos, Vec3 requesterPos, float interceptorSpeed) {

	UUID currentAssignment = assignments.get(requesterBlockPos);

	if (currentAssignment != null) {
	    VirtualMissile assigned = getMissileById(currentAssignment);
	    if (assigned != null && isValidThreat(assigned)) {
		return assigned;
	    }
	    assignments.remove(requesterBlockPos);
	}

	VirtualMissile best = null;
	int bestAssignmentCount = Integer.MAX_VALUE;
	double bestScore = Double.MAX_VALUE;

	for (VirtualMissile missile : trackedMissiles) {

	    if (missile.getId() == null || !isValidThreat(missile)) {
		continue;
	    }

	    int assignmentCount = getAssignmentCount(missile.getId());
	    double score = scoreThreat(missile, requesterPos, interceptorSpeed);

	    if (assignmentCount < bestAssignmentCount || assignmentCount == bestAssignmentCount && score < bestScore) {
		best = missile;
		bestAssignmentCount = assignmentCount;
		bestScore = score;
	    }
	}

	if (best != null) {
	    assignments.put(requesterBlockPos, best.getId());
	}

	return best;
    }

    private int getAssignmentCount(UUID missileId) {

	int count = 0;

	for (UUID assigned : assignments.values()) {
	    if (missileId.equals(assigned)) {
		count++;
	    }
	}

	return count;
    }

    public void tickClient(ComponentTickable tickable) {
	clientRotation += clientRotationSpeed;

	clientRotationSpeed = Mth.clamp(clientRotationSpeed + 0.25 * (running.getValue() ? 1 : -1), 0.0, 20.0);
	if (tickable.getTicks() % PULSE_TIME_TICKS == 0 && running.getValue()) {
	    SoundAPI.playSound(BallistixSounds.SOUND_FIRECONTROLRADAR.get(), SoundSource.BLOCKS, 1.0F, 1.0F,
		    worldPosition);
	}
    }

    @Override
    public void onNeightborChanged(BlockPos neighbor, boolean blockStateTrigger) {
	super.onNeightborChanged(neighbor, blockStateTrigger);
	redstone.setValue(level.getBestNeighborSignal(getBlockPos()) > 0);
    }

    public static double getDistanceToMissile(Vec3 pos, Vec3 missilePos) {
	double deltaX = missilePos.x - pos.x;
	double deltaY = missilePos.y - pos.y;
	double deltaZ = missilePos.z - pos.z;
	return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    // will return negative one if can't hit;
    // otherwise returns the time in seconds
    public static double getTimeToIntercept(Vec3 missPos, Vec3 missVect, float missSpeed, float bulletSpeed,
	    Vec3 interceptorPos) {
	Vec3 missVector = missVect.scale(missSpeed);

	double a = missVector.dot(missVector) - bulletSpeed * bulletSpeed; // if this is zero it means the proj can
	// never catch the target

	if (a == 0) {
	    return -1;
	}

	double b = missPos.dot(missVector) * 2;
	double c = missPos.dot(missPos);
	double root = b * b - 4 * a * c;
	if (root < 0) {

	    return -1;

	} else if (root == 0) {

	    return -b / (2 * a);

	} else {

	    root = Math.sqrt(root);

	    double sol1 = (-b - root) / (2 * a);
	    double sol2 = (-b + root) / (2 * a);

	    if (sol1 > 0 && sol2 > 0) {
		return -1;
	    } else if (sol1 > 0) {

		return -sol2;

	    } else if (sol2 > 0) {

		return -sol1;

	    } else {

		return -Math.max(sol1, sol2);

	    }

	}
    }

    @Override
    public void onBlockDestroyed() {
	super.onBlockDestroyed();

	if (!level.isClientSide) {
	    TileESMTower.removeFireControlRadar(this);
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

    @Override
    public int getComparatorSignal() {
	return trackingPos.getValue().equals(OUT_OF_REACH) ? 0 : 15;
    }

}
