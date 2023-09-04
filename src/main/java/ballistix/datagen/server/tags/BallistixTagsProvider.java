package ballistix.datagen.server.tags;

import java.util.concurrent.CompletableFuture;

import ballistix.datagen.server.tags.types.BallistixBlockTagsProvider;
import ballistix.datagen.server.tags.types.BallistixDamageTagsProvider;
import ballistix.datagen.server.tags.types.BallistixItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixTagsProvider {

	public static void addTagProviders(DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
		BallistixBlockTagsProvider blockProvider = new BallistixBlockTagsProvider(output, lookupProvider, helper);
		generator.addProvider(true, blockProvider);
		generator.addProvider(true, new BallistixItemTagsProvider(output, lookupProvider, blockProvider, helper));
		generator.addProvider(true, new BallistixDamageTagsProvider(output, lookupProvider, helper));
	}
	
}
