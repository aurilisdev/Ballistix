package ballistix.common.tile.radar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ballistix.References;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.radar.IDetected;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.settings.Constants;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class TileSearchRadar extends GenericTile {

    public final Property<Boolean> usingWhitelist = property(new Property<>(PropertyType.Boolean, "usingwhitelist", false));
    public final Property<List<Integer>> whitelistedFrequencies = property(new Property<>(PropertyType.IntegerList, "whitelistedfreqs", new ArrayList<>()));
    public final Property<Boolean> redstone = property(new Property<>(PropertyType.Boolean, "redstone", false));
    public final Property<Boolean> isRunning = property(new Property<>(PropertyType.Boolean, "isrunning", false));

    private final AABB searchArea = new AABB(getBlockPos()).inflate(Constants.RADAR_RANGE);
    private final HashSet<VirtualMissile> trackedMissiles = new HashSet<>();
    private final HashSet<TileESMTower> trackedEsmTowers = new HashSet<>();
    public final HashSet<IDetected.Detected> detections = new HashSet<>();

    public double clientRotation;
    public double clientRotationSpeed;

    public TileSearchRadar(BlockPos pos, BlockState state) {
        super(BallistixBlockTypes.TILE_RADAR.get(), pos, state);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE).setInputDirections(Direction.DOWN).maxJoules(Constants.RADAR_USAGE * 20));
        addComponent(new ComponentContainerProvider("container.searchradar", this).createMenu((id, player) -> new ContainerSearchRadar(id, player, new SimpleContainer(0), getCoordsArray())));
    }

    public void tickServer(ComponentTickable tickable) {
        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        isRunning.set(electro.getJoulesStored() > (Constants.RADAR_USAGE / 20.0) && level.getBrightness(LightLayer.SKY, getBlockPos()) > 0);

        trackedMissiles.clear();
        trackedEsmTowers.clear();

        if (!isRunning.get()) {
            if (redstone.get()) {
                redstone.set(false);
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
            TileESMTower.removeSearchRadar(this);
            return;
        }

        TileESMTower.addSearchRadar(this);

        electro.joules(electro.getJoulesStored() - (Constants.RADAR_USAGE / 20.0));

        for (VirtualMissile missile : MissileManager.getMissilesForLevel(level.dimension())) {
            if (missile.getBoundingBox().intersects(searchArea) && (!usingWhitelist.get() || (usingWhitelist.get() && !whitelistedFrequencies.get().contains(missile.frequency))) && !missile.hasExploded()) {
                trackedMissiles.add(missile);
            }
        }

        for (TileESMTower tower : TileESMTower.ESM_TOWERS.getOrDefault(level.dimension(), new HashSet<>())) {
            if (new AABB(tower.getBlockPos()).intersects(searchArea)) {
                trackedEsmTowers.add(tower);
            }
        }

        if ((trackedMissiles.isEmpty() && trackedEsmTowers.isEmpty()) && redstone.get()) {
            redstone.set(false);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        } else if ((!trackedMissiles.isEmpty() || !trackedEsmTowers.isEmpty()) && !redstone.get()) {
            redstone.set(true);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }

        detections.clear();

        for (VirtualMissile missile : trackedMissiles) {
            detections.add(new IDetected.Detected(missile.position, BallistixItems.getItem(SubtypeMissile.values()[missile.missileType]), true));
        }

        for (TileESMTower tile : trackedEsmTowers) {
            detections.add(new IDetected.Detected(new Vec3(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ()), BallistixBlocks.blockEsmTower.asItem(), false));
        }

    }

    public void tickClient(ComponentTickable tickable) {

        clientRotation += clientRotationSpeed;

        clientRotationSpeed = Mth.clamp(clientRotationSpeed + 0.25 * (isRunning.get() ? 1 : -1), 0.0, 10.0);

        if (tickable.getTicks() % 50 == 0 && isRunning.get()) {
            SoundAPI.playSound(BallistixSounds.SOUND_RADAR.get(), SoundSource.BLOCKS, 1.0F, 1.0F, worldPosition);
        }
    }

    @Override
    public int getSignal(Direction dir) {
        return redstone.get() ? 15 : 0;
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();

        if(!level.isClientSide) {
            TileESMTower.removeSearchRadar(this);
            ChunkPos pos = level.getChunk(getBlockPos()).getPos();
            ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, getBlockPos(), pos.x, pos.z, false, true);
        }


    }

    @Override
    public void onPlace(BlockState oldState, boolean isMoving) {
        super.onPlace(oldState, isMoving);
        if(!level.isClientSide) {
            ChunkPos pos = level.getChunk(getBlockPos()).getPos();
            ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, getBlockPos(), pos.x, pos.z, true, true);
        }
    }

}
