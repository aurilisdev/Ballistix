package ballistix.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import ballistix.Ballistix;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import voltaic.common.block.BlockMachine;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, Ballistix.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getAllValuesArray(new BlockMachine[0]));

		tag(BlockTags.NEEDS_STONE_TOOL).add(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getAllValuesArray(new BlockMachine[0]));

		tag(BallistixTags.Blocks.WHITELISTED_TURRET_BLOCKS)
				//
				.addTags(BlockTags.FLOWERS, BlockTags.CORALS, BlockTags.FIRE, BlockTags.SAPLINGS, BlockTags.CROPS, BlockTags.BUTTONS, BlockTags.PRESSURE_PLATES, BlockTags.CLIMBABLE)
				//
				.add(Blocks.SNOW, Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.VINE, Blocks.CAKE);

	}

}
