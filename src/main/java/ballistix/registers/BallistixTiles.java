package ballistix.registers;

import com.google.common.collect.Sets;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.tile.TileAirRaidSiren;
import ballistix.common.tile.TileESMTower;
import ballistix.common.tile.TileProximityDetector;
import ballistix.common.tile.TileVerticalLaunchSilo;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.common.tile.silo.TileLauncherControlPanelT2;
import ballistix.common.tile.silo.TileLauncherControlPanelT3;
import ballistix.common.tile.silo.TileLauncherPlatformT1;
import ballistix.common.tile.silo.TileLauncherPlatformT2;
import ballistix.common.tile.silo.TileLauncherPlatformT3;
import ballistix.common.tile.silo.TileLauncherSupportFrameT1;
import ballistix.common.tile.silo.TileLauncherSupportFrameT2;
import ballistix.common.tile.silo.TileLauncherSupportFrameT3;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixTiles {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Ballistix.ID);
	
	public static final RegistryObject<BlockEntityType<TileLauncherControlPanelT1>> TILE_LAUNCHER_CONTROL_PANEL_TIER1 = BLOCK_ENTITY_TYPES.register("launchercontrolpaneltier1", () -> new BlockEntityType<>(TileLauncherControlPanelT1::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherControlPanelT2>> TILE_LAUNCHER_CONTROL_PANEL_TIER2 = BLOCK_ENTITY_TYPES.register("launchercontrolpaneltier2", () -> new BlockEntityType<>(TileLauncherControlPanelT2::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherControlPanelT3>> TILE_LAUNCHER_CONTROL_PANEL_TIER3 = BLOCK_ENTITY_TYPES.register("launchercontrolpaneltier3", () -> new BlockEntityType<>(TileLauncherControlPanelT3::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherSupportFrameT1>> TILE_LAUNCHER_SUPPORT_FRAME_TIER1 = BLOCK_ENTITY_TYPES.register("launchersupportframetier1", () -> new BlockEntityType<>(TileLauncherSupportFrameT1::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherSupportFrameT2>> TILE_LAUNCHER_SUPPORT_FRAME_TIER2 = BLOCK_ENTITY_TYPES.register("launchersupportframetier2", () -> new BlockEntityType<>(TileLauncherSupportFrameT2::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherSupportFrameT3>> TILE_LAUNCHER_SUPPORT_FRAME_TIER3 = BLOCK_ENTITY_TYPES.register("launchersupportframetier3", () -> new BlockEntityType<>(TileLauncherSupportFrameT3::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherPlatformT1>> TILE_LAUNCHER_PLATFORM_TIER1 = BLOCK_ENTITY_TYPES.register("launcherplatformtier1", () -> new BlockEntityType<>(TileLauncherPlatformT1::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherPlatformT2>> TILE_LAUNCHER_PLATFORM_TIER2 = BLOCK_ENTITY_TYPES.register("launcherplatformtier2", () -> new BlockEntityType<>(TileLauncherPlatformT2::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2)), null));
	public static final RegistryObject<BlockEntityType<TileLauncherPlatformT3>> TILE_LAUNCHER_PLATFORM_TIER3 = BLOCK_ENTITY_TYPES.register("launcherplatformtier3", () -> new BlockEntityType<>(TileLauncherPlatformT3::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3)), null));
	public static final RegistryObject<BlockEntityType<TileSearchRadar>> TILE_RADAR = BLOCK_ENTITY_TYPES.register("radar", () -> new BlockEntityType<>(TileSearchRadar::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar)), null));
	public static final RegistryObject<BlockEntityType<TileFireControlRadar>> TILE_FIRECONTROLRADAR = BLOCK_ENTITY_TYPES.register("firecontrolradar", () -> new BlockEntityType<>(TileFireControlRadar::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar)), null));
	public static final RegistryObject<BlockEntityType<TileESMTower>> TILE_ESMTOWER = BLOCK_ENTITY_TYPES.register("esmtower", () -> new BlockEntityType<>(TileESMTower::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower)), null));
	public static final RegistryObject<BlockEntityType<TileTurretSAM>> TILE_SAMTURRET = BLOCK_ENTITY_TYPES.register("samturret", () -> new BlockEntityType<>(TileTurretSAM::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret)), null));
	public static final RegistryObject<BlockEntityType<TileTurretCIWS>> TILE_CIWSTURRET = BLOCK_ENTITY_TYPES.register("ciwsturret", () -> new BlockEntityType<>(TileTurretCIWS::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret)), null));
	public static final RegistryObject<BlockEntityType<TileTurretLaser>> TILE_LASERTURRET = BLOCK_ENTITY_TYPES.register("laserturret", () -> new BlockEntityType<>(TileTurretLaser::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret)), null));
	public static final RegistryObject<BlockEntityType<TileTurretRailgun>> TILE_RAILGUNTURRET = BLOCK_ENTITY_TYPES.register("railgunturret", () -> new BlockEntityType<>(TileTurretRailgun::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret)), null));
	public static final RegistryObject<BlockEntityType<TileVerticalLaunchSilo>> TILE_VLS = BLOCK_ENTITY_TYPES.register("vls", () -> new BlockEntityType<>(TileVerticalLaunchSilo::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls)), null));
	public static final RegistryObject<BlockEntityType<TileProximityDetector>> TILE_PROXIMITYDETECTOR = BLOCK_ENTITY_TYPES.register("proximitydetector", () -> new BlockEntityType<>(TileProximityDetector::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.proximitydetector)), null));
	public static final RegistryObject<BlockEntityType<TileAirRaidSiren>> TILE_AIRRAIDSIREN = BLOCK_ENTITY_TYPES.register("airraidsiren", () -> new BlockEntityType<>(TileAirRaidSiren::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.airraidsiren)), null));


}
