package ballistix.common.tile.radar;

import java.util.ArrayList;
import java.util.HashSet;

import ballistix.Ballistix;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.radar.IDetected;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
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
import voltaic.api.sound.SoundAPI;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.*;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;

public class TileSearchRadar extends GenericTile {

    public final SingleProperty<Boolean> usingWhitelist = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "usingwhitelist", false));
    public final ListProperty<Integer> whitelistedFrequencies = property(new ListProperty<>(PropertyTypes.INTEGER_LIST, "whitelistedfreqs", new ArrayList<>()));
    public final SingleProperty<Boolean> redstone = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "redstone", false));
    public final SingleProperty<Boolean> isRunning = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "isrunning", false));

    private final AABB searchArea = new AABB(getBlockPos()).inflate(BallistixConstants.RADAR_RANGE);
    private final HashSet<VirtualMissile> trackedMissiles = new HashSet<>();
    public final HashSet<TileESMTower> trackedEsmTowers = new HashSet<>();
    public final HashSet<IDetected.Detected> detections = new HashSet<>();

    public double clientRotation;
    public double clientRotationSpeed;

    public TileSearchRadar(BlockPos pos, BlockState state) {
        super(BallistixTiles.TILE_RADAR.get(), pos, state);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM).maxJoules(BallistixConstants.RADAR_USAGE * 20));
        addComponent(new ComponentContainerProvider("searchradar", this).createMenu((id, player) -> new ContainerSearchRadar(id, player, new SimpleContainer(0), getCoordsArray())));
        addComponent(new ComponentForgeEnergy(this));
    }

    public void tickServer(ComponentTickable tickable) {
        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        isRunning.setValue(electro.getJoulesStored() > (BallistixConstants.RADAR_USAGE / 20.0) && level.getBrightness(LightLayer.SKY, getBlockPos()) > 0);

        trackedMissiles.clear();
        trackedEsmTowers.clear();

        if (!isRunning.getValue()) {
            if (redstone.getValue()) {
                redstone.setValue(false);
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
            TileESMTower.removeSearchRadar(this);
            return;
        }

        TileESMTower.addSearchRadar(this);

        electro.joules(electro.getJoulesStored() - (BallistixConstants.RADAR_USAGE / 20.0));

        for (VirtualMissile missile : MissileManager.getMissilesForLevel(level.dimension())) {
            if (missile.getBoundingBox().intersects(searchArea) && (!usingWhitelist.getValue() || (usingWhitelist.getValue() && !whitelistedFrequencies.getValue().contains(missile.payloadData.frequency))) && !missile.hasExploded()) {
                trackedMissiles.add(missile);
            }
        }

        for (TileESMTower tower : TileESMTower.ESM_TOWERS.getOrDefault(level.dimension(), new HashSet<>())) {
            if (new AABB(tower.getBlockPos()).intersects(searchArea)) {
                trackedEsmTowers.add(tower);
            }
        }

        if ((trackedMissiles.isEmpty() && trackedEsmTowers.isEmpty()) && redstone.getValue()) {
            redstone.setValue(false);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        } else if ((!trackedMissiles.isEmpty() || !trackedEsmTowers.isEmpty()) && !redstone.getValue()) {
            redstone.setValue(true);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }

        detections.clear();

        for (VirtualMissile missile : trackedMissiles) {
            detections.add(new IDetected.Detected(missile.position, BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.values()[missile.payloadData.missileType]), true));
        }

        for (TileESMTower tile : trackedEsmTowers) {
            detections.add(new IDetected.Detected(new Vec3(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ()), BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), false));
        }

    }

    public void tickClient(ComponentTickable tickable) {

        clientRotation += clientRotationSpeed;

        clientRotationSpeed = Mth.clamp(clientRotationSpeed + 0.25 * (isRunning.getValue() ? 1 : -1), 0.0, 10.0);

        if (tickable.getTicks() % 50 == 0 && isRunning.getValue()) {
            SoundAPI.playSound(BallistixSounds.SOUND_RADAR.get(), SoundSource.BLOCKS, 1.0F, 1.0F, worldPosition);
        }
    }

    @Override
    public int getSignal(Direction dir) {
        return redstone.getValue() ? 15 : 0;
    }

    @Override
    public int getComparatorSignal() {
        if (!trackedMissiles.isEmpty() && !trackedEsmTowers.isEmpty()) {
            return 15;
        } else if (trackedMissiles.isEmpty() && !trackedEsmTowers.isEmpty()) {
            return 8;
        } else {
            return 0;
        }
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();

        if (!level.isClientSide) {
            TileESMTower.removeSearchRadar(this);
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

}
