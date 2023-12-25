package ballistix.datagen.server;

import ballistix.References;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixItems;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixItemTagsProvider extends ItemTagsProvider {

	public BallistixItemTagsProvider(DataGenerator generator, BlockTagsProvider provider, ExistingFileHelper existingFileHelper) {
		super(generator, provider, References.ID, existingFileHelper);
	}

	@Override
	protected void addTags() {

		tag(BallistixTags.Items.DUST_POISON).add(BallistixItems.ITEM_DUSTPOISON.get());

	}

}
