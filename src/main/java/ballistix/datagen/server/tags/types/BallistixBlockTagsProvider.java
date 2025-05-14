package ballistix.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import ballistix.Ballistix;
import ballistix.registers.BallistixBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.common.block.BlockMachine;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, Ballistix.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getAllValuesArray(new BlockMachine[0]));

		tag(BlockTags.NEEDS_STONE_TOOL).add(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getAllValuesArray(new BlockMachine[0]));
	}

}
