package ballistix.datagen.client;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import electrodynamics.datagen.client.ElectrodynamicsLangKeyProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.Level;

public class BallistixLangKeyProvider extends ElectrodynamicsLangKeyProvider {

	public BallistixLangKeyProvider(DataGenerator gen, Locale locale) {
		super(gen, locale, References.ID);
	}

	@Override
	protected void addTranslations() {

		switch (locale) {
		case EN_US:
		default:

			add("itemGroup.itemgroupballistix", "Ballistix");

			addBlock(BallistixBlocks.getBlock(SubtypeBlast.obsidian), "Obsidian TNT");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.condensive), "Condensive Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.attractive), "Attractive Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.repulsive), "Repulsive Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.incendiary), "Incendiary Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.breaching), "Breaching Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.shrapnel), "Shrapnel Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.debilitation), "Debilitation Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.chemical), "Chemical Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.thermobaric), "Thermobaric Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.contagious), "Contagious Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.fragmentation), "Fragmentation Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.emp), "EMP Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.nuclear), "Nuclear Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.antimatter), "Antimatter Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.largeantimatter), "Large Antimatter Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.darkmatter), "Darkmatter Explosive");
			addBlock(BallistixBlocks.getBlock(SubtypeBlast.landmine), "Landmine");

			addBlock(BallistixBlocks.blockMissileSilo, "Missile Silo");
			addBlock(BallistixBlocks.blockRadar, "Radar");

			addItem(BallistixItems.getItem(SubtypeGrenade.condensive), "Condensive Grenade");
			addItem(BallistixItems.getItem(SubtypeGrenade.attractive), "Attractive Grenade");
			addItem(BallistixItems.getItem(SubtypeGrenade.repulsive), "Repulsive Grenade");
			addItem(BallistixItems.getItem(SubtypeGrenade.incendiary), "Incendiary Grenade");
			addItem(BallistixItems.getItem(SubtypeGrenade.shrapnel), "Shrapnel Grenade");
			addItem(BallistixItems.getItem(SubtypeGrenade.debilitation), "Debilitation Grenade");
			addItem(BallistixItems.getItem(SubtypeGrenade.chemical), "Chemical Grenade");

			addItem(BallistixItems.getItem(SubtypeMinecart.obsidian), "Minecart with Obsidian TNT");
			addItem(BallistixItems.getItem(SubtypeMinecart.condensive), "Minecart with Condensive Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.attractive), "Minecart with Attractive Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.repulsive), "Minecart with Repulsive Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.incendiary), "Minecart with Incendiary Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.breaching), "Minecart with Breaching Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.shrapnel), "Minecart with Shrapnel Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.debilitation), "Minecart with Debilitation Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.chemical), "Minecart with Chemical Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.thermobaric), "Minecart with Thermobaric Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.contagious), "Minecart with Contagious Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.emp), "Minecart with EMP Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.fragmentation), "Minecart with Fragmentation Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.nuclear), "Minecart with Nuclear Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.antimatter), "Minecart with Antimatter Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.largeantimatter), "Minecart with Large Antimatter Explosive");
			addItem(BallistixItems.getItem(SubtypeMinecart.darkmatter), "Minecart with Darkmatter Explosive");

			addItem(BallistixItems.ITEM_DUSTPOISON, "Poison Dust");

			addItem(BallistixItems.getItem(SubtypeMissile.closerange), "Close-range Missile");
			addItem(BallistixItems.getItem(SubtypeMissile.mediumrange), "Medium-range Missile");
			addItem(BallistixItems.getItem(SubtypeMissile.longrange), "Long-range Missile");

			addItem(BallistixItems.ITEM_ROCKETLAUNCHER, "Rocket Launcher");
			addItem(BallistixItems.ITEM_RADARGUN, "Radar Gun");
			addItem(BallistixItems.ITEM_LASERDESIGNATOR, "Laser Designator");
			addItem(BallistixItems.ITEM_TRACKER, "Tracker");
			addItem(BallistixItems.ITEM_SCANNER, "Scanner");
			addItem(BallistixItems.ITEM_DEFUSER, "Defuser");

			addContainer("missilesilo", "Missile Silo");

			addGuiLabel("missilesilo.missile", "Missile");
			addGuiLabel("missilesilo.explosive", "Explosive");
			addGuiLabel("missilesilo.x", "X-Coord");
			addGuiLabel("missilesilo.y", "Y-Coord");
			addGuiLabel("missilesilo.z", "Z-Coord");
			addGuiLabel("missilesilo.freq", "Freq");

			addDamageSource("chemicalgas", "%s opened the Zyklon-B without reading the instructions!");
			addDamageSource("shrapnel", "%s was hit by shrapnel!");

			addChatMessage("radargun.text", "Coordinates: %s");
			addChatMessage("laserdesignator.launch", "Launching all missiles with frequency %s to targets: ");
			addChatMessage("laserdesignator.launchsend", "-> %s");
			addChatMessage("laserdesignator.setfrequency", "Set frequency to: %s");
			addChatMessage("scanner.cleared", "Cleared trackers!");
			addChatMessage("scanner.none", "There were no trackers!");

			addTooltip("radargun.pos", "Stored: %s");
			addTooltip("radargun.notag", "No Coordinates Stored");
			addTooltip("laserdesignator.frequency", "Frequency: %s");
			addTooltip("laserdesignator.nofrequency", "Unbound");
			addTooltip("laserdesignator.invalidfreq", "Set a frequency for the silo");
			addTooltip("tracker.tracking", "Tracking: %s");
			addTooltip("tracker.none", "NONE");

			addSubtitle("ballistix.antimatterexplosion", "Antimatter bomb explodes");
			addSubtitle("ballistix.darkmatter", "Dark matter bomb ignites and explodes");
			addSubtitle("ballistix.nuclearexplosion", "Nuclear bomb explodes");

			addDimension(Level.OVERWORLD.location().getPath(), "The Overworld");
			addDimension(Level.NETHER.location().getPath(), "The Nether");
			addDimension(Level.END.location().getPath(), "The End");

			addGuidebook(References.ID, "Ballistix");

			addGuidebook("chapter.missilesilo", "Missile Silo");
			addGuidebook("chapter.missilesilo.l1", "The Missile Silo is used, as the name suggests, to launch various missiles with different types of warheads. The Silo has 3 available missile types with the following block ranges:");
			addGuidebook("chapter.missilesilo.range", "%1$s : %2$s");
			addGuidebook("chapter.missilesilo.close", "Close-Range");
			addGuidebook("chapter.missilesilo.medium", "Medium-Range");
			addGuidebook("chapter.missilesilo.long", "Long-Range");
			addGuidebook("chapter.missilesilo.unlimited", "Unlimited");
			addGuidebook("chapter.missilesilo.l2", "Each missile type is capable of carrying any explosive as a warhead.");

			addGuidebook("chapter.missilesilo.l3", "To load an explosive into a missile, first place the missile of your choice into the silo in its designated slot. Next, choose an explosive type and place it into its designated spot in the GUI. Next, program the target destination into the coordiante boxes. This can either be done manually or with a " + "Radar Gun. Finally, once this is all completed, to fire the missile, apply a redstone signal to the silo.");

			addGuidebook("chapter.items", "Items");
			addGuidebook("chapter.items.rocketlauncher1", "The Rocket Launcher is capable of firing Close-Range missiles with any explosive warhead attached as a projectile. To fire it, have a Close-Range missile in your inventory along with the explosive type of your choice. Note, it must be a Ballistix explosive, and it must be the block form. Once this is " + "done, hold the Launcher and Right-Click to fire!");

			addGuidebook("chapter.items.radargun1", "The Radar Gun is used to collect coordiantes of a target and feed them into the Missile Silo instead of programming them manually. To use the Radar Gun, simply Right-Click on the target with the Gun to store its coordinates. This will expend 150 Joules and store the coordinates to the Gun. Then Right-Click " + "on the Missile Silo with the Gun to feed in the coordinates.");

			addGuidebook("chapter.items.laserdesignator1", "The Laser Designator is used to launch missiles from a Missile Silo remotely instead of having to manually apply a redstone signal. To use it, first place a silo and prepare a missile with your choice of warhead. Next, Right-Click the Designator on the Silo to link the two. Note, another the benefit of the " + "Designator is that you will not need to pre-program coordinates to the Missile Silo.");
			addGuidebook("chapter.items.laserdesignator2", "With the Silo prepared and the Designator linked to the Silo, all the remains is to find a suitable target. When you find one, simply Right-Click on it with the Designator. This will expend 150 Joules, automatically feed the target's coordinates into the Silo, and fire the missile. Note, make sure the " + "target is actually in range of the missile in the Silo!");

			addGuidebook("chapter.items.defuser1", "The Defuser is your only hope of stopping an explosive from detonating once it has been activated. Right-Click on the explosive with the Defuser, and it will, as the name suggests, defuse it! This will expend 150 Joules and drop the explosive that was defused on the ground. Note, the Defuser " + "currently does not work on Vanilla TNT.");

			addGuidebook("chapter.items.tracker1", "The Tracker is capable of tracking the location of any entity in the world, player or otherwise. This is especially useful, as it allows you to track other players and send them Thermonuclear War...care packages from afar. To start tracking an entity, simply Right-Click on it with the Tracker. " + "This will expend 150 Joules. The tracker only keeps tabs on the X and Z coordinates of the entity along with what dimension they're in. There is still a little guesswork involved. Note, if the entity dies or the player disconnects from the server, the Tracker will stop tracking them!");

			addGuidebook("chapter.items.scanner1", "The Scanner is every paranoid player's best friend. Think someone has tagged you with a Tracker and is trying to nuke your prestigious dirt house? Fear not; Simply right-click with the Scanner. This will expend 150 Joules and jam any tracking signals if they are present!");

			addJei("info.item.missilecloserange", "Specs:\n    Range: 3000 Blocks");
			addJei("info.item.missilemediumrange", "Specs:\n    Range: 10 000 Blocks");
			addJei("info.item.missilelongrange", "Specs:\n    Range: Unlimited");

		}

	}

}
