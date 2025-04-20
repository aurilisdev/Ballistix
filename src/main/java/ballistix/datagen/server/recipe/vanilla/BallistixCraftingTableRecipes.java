package ballistix.datagen.server.recipe.vanilla;

import ballistix.Ballistix;
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
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import voltaic.common.tags.VoltaicTags;
import voltaic.datagen.utils.server.recipe.AbstractRecipeGenerator;
import voltaic.datagen.utils.server.recipe.ShapedCraftingRecipeBuilder;
import voltaic.datagen.utils.server.recipe.ShapelessCraftingRecipeBuilder;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

public class BallistixCraftingTableRecipes extends AbstractRecipeGenerator {

	private static final ModLoadedCondition ELECTRO_LOADED = new ModLoadedCondition("electrodynamics");
	private static final NotCondition ELECTRO_NOT_LOADED = new NotCondition(ELECTRO_LOADED);

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
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launcherplatformtier1_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SSS")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.DUSTS_REDSTONE)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launcherplatformtier1_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SPS")
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('P', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1))
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launcherplatformtier2_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SPS")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.INGOTS_COPPER)
				//
				.addKey('P', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launcherplatformtier2_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SPS")
				//
				.addKey('S', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addKey('P', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2))
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launcherplatformtier3_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SCS")
				//
				.addPattern("SPS")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.GEMS_DIAMOND)
				//
				.addKey('P', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launcherplatformtier3_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SSS")
				//
				.addPattern("S S")
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launchersupportframetier1_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SSS")
				//
				.addPattern("S S")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launchersupportframetier1_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SVS")
				//
				.addPattern("S S")
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('V', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1))
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launchersupportframetier2_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SVS")
				//
				.addPattern("S S")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('V', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launchersupportframetier2_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SVS")
				//
				.addPattern("S S")
				//
				.addKey('S', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('V', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2))
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launchersupportframetier3_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), 1)
				//
				.addPattern("S S")
				//
				.addPattern("SVS")
				//
				.addPattern("S S")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('V', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launchersupportframetier3_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), 1)
				//
				.addPattern("SGS")
				//
				.addPattern("SCS")
				//
				.addPattern("SLS")
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('G', Tags.Items.GLASS_BLOCKS)
				//
				.addKey('L', Items.LEVER)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launchercontrolpaneltier1_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), 1)
				//
				.addPattern("SGS")
				//
				.addPattern("SCS")
				//
				.addPattern("SLS")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('G', Tags.Items.GLASS_BLOCKS)
				//
				.addKey('L', Items.LEVER)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launchercontrolpaneltier1_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), 1)
				//
				.addPattern("SWS")
				//
				.addPattern("SCS")
				//
				.addPattern("SWS")
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('W', VoltaicTags.Items.INSULATED_COPPER_WIRES)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launchercontrolpaneltier2_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), 1)
				//
				.addPattern("SWS")
				//
				.addPattern("SCS")
				//
				.addPattern("SWS")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('W', Tags.Items.INGOTS_COPPER)
				//
				.addKey('C', Tags.Items.INGOTS_GOLD)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launchercontrolpaneltier2_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), 1)
				//
				.addPattern("SWS")
				//
				.addPattern("SCS")
				//
				.addPattern("SWS")
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('W', VoltaicTags.Items.INSULATED_GOLD_WIRES)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "launchercontrolpaneltier3_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), 1)
				//
				.addPattern("SWS")
				//
				.addPattern("SCS")
				//
				.addPattern("SWS")
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('W', Tags.Items.INGOTS_GOLD)
				//
				.addKey('C', Tags.Items.GEMS_DIAMOND)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "launchercontrolpaneltier3_noelectro", output);

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
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "radar_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar), 1)
				//
				.addPattern("WRW")
				//
				.addPattern(" M ")
				//
				.addPattern("PCP")
				//
				.addKey('W', Tags.Items.INGOTS_GOLD)
				//
				.addKey('R', BallistixItems.ITEM_RADARGUN.get())
				//
				.addKey('M', Tags.Items.STORAGE_BLOCKS_REDSTONE)
				//
				.addKey('P', Items.IRON_BARS)
				//
				.addKey('C', Tags.Items.DUSTS_REDSTONE)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "radar_noelectro", output);

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
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "fire_control_radar_electro", output);

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
				.addKey('M', Tags.Items.STORAGE_BLOCKS_REDSTONE)
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.INGOTS_GOLD)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "fire_control_radar_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), 1)
				//
				.addPattern("AAA")
				//
				.addPattern("WRW")
				//
				.addPattern("PCP")
				//
				.addKey('A', VoltaicTags.Items.PLATE_ALUMINUM)
				//
				.addKey('R', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar))
				//
				.addKey('W', ElectrodynamicsItems.ITEMS_WIRE.getValue(SubtypeWire.gold))
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "esm_tower_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), 1)
				//
				.addPattern("AAA")
				//
				.addPattern("WRW")
				//
				.addPattern("PCP")
				//
				.addKey('A', Tags.Items.GEMS_DIAMOND)
				//
				.addKey('R', BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar))
				//
				.addKey('W', Tags.Items.INGOTS_GOLD)
				//
				.addKey('P', Items.IRON_BARS)
				//
				.addKey('C', Tags.Items.STORAGE_BLOCKS_IRON)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "esm_tower_noelectro", output);

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
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "turret_sam_electro", output);

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
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('M', Tags.Items.STORAGE_BLOCKS_REDSTONE)
				//
				.addKey('C', Tags.Items.INGOTS_COPPER)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "turret_sam_noelectro", output);

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
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "turret_ciws_electro", output);

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
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('M', Tags.Items.STORAGE_BLOCKS_REDSTONE)
				//
				.addKey('C', Tags.Items.INGOTS_COPPER)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "turret_ciws_noelectro", output);

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
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('M', ElectrodynamicsItems.ITEM_MOTOR.get())
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "turret_laser_electro", output);

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
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('M', Tags.Items.STORAGE_BLOCKS_REDSTONE)
				//
				.addKey('C', Tags.Items.INGOTS_COPPER)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "turret_laser_noelectro", output);

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
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "turret_railgun_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret), 1)
				//
				.addPattern("OOH")
				//
				.addPattern(" MT")
				//
				.addPattern("PCP")
				//
				.addKey('O', Tags.Items.INGOTS_COPPER)
				//
				.addKey('H', Tags.Items.CHESTS)
				//
				.addKey('M', Tags.Items.STORAGE_BLOCKS_REDSTONE)
				//
				.addKey('T', Tags.Items.STORAGE_BLOCKS_COPPER)
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.GEMS_DIAMOND)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "turret_railgun_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1), 1)
				//
				.addPattern(" P ")
				//
				.addPattern("ICI")
				//
				.addPattern("IGI")
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('I', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "missile_tier1_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1), 1)
				//
				.addPattern(" P ")
				//
				.addPattern("ICI")
				//
				.addPattern("IGI")
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('I', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "missile_tier1_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.addKey('M', BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1))
				//
				.complete(Ballistix.ID, "missile_tier2_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', Tags.Items.INGOTS_GOLD)
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.addKey('M', BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1))
				//
				.complete(Ballistix.ID, "missile_tier2_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('M', BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2))
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "missile_tier3_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', Tags.Items.GEMS_DIAMOND)
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('M', BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "missile_tier3_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_AAMISSILE.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "sam_mark_1_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_AAMISSILE.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', Tags.Items.INGOTS_GOLD)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "sam_mark_1_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_AAMISSILEMK2.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PSP")
				//
				.addPattern("PGP")
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('S', BallistixItems.ITEM_AAMISSILE.get())
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "sam_mark_2_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_AAMISSILEMK2.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PSP")
				//
				.addPattern("PGP")
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('S', BallistixItems.ITEM_AAMISSILE.get())
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', Tags.Items.GEMS_DIAMOND)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "sam_mark_2_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_BULLET.get(), 4)
				//
				.addPattern(" P ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', VoltaicTags.Items.PLATE_BRONZE)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "bullet_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_BULLET.get(), 4)
				//
				.addPattern(" P ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', Tags.Items.INGOTS_COPPER)
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "bullet_noelectro", output);

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
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "explosive_antimatter_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antimatter), 1)
				//
				.addPattern("CCC")
				//
				.addPattern("CNC")
				//
				.addPattern("CCC")
				//
				.addKey('C', Tags.Items.NETHER_STARS)
				//
				.addKey('N', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.nuclear))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "explosive_antimatter_noelectro", output);

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
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "explosive_antimatterlarge_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("CAC")
				//
				.addPattern(" C ")
				//
				.addKey('C', Tags.Items.NETHER_STARS)
				//
				.addKey('A', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antimatter))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "explosive_antimatterlarge_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.attractive), 1)
				//
				.addPattern("CDC")
				//
				.addKey('D', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('C', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive))
				//
				.complete(Ballistix.ID, "explosive_attractive", output);

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
				.complete(Ballistix.ID, "explosive_breaching", output);

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
				.complete(Ballistix.ID, "explosive_chemical", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive), 3)
				//
				.addPattern("TRT")
				//
				.addKey('T', Items.TNT)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.complete(Ballistix.ID, "explosive_condensive", output);

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
				.complete(Ballistix.ID, "explosive_contagious", output);

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
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "explosive_darkmatter_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("DAD")
				//
				.addPattern("DDD")
				//
				.addKey('D', Tags.Items.NETHER_STARS)
				//
				.addKey('A', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter))
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "explosive_darkmatter_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.debilitation), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("WRW")
				//
				.addPattern("DDD")
				//
				.addKey('D', VoltaicTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.addKey('W', Items.WATER_BUCKET)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "explosive_debilitation_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.debilitation), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("WRW")
				//
				.addPattern("DDD")
				//
				.addKey('D', Items.SPIDER_EYE)
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.addKey('W', Items.WATER_BUCKET)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "explosive_debilitation_noelectro", output);

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
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "explosive_emp_electro", output);

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
				.addKey('B', Tags.Items.INGOTS_COPPER)
				//
				.addKey('T', Items.TNT)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "explosive_emp_noelectro", output);

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
				.complete(Ballistix.ID, "explosive_fragmentation", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.incendiary), 1)
				//
				.addPattern("SSS")
				//
				.addPattern("SRS")
				//
				.addPattern("SLS")
				//
				.addKey('S', VoltaicTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.addKey('L', Items.LAVA_BUCKET)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "explosive_incendiary_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.incendiary), 1)
				//
				.addPattern("SSS")
				//
				.addPattern("SRS")
				//
				.addPattern("SLS")
				//
				.addKey('S', Items.FLINT_AND_STEEL)
				//
				.addKey('R', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive))
				//
				.addKey('L', Items.LAVA_BUCKET)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "explosive_incendiary_noelectro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.nuclear), 1)
				//
				.addPattern("CTC")
				//
				.addPattern("TRT")
				//
				.addPattern("CTC")
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('T', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric))
				//
				.addKey('R', BallistixTags.Items.FUELROD_URANIUM_HIGH_EN)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "explosive_nuclear_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.nuclear), 1)
				//
				.addPattern("CTC")
				//
				.addPattern("TRT")
				//
				.addPattern("CTC")
				//
				.addKey('C', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('T', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric))
				//
				.addKey('R', Items.NETHERITE_SCRAP)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "explosive_nuclear_noelectro", output);

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
				.complete(Ballistix.ID, "explosive_obsidian", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive), 1)
				//
				.addPattern("CGC")
				//
				.addKey('G', Tags.Items.GUNPOWDERS)
				//
				.addKey('C', BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive))
				//
				.complete(Ballistix.ID, "explosive_repulsive", output);

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
				.complete(Ballistix.ID, "explosive_shrapnel", output);

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
				.complete(Ballistix.ID, "explosive_thermobaric", output);

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
				.complete(Ballistix.ID, "landmine", output);

		for (SubtypeMinecart minecart : SubtypeMinecart.values()) {
			ShapelessCraftingRecipeBuilder.start(BallistixItems.ITEMS_MINECART.getValue(minecart), 1)
					//
					.addIngredient(Items.MINECART)
					//
					.addIngredient(BallistixItems.ITEMS_EXPLOSIVE.getValue(minecart.explosiveType))
					//
					.complete(Ballistix.ID, minecart.tag(), output);
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
					.complete(Ballistix.ID, "grenade_" + grenade.name(), output);

		}

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_RANGEUPGRADE.get(), 1)
				//
				.addPattern("PWP")
				//
				.addPattern("WBW")
				//
				.addPattern("PWP")
				//
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('W', Tags.Items.INGOTS_COPPER)
				//
				.addKey('B', Tags.Items.DUSTS_REDSTONE)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Electrodynamics.ID, "upgrade_range", output);

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
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('B', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "defuser_electro", output);

		ShapedCraftingRecipeBuilder.start(BallistixItems.ITEM_DEFUSER.get(), 1)
				//
				.addPattern("W  ")
				//
				.addPattern(" SB")
				//
				.addPattern("  C")
				//
				.addKey('W', Tags.Items.INGOTS_COPPER)
				//
				.addKey('S', Items.SHEARS)
				//
				.addKey('C', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('B', Tags.Items.GEMS_DIAMOND)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "defuser_noelectro", output);

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
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addKey('B', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "laserdesignator_electro", output);

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
				.addKey('C', Tags.Items.INGOTS_GOLD)
				//
				.addKey('B', Tags.Items.GEMS_DIAMOND)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "laserdesignator_noelectro", output);

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
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.addKey('A', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "radargun_electro", output);

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
				.addKey('C', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.addKey('A', Tags.Items.GEMS_DIAMOND)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "radargun_noelectro", output);

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
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "rocketlauncher_electro", output);

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
				.addKey('S', Tags.Items.INGOTS_IRON)
				//
				.addKey('C', Tags.Items.INGOTS_GOLD)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "rocketlauncher_noelectro", output);

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
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "scanner_electro", output);

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
				.addKey('B', Tags.Items.GEMS_DIAMOND)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "scanner_noelectro", output);

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
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('B', ElectrodynamicsItems.ITEM_BATTERY.get())
				//
				.addKey('A', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addConditions(ELECTRO_LOADED)
				//
				.complete(Ballistix.ID, "tracker_electro", output);

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
				.addKey('P', Tags.Items.INGOTS_IRON)
				//
				.addKey('B', Tags.Items.GEMS_DIAMOND)
				//
				.addKey('A', Tags.Items.INGOTS_GOLD)
				//
				.addConditions(ELECTRO_NOT_LOADED)
				//
				.complete(Ballistix.ID, "tracker_noelectro", output);

	}

}
