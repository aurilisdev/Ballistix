package ballistix.common.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.registers.BallistixTiles;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.*;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;

public class TileESMTower extends GenericTile implements IMultiblockParentTile {

    public static final ConcurrentHashMap<RegistryKey<World>, HashSet<TileSearchRadar>> SEARCH_RADARS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<RegistryKey<World>, HashSet<TileFireControlRadar>> FIRE_CONTROL_RADARS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<RegistryKey<World>, HashSet<TileESMTower>> ESM_TOWERS = new ConcurrentHashMap<>();

    public final SingleProperty<Boolean> active = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "active", false));
    public final SingleProperty<Boolean> searchRadarDetected = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "searchradar", false));
    public final ListProperty<BlockPos> fireControlRadars = property(new ListProperty<>(PropertyTypes.BLOCK_POS_LIST, "firecontrolradars", new ArrayList<>())).setNoUpdateServer();

    private AxisAlignedBB searchArea;

    public TileESMTower() {
        super(BallistixTiles.TILE_ESMTOWER.get());
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE * 4).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM).maxJoules(BallistixConstants.ESM_TOWER_USAGE_PER_TICK * 20));
        addComponent(new ComponentContainerProvider("esmtower", this).createMenu((id, player) -> new ContainerESMTower(id, player, new Inventory(0), getCoordsArray())));
        addComponent(new ComponentForgeEnergy(this));
    }
    
    @Override
    public void setLevelAndPosition(World world, BlockPos pos) {
    	super.setLevelAndPosition(world, pos);
    	searchArea = new AxisAlignedBB(getBlockPos()).inflate(BallistixConstants.ESM_TOWER_SEARCH_RADIUS);
    }

    public void tickServer(ComponentTickable tickable) {

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        active.setValue(electro.getJoulesStored() > BallistixConstants.ESM_TOWER_USAGE_PER_TICK && level.getBrightness(LightType.SKY, getBlockPos()) > 0);

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
            if (searchArea.intersects(new AxisAlignedBB(radar.getBlockPos()))) {
                searchRadarDetected.setValue(true);
                break;
            }
        }

        for (TileFireControlRadar radar : FIRE_CONTROL_RADARS.getOrDefault(getLevel().dimension(), new HashSet<>())) {
            if (searchArea.intersects(new AxisAlignedBB(radar.getBlockPos()))) {
                fireControlRadars.addValue(radar.getBlockPos());
            }
        }


    }

    @Override
    public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
        return SubtypeBallistixMachine.Subnodes.ESM_TOWER;
    }
    
    @Override
    public ActionResultType onSubnodeUse(PlayerEntity player, Hand hand, BlockRayTraceResult hit, TileMultiSubnode subnode) {
    	return use(player, hand, hit);
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

    @EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
    public static class MapHandlerer {

        @SubscribeEvent
        public static void clearMaps(ServerTickEvent event) {
        	
        	if(event.phase != Phase.END) {
        		return;
        	}

            Iterator<Map.Entry<RegistryKey<World>, HashSet<TileSearchRadar>>> searchIterator = SEARCH_RADARS.entrySet().iterator();

            while (searchIterator.hasNext()) {
                Map.Entry<RegistryKey<World>, HashSet<TileSearchRadar>> entry = searchIterator.next();

                Iterator<TileSearchRadar> it = entry.getValue().iterator();

                while (it.hasNext()) {
                    TileSearchRadar radar = it.next();

                    if (radar == null || radar.isRemoved()) {
                        it.remove();
                    }
                }

            }

            Iterator<Map.Entry<RegistryKey<World>, HashSet<TileFireControlRadar>>> fireIterator = FIRE_CONTROL_RADARS.entrySet().iterator();

            while (fireIterator.hasNext()) {
                Map.Entry<RegistryKey<World>, HashSet<TileFireControlRadar>> entry = fireIterator.next();

                Iterator<TileFireControlRadar> it = entry.getValue().iterator();

                while (it.hasNext()) {
                    TileFireControlRadar radar = it.next();

                    if (radar == null || radar.isRemoved()) {
                        it.remove();
                    }
                }

            }

            Iterator<Map.Entry<RegistryKey<World>, HashSet<TileESMTower>>> esmIterator = ESM_TOWERS.entrySet().iterator();

            while(esmIterator.hasNext()) {
                Map.Entry<RegistryKey<World>, HashSet<TileESMTower>> entry = esmIterator.next();

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
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
    	return super.getRenderBoundingBox().inflate(0, 2, 0);
    }

}
