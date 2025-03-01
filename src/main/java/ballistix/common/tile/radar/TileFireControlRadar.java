package ballistix.common.tile.radar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ballistix.References;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.inventory.container.ContainerFireControlRadar;
import ballistix.common.settings.Constants;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixSounds;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;

public class TileFireControlRadar extends GenericTile {

	public static final long PULSE_TIME_TICKS = 25L;

	public static final Vector3d OUT_OF_REACH = new Vector3d(0, -1000, 0);

	public final Property<Vector3d> trackingPos = property(new Property<>(PropertyType.Vec3, "trackingpos", OUT_OF_REACH));
	public final Property<Boolean> usingWhitelist = property(new Property<>(PropertyType.Boolean, "usingwhitelist", false));
	public final Property<List<Integer>> whitelistedFrequencies = property(new Property<>(PropertyType.IntegerList, "whitelistedfreqs", new ArrayList<>()));
	public final Property<Integer> missileType = property(new Property<>(PropertyType.Integer, "trackingtype", -1));
	public final Property<Boolean> usingRedstone = property(new Property<>(PropertyType.Boolean, "usingredstone", false));
	public final Property<Boolean> redstone = property(new Property<>(PropertyType.Boolean, "redstone", false));
	public final Property<Boolean> running = property(new Property<>(PropertyType.Boolean, "running", false));

	public Vector3d searchPos = OUT_OF_REACH;
	private AxisAlignedBB searchArea = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	@Nullable
	public VirtualMissile tracking;

	public double clientRotation;
	public double clientRotationSpeed;

	public TileFireControlRadar() {
		super(BallistixBlockTypes.TILE_FIRECONTROLRADAR.get());
		addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE).setInputDirections(Direction.DOWN).maxJoules(Constants.FIRE_CONTROL_RADAR_USAGE * 20));
		addComponent(new ComponentContainerProvider("container.firecontrolradar", this).createMenu((id, player) -> new ContainerFireControlRadar(id, player, new Inventory(0), getCoordsArray())));
	}

	public void tickServer(ComponentTickable tickable) {
		ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

		running.set(electro.getJoulesStored() > Constants.RADAR_USAGE / 20.0 && level.getBrightness(LightType.SKY, getBlockPos()) > 0 && (!usingRedstone.get() || (usingRedstone.get() && redstone.get())));

		if (!running.get()) {
			tracking = null;
			TileESMTower.removeFireControlRadar(this);
			return;
		}

		TileESMTower.addFireControlRadar(this);

		electro.joules(electro.getJoulesStored() - (Constants.RADAR_USAGE / 20.0));

		if (tracking != null && (tracking.hasExploded() || tracking.getId() == null || MissileManager.getMissile(level.dimension(), tracking.getId()) == null)) {
			tracking = null;
		}

		VirtualMissile temp = null;

		for (VirtualMissile missile : MissileManager.getMissilesForLevel(level.dimension())) {
			if (missile.getBoundingBox().intersects(searchArea)) {
				if (temp == null && (!usingWhitelist.get() || usingWhitelist.get() && !whitelistedFrequencies.get().contains(missile.frequency)) && !missile.hasExploded()) {
					temp = missile;
				} else if (temp != null && getDistanceToMissile(searchPos, missile.position) < getDistanceToMissile(searchPos, temp.position) && (!usingWhitelist.get() || usingWhitelist.get() && !whitelistedFrequencies.get().contains(missile.frequency)) && !missile.hasExploded()) {
					temp = missile;
				}
			}
		}

		if (tracking == null) {
			tracking = temp;
		}

		if (tracking != null && !tracking.hasExploded()) {
			trackingPos.set(tracking.position);
			missileType.set(tracking.missileType);
			if (trackingPos.get().distanceTo(new Vector3d(worldPosition.getX(), trackingPos.get().y, worldPosition.getZ())) > Constants.FIRE_CONTROL_RADAR_RANGE) {
				tracking = null;
				trackingPos.set(OUT_OF_REACH);
			}
		} else {
			trackingPos.set(OUT_OF_REACH);
			missileType.set(-1);
		}

	}
	
	@Override
	public void setLevelAndPosition(World level, BlockPos pos) {
		super.setLevelAndPosition(level, pos);
		searchPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		searchArea = new AxisAlignedBB(getBlockPos()).inflate(Constants.FIRE_CONTROL_RADAR_RANGE);
	}

	public void tickClient(ComponentTickable tickable) {
		clientRotation += clientRotationSpeed;

		clientRotationSpeed = MathHelper.clamp(clientRotationSpeed + 0.25 * (running.get() ? 1 : -1), 0.0, 20.0);
		if (tickable.getTicks() % PULSE_TIME_TICKS == 0 && running.get()) {
			SoundAPI.playSound(BallistixSounds.SOUND_FIRECONTROLRADAR.get(), SoundCategory.BLOCKS, 1.0F, 1.0F, worldPosition);
		}
	}

	@Override
	public void onNeightborChanged(BlockPos neighbor, boolean blockStateTrigger) {
		super.onNeightborChanged(neighbor, blockStateTrigger);
		redstone.set(level.getBestNeighborSignal(getBlockPos()) > 0);
	}

	public static double getDistanceToMissile(Vector3d pos, Vector3d missilePos) {
		double deltaX = missilePos.x - pos.x;
		double deltaY = missilePos.y - pos.y;
		double deltaZ = missilePos.z - pos.z;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	// will return negative one if can't hit;
	// otherwise returns the time in seconds
	public static double getTimeToIntercept(Vector3d missPos, Vector3d missVect, float missSpeed, float bulletSpeed, Vector3d interceptorPos) {
		Vector3d missVector = missVect.scale(missSpeed);

		double a = missVector.dot(missVector) - bulletSpeed * bulletSpeed; // if this is zero it means the proj can
		// never catch the target

		if (a == 0) {
			return -1;
		}

		double b = missPos.dot(missVector) * 2;
		double c = missPos.dot(missPos);
		double root = (b * b) - 4 * a * c;
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
			ForgeChunkManager.forceChunk((ServerWorld) level, References.ID, getBlockPos(), pos.x, pos.z, false, true);
		}

	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		if (!level.isClientSide) {
			ChunkPos pos = level.getChunk(getBlockPos()).getPos();
			ForgeChunkManager.forceChunk((ServerWorld) level, References.ID, getBlockPos(), pos.x, pos.z, true, true);
		}
	}

}
