package ballistix.datagen.client;

import ballistix.References;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import electrodynamics.datagen.client.ElectrodynamicsItemModelsProvider;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixItemModelsProvider extends ElectrodynamicsItemModelsProvider {

	public BallistixItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, existingFileHelper, References.ID);
	}

	@Override
	protected void registerModels() {

		for (SubtypeGrenade grenade : SubtypeGrenade.values()) {
			layeredItem(BallistixItems.getItem(grenade), Parent.GENERATED, itemLoc("grenade/" + name(BallistixItems.getItem(grenade))));
		}

		for (SubtypeMinecart minecart : SubtypeMinecart.values()) {
			layeredItem(BallistixItems.getItem(minecart), Parent.GENERATED, itemLoc("minecart/" + BallistixItems.getItem(minecart)));

		}

		layeredItem(BallistixItems.ITEM_DEFUSER, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_DEFUSER)));
		layeredItem(BallistixItems.ITEM_DUSTPOISON, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_DUSTPOISON)));
		layeredItem(BallistixItems.ITEM_LASERDESIGNATOR, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_LASERDESIGNATOR)));
		layeredItem(BallistixItems.ITEM_RADARGUN, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_RADARGUN)));
		layeredItem(BallistixItems.ITEM_SCANNER, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_SCANNER)));
		layeredItem(BallistixItems.ITEM_BULLET, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_BULLET)));

		simpleBlockItem(BallistixBlocks.blockRadar, existingBlock(blockLoc("radarfull"))).transforms()
				//
				.transform(TransformType.GUI).rotation(30, 225, 0).translation(0, -1.5F, 0).scale(0.45F).end()
				//
				.transform(TransformType.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25F).end()
				//
				.transform(TransformType.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5F).end()
				//
				.transform(TransformType.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(TransformType.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40F).end()
				//
				.transform(TransformType.THIRD_PERSON_LEFT_HAND).rotation(75, 225, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(TransformType.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40F).end();

		simpleBlockItem(BallistixBlocks.blockFireControlRadar, existingBlock(blockLoc("firecontrolradarfull"))).transforms()
				//
				.transform(TransformType.GUI).rotation(30, 225, 0).translation(0, -1.5F, 0).scale(0.45F).end()
				//
				.transform(TransformType.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25F).end()
				//
				.transform(TransformType.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5F).end()
				//
				.transform(TransformType.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(TransformType.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40F).end()
				//
				.transform(TransformType.THIRD_PERSON_LEFT_HAND).rotation(75, 225, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(TransformType.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40F).end();

		simpleBlockItem(BallistixBlocks.blockSamTurret, existingBlock(blockLoc("samturretitem")));
		simpleBlockItem(BallistixBlocks.blockEsmTower, existingBlock(blockLoc("esmtower"))).transforms().transform(TransformType.GUI).scale(0.3F).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.blockCiwsTurret, existingBlock(blockLoc("ciwsturretitem")));
		simpleBlockItem(BallistixBlocks.blockLaserTurret, existingBlock(blockLoc("laserturretitem")));
		simpleBlockItem(BallistixBlocks.blockRailgunTurret, existingBlock(blockLoc("railgunturretitem")));

	}


}
