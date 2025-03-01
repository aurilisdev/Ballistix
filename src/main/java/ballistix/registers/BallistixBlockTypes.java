package ballistix.registers;

import static ballistix.registers.BallistixBlocks.blockMissileSilo;

import com.google.common.collect.Sets;

import ballistix.References;
import ballistix.common.tile.TileESMTower;
import ballistix.common.tile.TileMissileSilo;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixBlockTypes {
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, References.ID);
	
	public static final RegistryObject<TileEntityType<TileMissileSilo>> TILE_MISSILESILO = BLOCK_ENTITY_TYPES.register("missilesilo", () -> new TileEntityType<>(TileMissileSilo::new, Sets.newHashSet(blockMissileSilo), null));
	public static final RegistryObject<TileEntityType<TileSearchRadar>> TILE_RADAR = BLOCK_ENTITY_TYPES.register("radar", () -> new TileEntityType<>(TileSearchRadar::new, Sets.newHashSet(BallistixBlocks.blockRadar), null));
	public static final RegistryObject<TileEntityType<TileFireControlRadar>> TILE_FIRECONTROLRADAR = BLOCK_ENTITY_TYPES.register("firecontrolradar", () -> new TileEntityType<>(TileFireControlRadar::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockFireControlRadar)), null));
	public static final RegistryObject<TileEntityType<TileESMTower>> TILE_ESMTOWER = BLOCK_ENTITY_TYPES.register("esmtower", () -> new TileEntityType<>(TileESMTower::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockEsmTower)), null));
	public static final RegistryObject<TileEntityType<TileTurretSAM>> TILE_SAMTURRET = BLOCK_ENTITY_TYPES.register("samturret", () -> new TileEntityType<>(TileTurretSAM::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockSamTurret)), null));
	public static final RegistryObject<TileEntityType<TileTurretCIWS>> TILE_CIWSTURRET = BLOCK_ENTITY_TYPES.register("ciwsturret", () -> new TileEntityType<>(TileTurretCIWS::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockCiwsTurret)), null));
	public static final RegistryObject<TileEntityType<TileTurretLaser>> TILE_LASERTURRET = BLOCK_ENTITY_TYPES.register("laserturret", () -> new TileEntityType<>(TileTurretLaser::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockLaserTurret)), null));
	public static final RegistryObject<TileEntityType<TileTurretRailgun>> TILE_RAILGUNTURRET = BLOCK_ENTITY_TYPES.register("railgunturret", () -> new TileEntityType<>(TileTurretRailgun::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockRailgunTurret)), null));

}
