package ballistix.datagen.client;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import electrodynamics.datagen.client.ElectrodynamicsBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixBlockStateProvider extends ElectrodynamicsBlockStateProvider {

	public BallistixBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, exFileHelper, References.ID);
	}

	@Override
	protected void registerStatesAndModels() {

		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.attractive), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.breaching), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.chemical), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.condensive), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.contagious), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.debilitation), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.emp), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.fragmentation), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.incendiary), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.nuclear), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.obsidian), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.repulsive), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.shrapnel), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		simpleExplosive(BallistixBlocks.getBlock(SubtypeBlast.thermobaric), ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);

		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.antimatter), existingBlock(BallistixBlocks.getBlock(SubtypeBlast.antimatter)), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.darkmatter), existingBlock(BallistixBlocks.getBlock(SubtypeBlast.darkmatter)), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.landmine), existingBlock(BallistixBlocks.getBlock(SubtypeBlast.landmine)), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.largeantimatter), existingBlock(BallistixBlocks.getBlock(SubtypeBlast.largeantimatter)), true);

		horrRotatedBlock(BallistixBlocks.blockMissileSilo, existingBlock(BallistixBlocks.blockMissileSilo), 90, 0, false);

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
			return super.toString().toLowerCase().replaceAll("_", "");
		}

	}

}
