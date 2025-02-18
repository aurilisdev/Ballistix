package ballistix.registers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ballistix.References;
import ballistix.common.block.BlockESMTower;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import electrodynamics.api.ISubtype;
import electrodynamics.prefab.block.GenericMachineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);

	public static final HashMap<ISubtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();

	public static BlockMissileSilo blockMissileSilo;
	public static GenericMachineBlock blockRadar;
	public static GenericMachineBlock blockFireControlRadar;
	public static BlockESMTower blockEsmTower;
	public static GenericMachineBlock blockSamTurret;
	public static GenericMachineBlock blockCiwsTurret;
	public static GenericMachineBlock blockLaserTurret;
	public static GenericMachineBlock blockRailgunTurret;

	static {
		BLOCKS.register("missilesilo", () -> blockMissileSilo = new BlockMissileSilo());
		BLOCKS.register("radar", () -> blockRadar = new GenericMachineBlock(TileSearchRadar::new));
		BLOCKS.register("firecontrolradar", () -> blockFireControlRadar = new GenericMachineBlock(TileFireControlRadar::new));
		BLOCKS.register("esmtower", () -> blockEsmTower = new BlockESMTower());
		BLOCKS.register("samturret", () -> blockSamTurret = new GenericMachineBlock(TileTurretSAM::new));
		BLOCKS.register("ciwsturret", () -> blockCiwsTurret = new GenericMachineBlock(TileTurretCIWS::new));
		BLOCKS.register("laserturret", () -> blockLaserTurret = new GenericMachineBlock(TileTurretLaser::new));
		BLOCKS.register("railgunturret", () -> blockRailgunTurret = new GenericMachineBlock(TileTurretRailgun::new));
		
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			SUBTYPEBLOCKREGISTER_MAPPINGS.put(subtype, BLOCKS.register(subtype.tag(), () -> new BlockExplosive(subtype)));
		}
	}

	public static Block[] getAllBlockForSubtype(ISubtype[] values) {
		List<Block> list = new ArrayList<>();
		for (ISubtype value : values) {
			list.add(SUBTYPEBLOCKREGISTER_MAPPINGS.get(value).get());
		}
		return list.toArray(new Block[] {});
	}

	public static Block getBlock(ISubtype value) {
		return SUBTYPEBLOCKREGISTER_MAPPINGS.get(value).get();
	}

}
