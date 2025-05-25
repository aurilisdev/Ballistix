package ballistix.datagen.client;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.datagen.utils.client.BaseItemModelsProvider;

public class BallistixItemModelsProvider extends BaseItemModelsProvider {

	public BallistixItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, existingFileHelper, Ballistix.ID);
	}

	@Override
	protected void registerModels() {

		for (SubtypeGrenade grenade : SubtypeGrenade.values()) {
			layeredItem(BallistixItems.ITEMS_GRENADE.getValue(grenade), Parent.GENERATED, itemLoc("grenade/" + name(BallistixItems.ITEMS_GRENADE.getValue(grenade))));
		}

		for (SubtypeMinecart minecart : SubtypeMinecart.values()) {
			layeredItem(BallistixItems.ITEMS_MINECART.getValue(minecart), Parent.GENERATED, itemLoc("minecart/" + name(BallistixItems.ITEMS_MINECART.getValue(minecart))));

		}

		layeredItem(BallistixItems.ITEM_DEFUSER, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_DEFUSER)));
		layeredItem(BallistixItems.ITEM_DUSTPOISON, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_DUSTPOISON)));
		layeredItem(BallistixItems.ITEM_LASERDESIGNATOR, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_LASERDESIGNATOR)));
		layeredItem(BallistixItems.ITEM_RADARGUN, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_RADARGUN)));
		layeredItem(BallistixItems.ITEM_SCANNER, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_SCANNER)));
		layeredItem(BallistixItems.ITEM_BULLET, Parent.GENERATED, itemLoc(name(BallistixItems.ITEM_BULLET)));

		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), existingBlock(blockLoc("launchercontrolpaneltier1"))).transforms().transform(ItemDisplayContext.GUI).scale(0.6f).rotation(30.0F, 225.0F, 0.0F).translation(0, -1, 0).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), existingBlock(blockLoc("launchercontrolpaneltier2"))).transforms().transform(ItemDisplayContext.GUI).scale(0.6f).rotation(30.0F, 225.0F, 0.0F).translation(0, -1, 0).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), existingBlock(blockLoc("launchercontrolpaneltier3"))).transforms().transform(ItemDisplayContext.GUI).scale(0.6f).rotation(30.0F, 225.0F, 0.0F).translation(0, -1, 0).end();

		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), existingBlock(blockLoc("launchersupportframetier1"))).transforms().transform(ItemDisplayContext.GUI).scale(0.45F).rotation(30.0F, 225.0F, 0.0F).translation(0, -3.2f, 0).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), existingBlock(blockLoc("launchersupportframetier2"))).transforms().transform(ItemDisplayContext.GUI).scale(0.45F).rotation(30.0F, 225.0F, 0.0F).translation(0, -3.2f, 0).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), existingBlock(blockLoc("launchersupportframetier3"))).transforms().transform(ItemDisplayContext.GUI).scale(0.45F).rotation(30.0F, 225.0F, 0.0F).translation(0, -3.2f, 0).end();

		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1), existingBlock(blockLoc("launcherplatformtier1"))).transforms().transform(ItemDisplayContext.GUI).scale(0.4F).translation(0, -2.2f, 0).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), existingBlock(blockLoc("launcherplatformtier2"))).transforms().transform(ItemDisplayContext.GUI).scale(0.4F).translation(0, -2.2f, 0).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), existingBlock(blockLoc("launcherplatformtier3"))).transforms().transform(ItemDisplayContext.GUI).scale(0.4F).translation(0, -2.2f, 0).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar), existingBlock(blockLoc("radarfull"))).transforms()
				//
				.transform(ItemDisplayContext.GUI).rotation(30, 225, 0).translation(0, -1.5F, 0).scale(0.45F).end()
				//
				.transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25F).end()
				//
				.transform(ItemDisplayContext.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5F).end()
				//
				.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40F).end()
				//
				.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(75, 225, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40F).end();

		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar), existingBlock(blockLoc("firecontrolradarfull"))).transforms()
				//
				.transform(ItemDisplayContext.GUI).rotation(30, 225, 0).translation(0, -1.5F, 0).scale(0.45F).end()
				//
				.transform(ItemDisplayContext.GROUND).rotation(0, 0, 0).translation(0, 3, 0).scale(0.25F).end()
				//
				.transform(ItemDisplayContext.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5F).end()
				//
				.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, 0, 0).scale(0.40F).end()
				//
				.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(75, 225, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				//
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, 0, 0).scale(0.40F).end();

		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret), existingBlock(blockLoc("samturretitem"))).transforms().transform(ItemDisplayContext.GUI).scale(0.5F).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), existingBlock(blockLoc("esmtower"))).transforms().transform(ItemDisplayContext.GUI).scale(0.3F).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret), existingBlock(blockLoc("ciwsturretitem"))).transforms().transform(ItemDisplayContext.GUI).scale(0.5F).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret), existingBlock(blockLoc("laserturretitem"))).transforms().transform(ItemDisplayContext.GUI).scale(0.5F).rotation(30.0F, 225.0F, 0.0F).end();
		simpleBlockItem(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret), existingBlock(blockLoc("railgunturretitem"))).transforms().transform(ItemDisplayContext.GUI).scale(0.5F).rotation(30.0F, 225.0F, 0.0F).end();

		//layeredBuilder(name(BallistixItems.ITEM_RANGEUPGRADE), Parent.GENERATED, itemLoc("upgrade/" + SubtypeItemUpgrade.range.tag())).transforms().transform(ItemDisplayContext.GUI).scale(0.8F).end();
	}

}
