package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import voltaic.api.registration.BulkDeferredHolder;
import voltaic.common.block.BlockMachine;

public class BallistixBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Ballistix.ID);

	public static final BulkDeferredHolder<Block, BlockMachine, SubtypeBallistixMachine> BLOCKS_BALLISTIXMACHINE = new BulkDeferredHolder<>(SubtypeBallistixMachine.values(), subtype -> BLOCKS.register(subtype.tag(), () -> new BlockMachine(subtype)));
	public static final BulkDeferredHolder<Block, BlockExplosive, SubtypeBlast> BLOCKS_EXPLOSIVE = new BulkDeferredHolder<>(SubtypeBlast.values(), subtype -> BLOCKS.register(subtype.tag(), () -> new BlockExplosive(subtype)));

}
