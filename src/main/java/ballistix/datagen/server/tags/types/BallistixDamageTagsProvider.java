package ballistix.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import ballistix.Ballistix;
import ballistix.registers.BallistixDamageTypes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BallistixDamageTagsProvider extends DamageTypeTagsProvider {

	public BallistixDamageTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, Ballistix.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).add(BallistixDamageTypes.CHEMICAL_GAS);
		tag(DamageTypeTags.BYPASSES_EFFECTS).add(BallistixDamageTypes.CHEMICAL_GAS, BallistixDamageTypes.SHRAPNEL, BallistixDamageTypes.CIWS_BULLET, BallistixDamageTypes.LASER_TURRET, BallistixDamageTypes.RAILGUN_ROUND); // bypasses magic
		tag(DamageTypeTags.NO_KNOCKBACK).add(BallistixDamageTypes.CHEMICAL_GAS, BallistixDamageTypes.LASER_TURRET, BallistixDamageTypes.VIRUS);
		//tag(DamageTypeTags.BYPASSES_COOLDOWN).add(BallistixDamageTypes.CIWS_BULLET, BallistixDamageTypes.LASER_TURRET);
	}

}
