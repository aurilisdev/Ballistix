package ballistix.datagen;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import ballistix.References;
import ballistix.datagen.client.BallistixBlockStateProvider;
import ballistix.datagen.client.BallistixItemModelsProvider;
import ballistix.datagen.client.BallistixLangKeyProvider;
import ballistix.datagen.client.BallistixSoundProvider;
import ballistix.datagen.server.BallistixLootTablesProvider;
import ballistix.datagen.server.recipe.BallistixRecipeProvider;
import ballistix.datagen.server.tags.BallistixTagsProvider;
import ballistix.registers.BallistixDamageTypes;
import electrodynamics.datagen.client.ElectrodynamicsLangKeyProvider.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();

		PackOutput output = generator.getPackOutput();

		ExistingFileHelper helper = event.getExistingFileHelper();

		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		
		if (event.includeServer()) {

			generator.addProvider(true, new LootTableProvider(output, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(BallistixLootTablesProvider::new, LootContextParamSets.BLOCK))));
			generator.addProvider(true, new BallistixRecipeProvider(output));
			
			DatapackBuiltinEntriesProvider datapacks = new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
					//
					.add(Registries.DAMAGE_TYPE, BallistixDamageTypes::registerTypes),
					//
					Set.of(References.ID));

			generator.addProvider(true, datapacks);
			BallistixTagsProvider.addTagProviders(generator, output, datapacks.getRegistryProvider(), helper);

		}
		if (event.includeClient()) {
			generator.addProvider(true, new BallistixBlockStateProvider(output, helper));
			generator.addProvider(true, new BallistixItemModelsProvider(output, helper));
			generator.addProvider(true, new BallistixLangKeyProvider(output, Locale.EN_US));
			generator.addProvider(true, new BallistixSoundProvider(output, helper));
		}
	}

}
