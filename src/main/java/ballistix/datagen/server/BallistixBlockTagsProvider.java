package ballistix.datagen.server;

import ballistix.Ballistix;
import ballistix.registers.BallistixBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
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

	}

}
