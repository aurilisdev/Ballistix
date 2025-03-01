package ballistix.common.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ballistix.References;
import ballistix.common.block.BlockESMTower;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.registers.BallistixBlockTypes;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
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

public class TileESMTower extends GenericTile implements IMultiblockParentTile {

    public static final ConcurrentHashMap<RegistryKey<World>, HashSet<TileSearchRadar>> SEARCH_RADARS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<RegistryKey<World>, HashSet<TileFireControlRadar>> FIRE_CONTROL_RADARS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<RegistryKey<World>, HashSet<TileESMTower>> ESM_TOWERS = new ConcurrentHashMap<>();

    public final Property<Boolean> active = property(new Property<>(PropertyType.Boolean, "active", false));
    public final Property<Boolean> searchRadarDetected = property(new Property<>(PropertyType.Boolean, "searchradar", false));
    public final Property<ArrayList<BlockPos>> fireControlRadars = property(new Property<>(PropertyType.BlockPosList, "firecontrolradars", new ArrayList<BlockPos>()));

    private AxisAlignedBB searchArea;

    public TileESMTower() {
        super(BallistixBlockTypes.TILE_ESMTOWER.get());
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE * 4).setInputDirections(Direction.DOWN).maxJoules(Constants.ESM_TOWER_USAGE_PER_TICK * 20));
        addComponent(new ComponentContainerProvider("container.esmtower", this).createMenu((id, player) -> new ContainerESMTower(id, player, new Inventory(0), getCoordsArray())));
    }
    
    @Override
    public void setLevelAndPosition(World world, BlockPos pos) {
    	super.setLevelAndPosition(world, pos);
    	searchArea = new AxisAlignedBB(getBlockPos()).inflate(Constants.ESM_TOWER_SEARCH_RADIUS);
    }

    public void tickServer(ComponentTickable tickable) {

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        active.set(electro.getJoulesStored() > Constants.ESM_TOWER_USAGE_PER_TICK && level.getBrightness(LightType.SKY, getBlockPos()) > 0);

        if(!active.get()) {
            removeESMTower(this);
            searchRadarDetected.set(false);
            fireControlRadars.get().clear();
            fireControlRadars.forceDirty();
            return;
        }

        addESMTower(this);

        searchRadarDetected.set(false);
        fireControlRadars.get().clear();

        for(TileSearchRadar radar : SEARCH_RADARS.getOrDefault(getLevel().dimension(), new HashSet<>())) {
            if(searchArea.intersects(new AxisAlignedBB(radar.getBlockPos()))) {
                searchRadarDetected.set(true);
                break;
            }
        }

        for(TileFireControlRadar radar : FIRE_CONTROL_RADARS.getOrDefault(getLevel().dimension(), new HashSet<>())) {
            if(searchArea.intersects(new AxisAlignedBB(radar.getBlockPos()))) {
                fireControlRadars.get().add(radar.getBlockPos());
            }
        }

        fireControlRadars.forceDirty();


    }

    @Override
    public Subnode[] getSubNodes() {
        return BlockESMTower.SUBNODES;
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
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
    	return super.getRenderBoundingBox().inflate(0, 2, 0);
    }
    
    @EventBusSubscriber(modid = References.ID, bus = EventBusSubscriber.Bus.FORGE)
    private static class MapHandlerer {

        @SubscribeEvent
        public static void clearMaps(ServerTickEvent event) {
        	
        	if(event.phase == Phase.START) {
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

}
