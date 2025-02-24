package ballistix.datagen.server;

import ballistix.References;
import ballistix.registers.BallistixBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, References.ID, existingFileHelper);
	}

	@Override
	protected void addTags() {

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BallistixBlocks.blockMissileSilo, BallistixBlocks.blockRadar, BallistixBlocks.blockFireControlRadar, BallistixBlocks.blockEsmTower, BallistixBlocks.blockSamTurret, BallistixBlocks.blockCiwsTurret, BallistixBlocks.blockLaserTurret, BallistixBlocks.blockRailgunTurret);

		tag(BlockTags.NEEDS_STONE_TOOL).add(BallistixBlocks.blockMissileSilo, BallistixBlocks.blockRadar, BallistixBlocks.blockFireControlRadar, BallistixBlocks.blockEsmTower, BallistixBlocks.blockSamTurret, BallistixBlocks.blockCiwsTurret, BallistixBlocks.blockLaserTurret, BallistixBlocks.blockRailgunTurret);

	}

}
