package ballistix.datagen;

import ballistix.References;
import ballistix.datagen.client.BallistixBlockStateProvider;
import ballistix.datagen.client.BallistixItemModelsProvider;
import ballistix.datagen.client.BallistixLangKeyProvider;
import ballistix.datagen.client.BallistixSoundProvider;
import ballistix.datagen.server.BallistixBlockTagsProvider;
import ballistix.datagen.server.BallistixItemTagsProvider;
import ballistix.datagen.server.BallistixLootTablesProvider;
import ballistix.datagen.server.recipe.BallistixRecipeProvider;
import electrodynamics.datagen.client.ElectrodynamicsLangKeyProvider.Locale;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {

			BallistixBlockTagsProvider blockProvider = new BallistixBlockTagsProvider(generator, event.getExistingFileHelper());
			generator.addProvider(blockProvider);
			generator.addProvider(new BallistixItemTagsProvider(generator, blockProvider, event.getExistingFileHelper()));
			generator.addProvider(new BallistixLootTablesProvider(generator));
			generator.addProvider(new BallistixRecipeProvider(generator));

		}
		if (event.includeClient()) {
			generator.addProvider(new BallistixBlockStateProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new BallistixItemModelsProvider(generator, event.getExistingFileHelper()));
			generator.addProvider(new BallistixLangKeyProvider(generator, Locale.EN_US));
			generator.addProvider(new BallistixSoundProvider(generator, event.getExistingFileHelper()));
		}
	}

}
