package ballistix.registers;

import com.google.common.collect.Sets;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.tile.TileESMTower;
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
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixTiles {
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Ballistix.ID);
	
	public static final RegistryObject<TileEntityType<TileLauncherControlPanelT1>> TILE_LAUNCHER_CONTROL_PANEL_TIER1 = BLOCK_ENTITY_TYPES.register("launchercontrolpaneltier1", () -> new TileEntityType<>(TileLauncherControlPanelT1::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1)), null));
	public static final RegistryObject<TileEntityType<TileLauncherControlPanelT2>> TILE_LAUNCHER_CONTROL_PANEL_TIER2 = BLOCK_ENTITY_TYPES.register("launchercontrolpaneltier2", () -> new TileEntityType<>(TileLauncherControlPanelT2::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2)), null));
	public static final RegistryObject<TileEntityType<TileLauncherControlPanelT3>> TILE_LAUNCHER_CONTROL_PANEL_TIER3 = BLOCK_ENTITY_TYPES.register("launchercontrolpaneltier3", () -> new TileEntityType<>(TileLauncherControlPanelT3::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3)), null));
	public static final RegistryObject<TileEntityType<TileLauncherSupportFrameT1>> TILE_LAUNCHER_SUPPORT_FRAME_TIER1 = BLOCK_ENTITY_TYPES.register("launchersupportframetier1", () -> new TileEntityType<>(TileLauncherSupportFrameT1::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1)), null));
	public static final RegistryObject<TileEntityType<TileLauncherSupportFrameT2>> TILE_LAUNCHER_SUPPORT_FRAME_TIER2 = BLOCK_ENTITY_TYPES.register("launchersupportframetier2", () -> new TileEntityType<>(TileLauncherSupportFrameT2::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2)), null));
	public static final RegistryObject<TileEntityType<TileLauncherSupportFrameT3>> TILE_LAUNCHER_SUPPORT_FRAME_TIER3 = BLOCK_ENTITY_TYPES.register("launchersupportframetier3", () -> new TileEntityType<>(TileLauncherSupportFrameT3::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3)), null));
	public static final RegistryObject<TileEntityType<TileLauncherPlatformT1>> TILE_LAUNCHER_PLATFORM_TIER1 = BLOCK_ENTITY_TYPES.register("launcherplatformtier1", () -> new TileEntityType<>(TileLauncherPlatformT1::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1)), null));
	public static final RegistryObject<TileEntityType<TileLauncherPlatformT2>> TILE_LAUNCHER_PLATFORM_TIER2 = BLOCK_ENTITY_TYPES.register("launcherplatformtier2", () -> new TileEntityType<>(TileLauncherPlatformT2::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2)), null));
	public static final RegistryObject<TileEntityType<TileLauncherPlatformT3>> TILE_LAUNCHER_PLATFORM_TIER3 = BLOCK_ENTITY_TYPES.register("launcherplatformtier3", () -> new TileEntityType<>(TileLauncherPlatformT3::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3)), null));
	public static final RegistryObject<TileEntityType<TileSearchRadar>> TILE_RADAR = BLOCK_ENTITY_TYPES.register("radar", () -> new TileEntityType<>(TileSearchRadar::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar)), null));
	public static final RegistryObject<TileEntityType<TileFireControlRadar>> TILE_FIRECONTROLRADAR = BLOCK_ENTITY_TYPES.register("firecontrolradar", () -> new TileEntityType<>(TileFireControlRadar::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar)), null));
	public static final RegistryObject<TileEntityType<TileESMTower>> TILE_ESMTOWER = BLOCK_ENTITY_TYPES.register("esmtower", () -> new TileEntityType<>(TileESMTower::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower)), null));
	public static final RegistryObject<TileEntityType<TileTurretSAM>> TILE_SAMTURRET = BLOCK_ENTITY_TYPES.register("samturret", () -> new TileEntityType<>(TileTurretSAM::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret)), null));
	public static final RegistryObject<TileEntityType<TileTurretCIWS>> TILE_CIWSTURRET = BLOCK_ENTITY_TYPES.register("ciwsturret", () -> new TileEntityType<>(TileTurretCIWS::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret)), null));
	public static final RegistryObject<TileEntityType<TileTurretLaser>> TILE_LASERTURRET = BLOCK_ENTITY_TYPES.register("laserturret", () -> new TileEntityType<>(TileTurretLaser::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret)), null));
	public static final RegistryObject<TileEntityType<TileTurretRailgun>> TILE_RAILGUNTURRET = BLOCK_ENTITY_TYPES.register("railgunturret", () -> new TileEntityType<>(TileTurretRailgun::new, Sets.newHashSet(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret)), null));


}
