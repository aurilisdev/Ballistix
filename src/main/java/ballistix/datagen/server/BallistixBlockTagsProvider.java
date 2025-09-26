package ballistix.datagen.server;

import ballistix.Ballistix;
import ballistix.common.tags.BallistixTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, Ballistix.ID, existingFileHelper);
	}

	@Override
	protected void addTags() {

		tag(BallistixTags.Blocks.WHITELISTED_TURRET_BLOCKS)
				//
				.addTags(BlockTags.FLOWERS, BlockTags.CORALS, BlockTags.FIRE, BlockTags.SAPLINGS, BlockTags.CROPS, BlockTags.BUTTONS, BlockTags.PRESSURE_PLATES, BlockTags.CLIMBABLE)
				//
				.add(Blocks.SNOW, Blocks.GRASS, Blocks.TALL_GRASS, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.VINE, Blocks.CAKE);

	}

}
