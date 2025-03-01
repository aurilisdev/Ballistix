package ballistix.datagen.client;

import java.util.Locale;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import electrodynamics.datagen.client.ElectrodynamicsBlockStateProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixBlockStateProvider extends ElectrodynamicsBlockStateProvider {

	public BallistixBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, exFileHelper, References.ID);
	}

	@Override
	protected void registerStatesAndModels() {

		// Tier 0
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.obsidian), existingBlock(blockLoc("explosiveobsidian")), true);
		// Tier 1
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.chemical), existingBlock(blockLoc("explosivechemical")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.attractive), existingBlock(blockLoc("explosiveattractive")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.repulsive), existingBlock(blockLoc("explosiverepulsive")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.incendiary), existingBlock(blockLoc("explosiveincendiary")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.shrapnel), existingBlock(blockLoc("explosiveshrapnel")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.condensive), existingBlock(blockLoc("explosivecondensive")), true);
		// Tier 2
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.thermobaric), existingBlock(blockLoc("explosivethermobaric")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.breaching), existingBlock(blockLoc("explosivebreaching")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.debilitation), existingBlock(blockLoc("explosivedebilitation")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.contagious), existingBlock(blockLoc("explosivecontagious")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.fragmentation), existingBlock(blockLoc("explosivefragmentation")), true);
		// Tier 3
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.emp), existingBlock(blockLoc("explosiveemp")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.nuclear), existingBlock(blockLoc("explosivenuclear")), true);
		// Tier 4
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.antimatter), existingBlock(blockLoc("explosiveantimatter")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.darkmatter), existingBlock(blockLoc("explosivedarkmatter")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.largeantimatter), existingBlock(blockLoc("explosivelargeantimatter")), true);
		// Other
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.landmine), existingBlock(BallistixBlocks.getBlock(SubtypeBlast.landmine)), true);

		horrRotatedBlock(BallistixBlocks.blockMissileSilo, existingBlock(BallistixBlocks.blockMissileSilo), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.blockRadar, existingBlock(BallistixBlocks.blockRadar), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.blockFireControlRadar, existingBlock(BallistixBlocks.blockFireControlRadar), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.blockSamTurret, existingBlock(BallistixBlocks.blockSamTurret), false);
		horrRotatedBlock(BallistixBlocks.blockEsmTower, existingBlock(BallistixBlocks.blockEsmTower), false);
		horrRotatedBlock(BallistixBlocks.blockCiwsTurret, existingBlock(BallistixBlocks.blockCiwsTurret), false);
		horrRotatedBlock(BallistixBlocks.blockLaserTurret, existingBlock(BallistixBlocks.blockLaserTurret), false);
		horrRotatedBlock(BallistixBlocks.blockRailgunTurret, existingBlock(BallistixBlocks.blockRailgunTurret), false);

	}

	private void simpleExplosive(Block block, ExplosiveParent parent, boolean registerItem) {
		BlockModelBuilder builder = models().withExistingParent(name(block), blockLoc(parent.toString())).texture("3", blockLoc(name(block) + "base")).texture("particle", "#3");
		getVariantBuilder(block).partialState().setModels(new ConfiguredModel(builder));
		if (registerItem) {
			simpleBlockItem(block, builder);
		}
	}

	public enum ExplosiveParent {

		EXPLOSIVE_MODEL_ONE;

		@Override
		public String toString() {
			return super.toString().toLowerCase(Locale.ROOT).replaceAll("_", "");
		}

	}

}
