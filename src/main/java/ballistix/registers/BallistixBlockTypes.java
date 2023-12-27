package ballistix.registers;

import static ballistix.registers.BallistixBlocks.blockMissileSilo;

import com.google.common.collect.Sets;

import ballistix.References;
import ballistix.common.tile.TileMissileSilo;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixBlockTypes {
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, References.ID);
	
	public static final RegistryObject<TileEntityType<TileMissileSilo>> TILE_MISSILESILO = BLOCK_ENTITY_TYPES.register("missilesilo", () -> new TileEntityType<>(TileMissileSilo::new, Sets.newHashSet(blockMissileSilo), null));

}
