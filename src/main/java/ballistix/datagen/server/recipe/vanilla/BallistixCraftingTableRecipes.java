package ballistix.datagen.server.recipe.vanilla;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.common.recipe.recipeutils.ChargedItemIngredient;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixItems;
import electrodynamics.Electrodynamics;
import electrodynamics.common.block.subtype.SubtypeMachine;
import electrodynamics.common.block.subtype.SubtypeWire;
import electrodynamics.common.tags.ElectrodynamicsTags;
import electrodynamics.datagen.utils.recipe.AbstractRecipeGenerator;
import electrodynamics.datagen.utils.recipe.ShapedCraftingRecipeBuilder;
import electrodynamics.datagen.utils.recipe.ShapelessCraftingRecipeBuilder;
import electrodynamics.prefab.item.ItemElectric;
import electrodynamics.prefab.utilities.object.TransferPack;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

public class BallistixCraftingTableRecipes extends AbstractRecipeGenerator {

	@Override
	public void addRecipes(RecipeOutput output) {
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SSS")
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "launcherplatformtier1", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SPS")
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('P', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1))
				//
				.complete(References.ID, "launcherplatformtier2", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SPS")
				//
				.addKey('S', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.addKey('P', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2))
				//
				.complete(References.ID, "launcherplatformtier3", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SSS")
				//
				.addPattern("S S")
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.complete(References.ID, "launchersupportframetier1", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SVS")
				//
				.addPattern("S S")
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('V', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1))
				//
				.complete(References.ID, "launchersupportframetier2", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SVS")
				//
				.addPattern("S S")
				//
				.addKey('S', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('V', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2))
				//
				.complete(References.ID, "launchersupportframetier3", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), 1)
				//
				.addPattern("SGS")
				//
				.addPattern("SCS")
				//
				.addPattern("SLS")
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.addKey('G', Tags.Items.GLASS_BLOCKS)
				//
				.addKey('L', Items.LEVER)
				//
				.complete(References.ID, "launchercontrolpaneltier1", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), 1)
				//
				.addPattern("SWS")
				//
				.addPattern("SCS")
				//
				.addPattern("SWS")
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('W', ElectrodynamicsTags.Items.INSULATED_COPPER_WIRES)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.complete(References.ID, "launchercontrolpaneltier2", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), 1)
				//
				.addPattern("SWS")
				//
				.addPattern("SCS")
				//
				.addPattern("SWS")
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('W', ElectrodynamicsTags.Items.INSULATED_GOLD_WIRES)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.complete(References.ID, "launchercontrolpaneltier3", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar), 1)
				//
				.addPattern("WRW")
				//
				.addPattern(" M ")
				//
				.addPattern("PCP")
				//
				.addKey('W', ElectrodynamicsItems.ITEMS_WIRE.getValue(SubtypeWire.gold))
				//
				.addKey('R', BallistixItems.ITEM_RADARGUN.get())
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "radar", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar), 1)
				//
				.addPattern(" G ")
				//
				.addPattern("CRC")
				//
				.addPattern("PMP")
				//
				.addKey('G', BallistixItems.ITEM_RADARGUN.get())
				//
				.addKey('R', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar))
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.complete(References.ID, "fire_control_radar", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), 1)
				//
				.addPattern("AAA")
				//
				.addPattern("WRW")
				//
				.addPattern("PCP")
				//
				.addKey('A', ElectrodynamicsTags.Items.PLATE_ALUMINUM)
				//
				.addKey('R', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar))
				//
				.addKey('W', ElectrodynamicsItems.ITEMS_WIRE.getValue(SubtypeWire.gold))
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.complete(References.ID, "esm_tower", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("PMP")
				//
				.addPattern("PCP")
				//
				.addKey('S', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1))
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "turret_sam", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret), 1)
				//
				.addPattern("PPC")
				//
				.addPattern(" M ")
				//
				.addPattern("PCP")
				//
				.addKey('C', Tags.Items.CHESTS)
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "turret_ciws", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret), 1)
				//
				.addPattern("GDG")
				//
				.addPattern(" M ")
				//
				.addPattern("PCP")
				//
				.addKey('G', Tags.Items.GLASS_BLOCKS)
				//
				.addKey('D', Tags.Items.GEMS_DIAMOND)
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "turret_laser", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret), 1)
				//
				.addPattern("OOH")
				//
				.addPattern(" MT")
				//
				.addPattern("PCP")
				//
				.addKey('O', ElectrodynamicsItems.ITEM_COIL.get())
				//
				.addKey('H', Tags.Items.CHESTS)
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('T', ElectrodynamicsItems.ITEMS_MACHINE.getValue(SubtypeMachine.upgradetransformer))
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.complete(References.ID, "turret_railgun", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1), 1)
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
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.complete(References.ID, "missile_tier1", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2), 1)
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
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('M', BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1))
				//
				.complete(References.ID, "missile_tier2", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3), 1)
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
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('M', BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2))
				//
				.complete(References.ID, "missile_tier3", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_AAMISSILE.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.complete(References.ID, "sam_mark_1", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_AAMISSILEMK2.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PSP")
				//
				.addPattern("PGP")
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_STEEL)
				//
				.addKey('S', BallistixItems.ITEM_AAMISSILE.get())
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ELITE)
				//
				.complete(References.ID, "sam_mark_2", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_BULLET.get(), 4)
				//
				.addPattern(" P ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', ElectrodynamicsTags.Items.PLATE_BRONZE)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.complete(References.ID, "bullet", output);

		addExplosives(output);
		addGear(output);

	}

	private void addExplosives(RecipeOutput output) {
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antimatter), 1)
				//
				.addPattern("CCC")
				//
				.addPattern("CNC")
				//
				.addPattern("CCC")
				//
				.addKey('C', BallistixTags.Items.CELL_ANTIMATTER_LARGE)
				//
				.addKey('N', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.nuclear))
				//
				.complete(References.ID, "explosive_antimatter", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("CAC")
				//
				.addPattern(" C ")
				//
				.addKey('C', BallistixTags.Items.CELL_ANTIMATTER_VERY_LARGE)
				//
				.addKey('A', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antimatter))
				//
				.complete(References.ID, "explosive_antimatterlarge", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.attractive), 1)
				//
				.addPattern("CDC")
				//
				.addKey('D', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('C', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive))
				//
				.complete(References.ID, "explosive_attractive", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.breaching), 1)
				//
				.addPattern("GCG")
				//
				.addPattern("GCG")
				//
				.addPattern("GCG")
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive))
				//
				.complete(References.ID, "explosive_breaching", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.chemical), 1)
				//
				.addPattern("PPP")
				//
				.addPattern("PDP")
				//
				.addPattern("PPP")
				//
				.addKey('P', BallistixTags.Items.DUST_POISON)
				//
				.addKey('D', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.debilitation))
				//
				.complete(References.ID, "explosive_chemical", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive), 3)
				//
				.addPattern("TRT")
				//
				.addKey('T', Items.TNT)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.complete(References.ID, "explosive_condensive", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.contagious), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("CRC")
				//
				.addPattern(" C ")
				//
				.addKey('R', Items.ROTTEN_FLESH)
				//
				.addKey('C', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.chemical))
				//
				.complete(References.ID, "explosive_contagious", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("DAD")
				//
				.addPattern("DDD")
				//
				.addKey('D', BallistixTags.Items.CELL_DARK_MATTER)
				//
				.addKey('A', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter))
				//
				.complete(References.ID, "explosive_darkmatter", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.debilitation), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("WRW")
				//
				.addPattern("DDD")
				//
				.addKey('D', ElectrodynamicsTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.addKey('W', Items.WATER_BUCKET)
				//
				.complete(References.ID, "explosive_debilitation", output);

		ItemStack fullBattery = new ItemStack(ElectrodynamicsItems.ITEM_BATTERY.get());
		ItemElectric battery = (ItemElectric) fullBattery.getItem();

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.emp), 1)
				//
				.addPattern("DBD")
				//
				.addPattern("BTB")
				//
				.addPattern("DBD")
				//
				.addKey('D', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('B', new ChargedItemIngredient(Ingredient.of(ElectrodynamicsItems.ITEM_BATTERY.get()), TransferPack.joulesVoltage(battery.getElectricProperties().capacity, battery.getElectricProperties().receive.getVoltage()), false))
				//
				.addKey('T', Items.TNT)
				//
				.complete(References.ID, "explosive_emp", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("SIS")
				//
				.addPattern(" S ")
				//
				.addKey('S', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel))
				//
				.addKey('I', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.incendiary))
				//
				.complete(References.ID, "explosive_fragmentation", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.incendiary), 1)
				//
				.addPattern("SSS")
				//
				.addPattern("SRS")
				//
				.addPattern("SLS")
				//
				.addKey('S', ElectrodynamicsTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.addKey('L', Items.LAVA_BUCKET)
				//
				.complete(References.ID, "explosive_incendiary", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.nuclear), 1)
				//
				.addPattern("CTC")
				//
				.addPattern("TRT")
				//
				.addPattern("CTC")
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.addKey('T', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric))
				//
				.addKey('R', BallistixTags.Items.FUELROD_URANIUM_HIGH_EN)
				//
				.complete(References.ID, "explosive_nuclear", output);
		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.obsidian), 1)
				//
				.addPattern("OOO")
				//
				.addPattern("TRT")
				//
				.addPattern("OOO")
				//
				.addKey('O', Tags.Items.OBSIDIANS)
				//
				.addKey('T', Items.TNT)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.complete(References.ID, "explosive_obsidian", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive), 1)
				//
				.addPattern("CGC")
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive))
				//
				.complete(References.ID, "explosive_repulsive", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel), 1)
				//
				.addPattern("AAA")
				//
				.addPattern("ARA")
				//
				.addPattern("AAA")
				//
				.addKey('A', ItemTags.ARROWS)
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.complete(References.ID, "explosive_shrapnel", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric), 1)
				//
				.addPattern("CIC")
				//
				.addPattern("BRB")
				//
				.addPattern("CIC")
				//
				.addKey('C', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.chemical))
				//
				.addKey('I', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.incendiary))
				//
				.addKey('B', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.breaching))
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.complete(References.ID, "explosive_thermobaric", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.landmine), 1)
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
				.addKey('F', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation))
				//
				.complete(References.ID, "landmine", output);

		for (SubtypeMinecart minecart : SubtypeMinecart.values()) {
			ShapelessCraftingRecipeBuilder.start(BallistixItems.ITEMS_MINECART.getValue(minecart), 1)
					//
					.addIngredient(Items.MINECART)
					//
					.addIngredient(BallistixItems.ITEMS_EXPLOSIVE.getValue(minecart.explosiveType))
					//
					.complete(References.ID, minecart.tag(), output);
		}

		for (SubtypeGrenade grenade : SubtypeGrenade.values()) {

			ShapelessCraftingRecipeBuilder.start(BallistixItems.ITEMS_GRENADE.getValue(grenade), 1)
					//
					.addIngredient(BallistixItems.ITEMS_EXPLOSIVE.getValue(grenade.explosiveType))
					//
					.addIngredient(Tags.Items.GUNPOWDERS)
					//
					.addIngredient(Tags.Items.STRINGS)
					//
					.complete(References.ID, "grenade_" + grenade.name(), output);

		}

	}

	private void addGear(RecipeOutput output) {

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_DEFUSER.get(), 1)
				//
				.addPattern("W  ")
				//
				.addPattern(" SB")
				//
				.addPattern("  C")
				//
				.addKey('W', ElectrodynamicsItems.ITEMS_WIRE.getValue(SubtypeWire.copper))
				//
				.addKey('S', Items.SHEARS)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.addKey('B', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "defuser", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_LASERDESIGNATOR.get(), 1)
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
				.complete(References.ID, "laserdesignator", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_RADARGUN.get(), 1)
				//
				.addPattern("GCS")
				//
				.addPattern(" BS")
				//
				.addPattern(" AS")
				//
				.addKey('G', Tags.Items.GLASS_BLOCKS)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_BASIC)
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.addKey('A', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "radargun", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_ROCKETLAUNCHER.get(), 1)
				//
				.addPattern("  G")
				//
				.addPattern("SSC")
				//
				.addPattern("  B")
				//
				.addKey('G', Tags.Items.GLASS_BLOCKS)
				//
				.addKey('S', ElectrodynamicsTags.Items.INGOT_STEEL)
				//
				.addKey('C', ElectrodynamicsTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.complete(References.ID, "rocketlauncher", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_SCANNER.get(), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("STS")
				//
				.addPattern(" SB")
				//
				.addKey('S', Tags.Items.GEMS_AMETHYST)
				//
				.addKey('T', BallistixItems.ITEM_TRACKER.get())
				//
				.addKey('B', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "scanner", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_TRACKER.get(), 1)
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
				.complete(References.ID, "tracker", output);

	}

}
