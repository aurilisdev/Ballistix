package ballistix.datagen.client;

import java.util.Locale;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import voltaic.Voltaic;
import voltaic.datagen.utils.client.BaseBlockstateProvider;

public class BallistixBlockStateProvider extends BaseBlockstateProvider {

	public BallistixBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, exFileHelper, Ballistix.ID);
		exFileHelper.trackGenerated(Voltaic.rl("block/steelcasing"), new ResourceType(ResourcePackType.CLIENT_RESOURCES, ".png", "textures"));
	}

	@Override
	protected void registerStatesAndModels() {

		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.attractive),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.breaching),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.chemical),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.condensive),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.contagious),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.debilitation),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.emp),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.incendiary),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.nuclear),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.obsidian),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.repulsive),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);
		// simpleExplosive(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric),
		// ExplosiveParent.EXPLOSIVE_MODEL_ONE, true);

		// Tier 0
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.obsidian), existingBlock(blockLoc("explosiveobsidian")), true);
		// Tier 1
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.chemical), existingBlock(blockLoc("explosivechemical")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.attractive), existingBlock(blockLoc("explosiveattractive")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.repulsive), existingBlock(blockLoc("explosiverepulsive")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.incendiary), existingBlock(blockLoc("explosiveincendiary")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel), existingBlock(blockLoc("explosiveshrapnel")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.condensive), existingBlock(blockLoc("explosivecondensive")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.anvil), existingBlock(blockLoc("explosiveanvil")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.infestive), existingBlock(blockLoc("explosiveinfestive")), true);
		// Tier 2
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric), existingBlock(blockLoc("explosivethermobaric")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.breaching), existingBlock(blockLoc("explosivebreaching")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.debilitation), existingBlock(blockLoc("explosivedebilitation")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.contagious), existingBlock(blockLoc("explosivecontagious")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation), existingBlock(blockLoc("explosivefragmentation")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.sonic), existingBlock(blockLoc("explosivesonic")), true);
		// Tier 3
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.antigravity), existingBlock(blockLoc("explosiveantigravity")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.emp), existingBlock(blockLoc("explosiveemp")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.exothermic), existingBlock(blockLoc("explosiveexothermic")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.nuclear), existingBlock(blockLoc("explosivenuclear")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.endothermic), existingBlock(blockLoc("explosiveendothermic")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.ender), existingBlock(blockLoc("explosiveender")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.hypersonic), existingBlock(blockLoc("explosivehypersonic")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.rejuvination), existingBlock(blockLoc("explosiverejuvination")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.antimatter), existingBlock(blockLoc("explosiveantimatter")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter), existingBlock(blockLoc("explosivedarkmatter")), true);
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter), existingBlock(blockLoc("explosivelargeantimatter")), true);
		// Other
		simpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.landmine), existingBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.landmine)), true);

		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3)), 90, 0, false);

		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3)), 90, 0, false);

		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3)), 90, 0, false);

		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar)), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret)), false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower)), false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret)), false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret)), false);
		horrRotatedBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret)), false);
		simpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls)), true).transforms().transform(Perspective.GUI).rotation(30, 225, 0).translation(0, -2.8F, 0).scale(0.4F).end();
		horrRotatedLitBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.airraidsiren), existingBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.airraidsiren)), existingBlock(blockLoc("airraidsirenon")), 90, 0, true);
		simpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.proximitydetector), //
				models().cube(SubtypeBallistixMachine.proximitydetector.tag(), blockLoc("proximitydetector_bottom"), blockLoc("proximitydetector_top"), blockLoc("proximitydetector_side"), blockLoc("proximitydetector_side"), blockLoc("proximitydetector_side"), blockLoc("proximitydetector_side")).texture("particle", Voltaic.rl("block/steelcasing")),
				//
				true);


	}

	@SuppressWarnings("unused")
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
