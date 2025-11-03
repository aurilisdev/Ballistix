package ballistix.common.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
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

public class TileESMTower extends GenericTile implements IMultiblockParentTile {

    public static final ConcurrentHashMap<ResourceKey<Level>, HashSet<TileSearchRadar>> SEARCH_RADARS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<ResourceKey<Level>, HashSet<TileFireControlRadar>> FIRE_CONTROL_RADARS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<ResourceKey<Level>, HashSet<TileESMTower>> ESM_TOWERS = new ConcurrentHashMap<>();

    public final SingleProperty<Boolean> active = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "active", false));
    public final SingleProperty<Boolean> searchRadarDetected = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "searchradar", false));
    public final ListProperty<BlockPos> fireControlRadars = property(new ListProperty<>(PropertyTypes.BLOCK_POS_LIST, "firecontrolradars", new ArrayList<>())).setNoUpdateServer();

    private final AABB searchArea = new AABB(getBlockPos()).inflate(BallistixConfig.INSTANCE.ESM_TOWER_SEARCH_RADIUS.get());

    public TileESMTower(BlockPos worldPos, BlockState blockState) {
        super(BallistixTiles.TILE_ESMTOWER.get(), worldPos, blockState);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE * 4).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM).maxJoules(BallistixConfig.INSTANCE.ESM_TOWER_USAGE_PER_TICK.get() * 20));
        addComponent(new ComponentContainerProvider("esmtower", this).createMenu((id, player) -> new ContainerESMTower(id, player, new SimpleContainer(0), getCoordsArray())));
        addComponent(new ComponentForgeEnergy(this));
    }

    public void tickServer(ComponentTickable tickable) {

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        active.setValue(electro.getJoulesStored() > BallistixConfig.INSTANCE.ESM_TOWER_USAGE_PER_TICK.get() && level.getBrightness(LightLayer.SKY, getBlockPos()) > 0);

        if (!active.getValue()) {
            removeESMTower(this);
            searchRadarDetected.setValue(false);
            fireControlRadars.wipeList();
            return;
        }

        addESMTower(this);

        searchRadarDetected.setValue(false);
        fireControlRadars.wipeList();

        for (TileSearchRadar radar : SEARCH_RADARS.getOrDefault(getLevel().dimension(), new HashSet<>())) {
            if (searchArea.intersects(new AABB(radar.getBlockPos()))) {
                searchRadarDetected.setValue(true);
                break;
            }
        }

        for (TileFireControlRadar radar : FIRE_CONTROL_RADARS.getOrDefault(getLevel().dimension(), new HashSet<>())) {
            if (searchArea.intersects(new AABB(radar.getBlockPos()))) {
                fireControlRadars.addValue(radar.getBlockPos());
            }
        }


    }

    @Override
    public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
        return SubtypeBallistixMachine.Subnodes.ESM_TOWER;
    }

    @Override
    public InteractionResult onSubnodeUseWithoutItem(Player player, BlockHitResult hit, TileMultiSubnode subnode) {
        return useWithoutItem(player, hit);
    }

    @Override
    public void onSubnodeDestroyed(TileMultiSubnode tileMultiSubnode) {
        level.destroyBlock(worldPosition, true);
    }

    @Override
    public Direction getFacingDirection() {
        return getFacing();
    }

    public static void removeSearchRadar(TileSearchRadar radar) {
        SEARCH_RADARS.getOrDefault(radar.getLevel().dimension(), new HashSet<>()).remove(radar);
    }

    public static void removeFireControlRadar(TileFireControlRadar radar) {
        FIRE_CONTROL_RADARS.getOrDefault(radar.getLevel().dimension(), new HashSet<>()).remove(radar);
    }

    public static void removeESMTower(TileESMTower esm) {
        ESM_TOWERS.getOrDefault(esm.getLevel().dimension(), new HashSet<>()).remove(esm);
    }

    public static void addSearchRadar(TileSearchRadar radar) {
        HashSet<TileSearchRadar> radars = SEARCH_RADARS.getOrDefault(radar.getLevel().dimension(), new HashSet<>());
        radars.add(radar);
        SEARCH_RADARS.put(radar.getLevel().dimension(), radars);
    }

    public static void addFireControlRadar(TileFireControlRadar radar) {
        HashSet<TileFireControlRadar> radars = FIRE_CONTROL_RADARS.getOrDefault(radar.getLevel().dimension(), new HashSet<>());
        radars.add(radar);
        FIRE_CONTROL_RADARS.put(radar.getLevel().dimension(), radars);
    }

    public static void addESMTower(TileESMTower esm) {
        HashSet<TileESMTower> esmTowers = ESM_TOWERS.getOrDefault(esm.getLevel().dimension(), new HashSet<>());
        esmTowers.add(esm);
        ESM_TOWERS.put(esm.getLevel().dimension(), esmTowers);
    }

    @EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.GAME)
    private static class MapHandlerer {

        @SubscribeEvent
        private static void clearMaps(ServerTickEvent.Post event) {

            Iterator<Map.Entry<ResourceKey<Level>, HashSet<TileSearchRadar>>> searchIterator = SEARCH_RADARS.entrySet().iterator();

            while (searchIterator.hasNext()) {
                Map.Entry<ResourceKey<Level>, HashSet<TileSearchRadar>> entry = searchIterator.next();

                Iterator<TileSearchRadar> it = entry.getValue().iterator();

                while (it.hasNext()) {
                    TileSearchRadar radar = it.next();

                    if (radar == null || radar.isRemoved()) {
                        it.remove();
                    }
                }

            }

            Iterator<Map.Entry<ResourceKey<Level>, HashSet<TileFireControlRadar>>> fireIterator = FIRE_CONTROL_RADARS.entrySet().iterator();

            while (fireIterator.hasNext()) {
                Map.Entry<ResourceKey<Level>, HashSet<TileFireControlRadar>> entry = fireIterator.next();

                Iterator<TileFireControlRadar> it = entry.getValue().iterator();

                while (it.hasNext()) {
                    TileFireControlRadar radar = it.next();

                    if (radar == null || radar.isRemoved()) {
                        it.remove();
                    }
                }

            }

            Iterator<Map.Entry<ResourceKey<Level>, HashSet<TileESMTower>>> esmIterator = ESM_TOWERS.entrySet().iterator();

            while(esmIterator.hasNext()) {
                Map.Entry<ResourceKey<Level>, HashSet<TileESMTower>> entry = esmIterator.next();

                Iterator<TileESMTower> it = entry.getValue().iterator();

                while(it.hasNext()) {
                    TileESMTower radar = it.next();

                    if(radar == null || radar.isRemoved()) {
                        it.remove();
                    }
                }

            }

        }
    }

}
