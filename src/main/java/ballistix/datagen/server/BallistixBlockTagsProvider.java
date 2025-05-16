package ballistix.datagen.server;

import ballistix.Ballistix;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, Ballistix.ID, existingFileHelper);
	}

	@Override
	protected void addTags() {

	}

}
