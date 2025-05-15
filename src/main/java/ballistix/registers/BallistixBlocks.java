package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import voltaic.api.registration.BulkRegistryObject;
import voltaic.common.block.BlockMachine;

public class BallistixBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Ballistix.ID);

	public static final BulkRegistryObject<BlockMachine, SubtypeBallistixMachine> BLOCKS_BALLISTIXMACHINE = new BulkRegistryObject<>(SubtypeBallistixMachine.values(), subtype -> BLOCKS.register(subtype.tag(), () -> new BlockMachine(subtype)));
	public static final BulkRegistryObject<BlockExplosive, SubtypeBlast> BLOCKS_EXPLOSIVE = new BulkRegistryObject<>(SubtypeBlast.values(), subtype -> BLOCKS.register(subtype.tag(), () -> new BlockExplosive(subtype)));

}
