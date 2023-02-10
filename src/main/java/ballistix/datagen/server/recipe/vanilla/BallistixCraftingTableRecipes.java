package ballistix.datagen.server.recipe.vanilla;

import java.util.function.Consumer;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import electrodynamics.common.block.subtype.SubtypeWire;
import electrodynamics.common.tags.ElectrodynamicsTags;
import electrodynamics.datagen.utils.recipe.AbstractRecipeGenerator;
import electrodynamics.datagen.utils.recipe.ElectrodynamicsShapedCraftingRecipe;
import electrodynamics.datagen.utils.recipe.ElectrodynamicsShapelessCraftingRecipe;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import nuclearscience.common.tags.NuclearScienceTags;

public class BallistixCraftingTableRecipes extends AbstractRecipeGenerator {

	@Override
	public void addRecipes(Consumer<FinishedRecipe> consumer) {

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.blockMissileSilo.asItem(), 1)
				//
				.addPattern("P P")
				//
				.addPattern("PCP")
				//
				.addPattern("PLP")
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.addKey('L', Items.LEVER)
				//
				.complete(References.ID, "missilesilo", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_MISSILECLOSERANGE.get(), 1)
				//
				.addPattern(" P ")
				//
				.addPattern("ICI")
				//
				.addPattern("IGI")
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('I', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.complete(References.ID, "missile_closerange", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_MISSILEMEDIUMRANGE.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('M', BallistixItems.ITEM_MISSILECLOSERANGE.get())
				//
				.complete(References.ID, "missile_mediumrange", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_MISSILELONGRANGE.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('M', BallistixItems.ITEM_MISSILEMEDIUMRANGE.get())
				//
				.complete(References.ID, "missile_longrange", consumer);

		addExplosives(consumer);
		addGear(consumer);

	}

	private void addExplosives(Consumer<FinishedRecipe> consumer) {

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.antimatter).asItem(), 1)
				//
				.addPattern("CCC")
				//
				.addPattern("CNC")
				//
				.addPattern("CCC")
				//
				.addKey('C', NuclearScienceTags.Items.CELL_ANTIMATTER_LARGE)
				//
				.addKey('N', BallistixBlocks.getBlock(SubtypeBlast.nuclear).asItem())
				//
				.complete(References.ID, "explosive_antimatter", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.largeantimatter).asItem(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("CAC")
				//
				.addPattern(" C ")
				//
				.addKey('C', NuclearScienceTags.Items.CELL_ANTIMATTER_VERY_LARGE)
				//
				.addKey('A', BallistixBlocks.getBlock(SubtypeBlast.antimatter).asItem())
				//
				.complete(References.ID, "explosive_antimatterlarge", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.attractive).asItem(), 1)
				//
				.addPattern("CDC")
				//
				.addKey('D', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.nuclear).asItem())
				//
				.complete(References.ID, "explosive_attractive", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.breaching).asItem(), 1)
				//
				.addPattern("GCG")
				//
				.addPattern("GCG")
				//
				.addPattern("GCG")
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.condensive).asItem())
				//
				.complete(References.ID, "explosive_breaching", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.chemical).asItem(), 1)
				//
				.addPattern("PPP")
				//
				.addPattern("PDP")
				//
				.addPattern("PPP")
				//
				.addKey('P', BallistixTags.Items.DUST_POISON)
				//
				.addKey('D', BallistixBlocks.getBlock(SubtypeBlast.debilitation).asItem())
				//
				.complete(References.ID, "explosive_chemical", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.condensive).asItem(), 3)
				//
				.addPattern("TRT")
				//
				.addKey('T', Items.TNT)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.complete(References.ID, "explosive_condensive", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.contagious).asItem(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("CRC")
				//
				.addPattern(" C ")
				//
				.addKey('R', Items.ROTTEN_FLESH)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.chemical).asItem())
				//
				.complete(References.ID, "explosive_contagious", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.darkmatter).asItem(), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("DAD")
				//
				.addPattern("DDD")
				//
				.addKey('D', NuclearScienceTags.Items.CELL_DARK_MATTER)
				//
				.addKey('A', BallistixBlocks.getBlock(SubtypeBlast.antimatter).asItem())
				//
				.complete(References.ID, "explosive_darkmatter", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.debilitation).asItem(), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("WRW")
				//
				.addPattern("DDD")
				//
				.addKey('D', ElectrodynamicsTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.addKey('W', Items.WATER_BUCKET)
				//
				.complete(References.ID, "explosive_debilitation", consumer);

		ItemStack fullBattery = new ItemStack(ElectrodynamicsItems.ITEM_BATTERY.get());
		ItemElectric battery = (ItemElectric) fullBattery.getItem();
		battery.receivePower(fullBattery, TransferPack.joulesVoltage(battery.getElectricProperties().capacity, battery.getElectricProperties().receive.getVoltage()), false);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.emp).asItem(), 1)
				//
				.addPattern("DBD")
				//
				.addPattern("BTB")
				//
				.addPattern("DBD")
				//
				.addKey('D', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('B', StrictNBTIngredient.of(fullBattery))
				//
				.addKey('T', Items.TNT)
				//
				.complete(References.ID, "explosive_emp", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.fragmentation).asItem(), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("SIS")
				//
				.addPattern(" S ")
				//
				.addKey('S', BallistixBlocks.getBlock(SubtypeBlast.shrapnel).asItem())
				//
				.addKey('I', BallistixBlocks.getBlock(SubtypeBlast.incendiary).asItem())
				//
				.complete(References.ID, "explosive_fragmentation", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.incendiary).asItem(), 1)
				//
				.addPattern("SSS")
				//
				.addPattern("SRS")
				//
				.addPattern("SLS")
				//
				.addKey('S', ElectrodynamicsTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.addKey('L', Items.LAVA_BUCKET)
				//
				.complete(References.ID, "explosive_incendiary", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.nuclear).asItem(), 1)
				//
				.addPattern("CTC")
				//
				.addPattern("TRT")
				//
				.addPattern("CTC")
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.addKey('T', BallistixBlocks.getBlock(SubtypeBlast.thermobaric).asItem())
				//
				.addKey('R', NuclearScienceTags.Items.FUELROD_URANIUM_HIGH_EN)
				//
				.complete(References.ID, "explosive_nuclear", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.antimatter).asItem(), 1)
				//
				.addPattern("OOO")
				//
				.addPattern("TRT")
				//
				.addPattern("OOO")
				//
				.addKey('O', Tags.Items.OBSIDIAN)
				//
				.addKey('T', Items.TNT)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.complete(References.ID, "explosive_obsidian", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem(), 1)
				//
				.addPattern("CGC")
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.condensive).asItem())
				//
				.complete(References.ID, "explosive_repulsive", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.shrapnel).asItem(), 1)
				//
				.addPattern("AAA")
				//
				.addPattern("ARA")
				//
				.addPattern("AAA")
				//
				.addKey('A', ItemTags.ARROWS)
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.complete(References.ID, "explosive_shrapnel", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.thermobaric).asItem(), 1)
				//
				.addPattern("CIC")
				//
				.addPattern("BRB")
				//
				.addPattern("CIC")
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.chemical).asItem())
				//
				.addKey('I', BallistixBlocks.getBlock(SubtypeBlast.incendiary).asItem())
				//
				.addKey('B', BallistixBlocks.getBlock(SubtypeBlast.breaching).asItem())
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.complete(References.ID, "explosive_thermobaric", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.landmine).asItem(), 1)
				//
				.addPattern("P")
				//
				.addPattern("R")
				//
				.addPattern("F")
				//
				.addKey('P', Items.STONE_PRESSURE_PLATE)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('F', BallistixBlocks.getBlock(SubtypeBlast.fragmentation).asItem())
				//
				.complete(References.ID, "landmine", consumer);

		for (SubtypeMinecart minecart : SubtypeMinecart.values()) {
			ElectrodynamicsShapelessCraftingRecipe.start(BallistixItems.getItem(minecart), 1)
					//
					.addIngredient(Items.MINECART)
					//
					.addIngredient(BallistixBlocks.getBlock(minecart.explosiveType).asItem())
					//
					.complete(References.ID, minecart.tag(), consumer);
		}

		for (SubtypeGrenade grenade : SubtypeGrenade.values()) {

			ElectrodynamicsShapelessCraftingRecipe.start(BallistixItems.getItem(grenade), 1)
					//
					.addIngredient(BallistixBlocks.getBlock(grenade.explosiveType).asItem())
					//
					.addIngredient(Tags.Items.GUNPOWDER)
					//
					.addIngredient(Tags.Items.STRING)
					//
					.complete(References.ID, "grenade_" + grenade.name(), consumer);

		}

	}

	private void addGear(Consumer<FinishedRecipe> consumer) {

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_DEFUSER.get(), 1)
				//
				.addPattern("W  ")
				//
				.addPattern(" S ")
				//
				.addPattern("  C")
				//
				.addKey('W', ElectrodynamicsItems.getItem(SubtypeWire.copper))
				//
				.addKey('S', Items.SHEARS)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "defuser", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_LASERDESIGNATOR.get(), 1)
				//
				.addPattern("G  ")
				//
				.addPattern(" C ")
				//
				.addPattern("  B")
				//
				.addKey('G', BallistixItems.ITEM_RADARGUN.get())
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.addKey('B', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "laserdesignator", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_RADARGUN.get(), 1)
				//
				.addPattern("GCS")
				//
				.addPattern(" BS")
				//
				.addPattern("  S")
				//
				.addKey('G', Tags.Items.GLASS)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.complete(References.ID, "radargun", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_ROCKETLAUNCHER.get(), 1)
				//
				.addPattern("  G")
				//
				.addPattern("SSC")
				//
				.addPattern("  B")
				//
				.addKey('G', Tags.Items.GLASS)
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.complete(References.ID, "rocketlauncher", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_SCANNER.get(), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("STS")
				//
				.addPattern(" S ")
				//
				.addKey('S', Tags.Items.GEMS_AMETHYST)
				//
				.addKey('T', BallistixItems.ITEM_TRACKER.get())
				//
				.complete(References.ID, "scanner", consumer);

		ElectrodynamicsShapedCraftingRecipe.start(BallistixItems.ITEM_TRACKER.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PBP")
				//
				.addPattern("PAP")
				//
				.addKey('C', Items.COMPASS)
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('B', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.addKey('A', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.complete(References.ID, "tracker", consumer);

	}

}
