package ballistix.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import ballistix.References;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixItemTagsProvider extends ItemTagsProvider {

	public BallistixItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider provider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, provider.contentsGetter(), References.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider pProvider) {

		tag(BallistixTags.Items.DUST_POISON).add(BallistixItems.ITEM_DUSTPOISON.get());

	}

}
