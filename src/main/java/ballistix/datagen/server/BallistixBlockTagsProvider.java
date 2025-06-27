package ballistix.datagen.server;

import ballistix.Ballistix;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.common.block.BlockMachine;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, Ballistix.ID, existingFileHelper);
	}

	@Override
	protected void addTags() {

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getAllValuesArray(new BlockMachine[0]));

		tag(BlockTags.NEEDS_STONE_TOOL).add(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getAllValuesArray(new BlockMachine[0]));

		tag(BallistixTags.Blocks.WHITELISTED_TURRET_BLOCKS)
				//
				.addTags(BlockTags.FLOWERS, BlockTags.CORALS, BlockTags.FIRE, BlockTags.SAPLINGS, BlockTags.CROPS, BlockTags.BUTTONS, BlockTags.PRESSURE_PLATES, BlockTags.CLIMBABLE)
				//
				.add(Blocks.SNOW, Blocks.GRASS, Blocks.TALL_GRASS, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.VINE, Blocks.CAKE);

	}

}
