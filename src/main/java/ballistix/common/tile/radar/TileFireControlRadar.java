package ballistix.common.tile.radar;

import java.util.ArrayList;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.inventory.container.ContainerFireControlRadar;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
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

    public static final Vector3d OUT_OF_REACH = new Vector3d(0, -1000, 0);

    public final SingleProperty<Vector3d> trackingPos = property(new SingleProperty<>(PropertyTypes.VEC3, "trackingpos", OUT_OF_REACH));
    public final SingleProperty<Boolean> usingWhitelist = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "usingwhitelist", false));
    public final ListProperty<Integer> whitelistedFrequencies = property(new ListProperty<>(PropertyTypes.INTEGER_LIST, "whitelistedfreqs", new ArrayList<>()));
    public final SingleProperty<Integer> missileType = property(new SingleProperty<>(PropertyTypes.INTEGER, "trackingtype", -1));
    public final SingleProperty<Boolean> usingRedstone = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "usingredstone", false));
    public final SingleProperty<Boolean> redstone = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "redstone", false));
    public final SingleProperty<Boolean> running = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "running", false));

    public Vector3d searchPos;
    private AxisAlignedBB searchArea;
    @Nullable
    public VirtualMissile tracking;

    public double clientRotation;
    public double clientRotationSpeed;

    public TileFireControlRadar() {
        super(BallistixTiles.TILE_FIRECONTROLRADAR.get());
        addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM).maxJoules(BallistixConstants.FIRE_CONTROL_RADAR_USAGE * 20));
        addComponent(new ComponentContainerProvider("firecontrolradar", this).createMenu((id, player) -> new ContainerFireControlRadar(id, player, new Inventory(0), getCoordsArray())));
        addComponent(new ComponentForgeEnergy(this));
    }
    
    @Override
    public void setLevelAndPosition(World world, BlockPos pos) {
    	super.setLevelAndPosition(world, pos);
    	searchPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    	searchArea = new AxisAlignedBB(getBlockPos()).inflate(BallistixConstants.FIRE_CONTROL_RADAR_RANGE);
    }

    public void tickServer(ComponentTickable tickable) {
        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        running.setValue(electro.getJoulesStored() > BallistixConstants.RADAR_USAGE / 20.0 && level.getBrightness(LightType.SKY, getBlockPos()) > 0 && (!usingRedstone.getValue() || usingRedstone.getValue() && redstone.getValue()));

        if (!running.getValue()) {
            tracking = null;
            TileESMTower.removeFireControlRadar(this);
            return;
        }

        TileESMTower.addFireControlRadar(this);

        electro.joules(electro.getJoulesStored() - BallistixConstants.RADAR_USAGE / 20.0);

        if (tracking != null && (tracking.hasExploded() || tracking.getId() == null || MissileManager.getMissile(level.dimension(), tracking.getId()) == null)) {
            tracking = null;
            trackingPos.setValue(OUT_OF_REACH);
        }

        VirtualMissile temp = null;

        for (VirtualMissile missile : MissileManager.getMissilesForLevel(level.dimension())) {
            if (missile.getBoundingBox().intersects(searchArea)) {
                if (temp == null && (!usingWhitelist.getValue() || usingWhitelist.getValue() && !whitelistedFrequencies.getValue().contains(missile.payloadData.frequency)) && !missile.hasExploded()) {
                    temp = missile;
                } else if (temp != null && getDistanceToMissile(searchPos, missile.position) < getDistanceToMissile(searchPos, temp.position) && (!usingWhitelist.getValue() || usingWhitelist.getValue() && !whitelistedFrequencies.getValue().contains(missile.payloadData.frequency)) && !missile.hasExploded()) {
                    temp = missile;
                }
            }
        }

        if (tracking == null) {
            tracking = temp;
        }

        if (tracking != null && !tracking.hasExploded()) {
            trackingPos.setValue(tracking.position);
            missileType.setValue(tracking.payloadData.missileType);
            if (trackingPos.getValue().distanceTo(new Vector3d(worldPosition.getX(), trackingPos.getValue().y, worldPosition.getZ())) > BallistixConstants.FIRE_CONTROL_RADAR_RANGE) {
                tracking = null;
                trackingPos.setValue(OUT_OF_REACH);
            }
        } else {
            trackingPos.setValue(OUT_OF_REACH);
            missileType.setValue(-1);
        }
    }

    public void tickClient(ComponentTickable tickable) {
        clientRotation += clientRotationSpeed;

        clientRotationSpeed = MathHelper.clamp(clientRotationSpeed + 0.25 * (running.getValue() ? 1 : -1), 0.0, 20.0);
        if (tickable.getTicks() % PULSE_TIME_TICKS == 0 && running.getValue()) {
            SoundAPI.playSound(BallistixSounds.SOUND_FIRECONTROLRADAR.get(), SoundCategory.BLOCKS, 1.0F, 1.0F, worldPosition);
        }
    }

    @Override
    public void onNeightborChanged(BlockPos neighbor, boolean blockStateTrigger) {
        super.onNeightborChanged(neighbor, blockStateTrigger);
        redstone.setValue(level.getBestNeighborSignal(getBlockPos()) > 0);
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
            ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, false, true);
        }

    }

    @Override
    public void onPlace(BlockState oldState, boolean isMoving) {
        super.onPlace(oldState, isMoving);
        if (!level.isClientSide) {
            ChunkPos pos = level.getChunk(getBlockPos()).getPos();
            ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, true, true);
        }
    }

    @Override
    public int getComparatorSignal() {
        return trackingPos.getValue().equals(OUT_OF_REACH) ? 0 : 15;
    }

}
