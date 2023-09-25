package ballistix.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import ballistix.References;
import ballistix.registers.BallistixDamageTypes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BallistixDamageTagsProvider extends DamageTypeTagsProvider {

	public BallistixDamageTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, References.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).add(BallistixDamageTypes.CHEMICAL_GAS);
		tag(DamageTypeTags.BYPASSES_EFFECTS).add(BallistixDamageTypes.CHEMICAL_GAS, BallistixDamageTypes.SHRAPNEL); // bypasses magic
	}

}
