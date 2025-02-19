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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixBlockTypes {
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, References.ID);
	
	public static final RegistryObject<BlockEntityType<TileMissileSilo>> TILE_MISSILESILO = BLOCK_ENTITY_TYPES.register("missilesilo", () -> new BlockEntityType<>(TileMissileSilo::new, Sets.newHashSet(blockMissileSilo), null));
	public static final RegistryObject<BlockEntityType<TileSearchRadar>> TILE_RADAR = BLOCK_ENTITY_TYPES.register("radar", () -> new BlockEntityType<>(TileSearchRadar::new, Sets.newHashSet(BallistixBlocks.blockRadar), null));
	public static final RegistryObject<BlockEntityType<TileFireControlRadar>> TILE_FIRECONTROLRADAR = BLOCK_ENTITY_TYPES.register("firecontrolradar", () -> new BlockEntityType<>(TileFireControlRadar::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockFireControlRadar)), null));
	public static final RegistryObject<BlockEntityType<TileESMTower>> TILE_ESMTOWER = BLOCK_ENTITY_TYPES.register("esmtower", () -> new BlockEntityType<>(TileESMTower::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockEsmTower)), null));
	public static final RegistryObject<BlockEntityType<TileTurretSAM>> TILE_SAMTURRET = BLOCK_ENTITY_TYPES.register("samturret", () -> new BlockEntityType<>(TileTurretSAM::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockSamTurret)), null));
	public static final RegistryObject<BlockEntityType<TileTurretCIWS>> TILE_CIWSTURRET = BLOCK_ENTITY_TYPES.register("ciwsturret", () -> new BlockEntityType<>(TileTurretCIWS::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockCiwsTurret)), null));
	public static final RegistryObject<BlockEntityType<TileTurretLaser>> TILE_LASERTURRET = BLOCK_ENTITY_TYPES.register("laserturret", () -> new BlockEntityType<>(TileTurretLaser::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockLaserTurret)), null));
	public static final RegistryObject<BlockEntityType<TileTurretRailgun>> TILE_RAILGUNTURRET = BLOCK_ENTITY_TYPES.register("railgunturret", () -> new BlockEntityType<>(TileTurretRailgun::new, Sets.newHashSet(Sets.newHashSet(BallistixBlocks.blockRailgunTurret)), null));

}
