package ballistix.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import ballistix.References;
import ballistix.registers.BallistixBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, References.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BallistixBlocks.blockMissileSilo, BallistixBlocks.blockRadar, BallistixBlocks.blockFireControlRadar, BallistixBlocks.blockEsmTower, BallistixBlocks.blockSamTurret, BallistixBlocks.blockCiwsTurret, BallistixBlocks.blockLaserTurret, BallistixBlocks.blockRailgunTurret);

		tag(BlockTags.NEEDS_STONE_TOOL).add(BallistixBlocks.blockMissileSilo, BallistixBlocks.blockRadar, BallistixBlocks.blockFireControlRadar, BallistixBlocks.blockEsmTower, BallistixBlocks.blockSamTurret, BallistixBlocks.blockCiwsTurret, BallistixBlocks.blockLaserTurret, BallistixBlocks.blockRailgunTurret);

	}

}
