package ballistix.datagen.client;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ballistix.References;
import ballistix.client.ClientRegister;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixItems;
import electrodynamics.datagen.client.ElectrodynamicsItemModelsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixItemModelsProvider extends ElectrodynamicsItemModelsProvider {

	private static final NumberFormat FORMATTER = new DecimalFormat("00");

	private static final float[] TRACKER_ANGLES = {

			0.484375F, 0.515625F, 0.546875F, 0.578125F, 0.609375F, 0.640625F, 0.671875F, 0.703125F, 0.734375F, 0.765625F, 0.796875F, 0.828125F, 0.859375F, 0.890625F, 0.921875F, 0.953125F, 0.000000F, 0.015625F, 0.046875F, 0.078125F, 0.109375F, 0.140625F, 0.171875F, 0.203125F, 0.234375F, 0.265625F, 0.296875F, 0.328125F, 0.359375F, 0.390625F, 0.421875F, 0.453125F, 0.984375F

	};

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

		genTracker();

	}

	private void genTracker() {

		// 16 is default layer
		ItemModelBuilder builder = layeredBuilder(name(BallistixItems.ITEM_TRACKER), Parent.GENERATED, itemLoc("tracker/tracker_16"));

		// 0-15
		for (int i = 0; i <= 15; i++) {
			String number = FORMATTER.format(i);
			builder = builder.override().model(layeredBuilder(name(BallistixItems.ITEM_TRACKER) + "_" + number, Parent.GENERATED, itemLoc("tracker/tracker_" + number))).predicate(ClientRegister.ANGLE_PREDICATE, TRACKER_ANGLES[i]).end();
		}

		// base
		builder = builder.override().model(builder).predicate(ClientRegister.ANGLE_PREDICATE, TRACKER_ANGLES[16]).end();

		// 17-31
		for (int i = 17; i <= 31; i++) {
			String number = FORMATTER.format(i);
			builder = builder.override().model(layeredBuilder(name(BallistixItems.ITEM_TRACKER) + "_" + number, Parent.GENERATED, itemLoc("tracker/tracker_" + number))).predicate(ClientRegister.ANGLE_PREDICATE, TRACKER_ANGLES[i]).end();
		}

		// base
		builder = builder.override().model(builder).predicate(ClientRegister.ANGLE_PREDICATE, TRACKER_ANGLES[32]).end();

	}

}
