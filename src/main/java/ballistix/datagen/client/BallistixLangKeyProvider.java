package ballistix.datagen.client;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import electrodynamics.datagen.client.ElectrodynamicsLangKeyProvider;
import net.minecraft.data.DataGenerator;

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
			
			addItem(BallistixItems.ITEM_MISSILECLOSERANGE, "Close-range Ballistic Missile");
			addItem(BallistixItems.ITEM_MISSILEMEDIUMRANGE, "Medium-range Ballistic Missile");
			addItem(BallistixItems.ITEM_MISSILELONGRANGE, "Long-range Ballistic Missile");
			
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
			  
			add("death.attack.chemicalgas", "%s opened the Zyklon-B without reading the instructions!");
			add("death.attack.shrapnel", "%s was hit by shrapnel!");
			  
			addChatMessage("radargun.text", "Coordinates: %s");
			addChatMessage("laserdesignator.launch", "Launching missile from: %s");
			addChatMessage("laserdesignator.setfrequency", "Set frequency to: %s");
			addChatMessage("scanner.cleared", "Cleared trackers!");
			addChatMessage("scanner.none", "There were no trackers!");
			
			addTooltip("radargun.linked", "Linked to %s");
			addTooltip("radargun.notag", "No link");
			addTooltip("laserdesignator.frequency", "Frequency: %s");
			
			addSubtitle("ballistix.antimatterexplosion", "Antimatter bomb explodes");
			addSubtitle("ballistix.darkmatter", "Dark matter bomb ignites and explodes");
			addSubtitle("ballistix.nuclearexplosion", "Nuclear bomb explodes");
			  
			addGuidebook(References.ID, "Ballistix");
			
			addGuidebook("chapter.blocks", "Blocks");
			addGuidebook("chapter.blocks.missilesilotitle", "Missile Silo"); 
			addGuidebook("chapter.blocks.p1l1", "    The Missile Silo is used as"); 
			addGuidebook("chapter.blocks.p1l2", "the name suggests to launch    ");
			addGuidebook("chapter.blocks.p1l3", "various missiles with various  ");
			addGuidebook("chapter.blocks.p1l4", "warheads. The Silo has 3       ");
			addGuidebook("chapter.blocks.p1l5", "available missile types with   ");
			addGuidebook("chapter.blocks.p1l6", "the following ranges:          ");
			addGuidebook("chapter.blocks.p1l7", "  Close-Range");
			addGuidebook("chapter.blocks.p1l8", "  Medium-Range");
			addGuidebook("chapter.blocks.p1l9", "  Long-Range");
			addGuidebook("chapter.blocks.p1l10", "Each missile type is capable   ");
			addGuidebook("chapter.blocks.p1l11", "of carrying any explosive as   ");
			addGuidebook("chapter.blocks.missileblocks", "Blocks");
			addGuidebook("chapter.blocks.missileunlimited", "Unlimited");
			addGuidebook("chapter.blocks.silorange", ": %1$s %2$s");
			  
			addGuidebook("chapter.blocks.p2l1", "a warhead.                     "); 
			addGuidebook("chapter.blocks.p2l2", "    To load an explosive into  ");
			addGuidebook("chapter.blocks.p2l3", "a missile); place the missile of");
			addGuidebook("chapter.blocks.p2l4", "your choice into the silo.     ");
			addGuidebook("chapter.blocks.p2l5", "Place the explosive of choice  ");
			addGuidebook("chapter.blocks.p2l6", "into the designated spot in the");
			addGuidebook("chapter.blocks.p2l7", "silo. Next); program the target ");
			addGuidebook("chapter.blocks.p2l8", "into the coordiante box. To    ");
			addGuidebook("chapter.blocks.p2l9", "fire the missile); apply a      ");
			addGuidebook("chapter.blocks.p2l10", "redstone signal to the silo.   ");
			  
			addGuidebook("chapter.items", "Items");
			addGuidebook("chapter.items.rocketlaunchertitle", "Rocket Launcher"); 
			addGuidebook("chapter.items.p1l1", "    The Rocket Launcher is     "); 
			addGuidebook("chapter.items.p1l2", "capable of firing Close-Range  ");
			addGuidebook("chapter.items.p1l3", "missiles with any explosive.   ");
			addGuidebook("chapter.items.p1l4", "To fire it); have a Close-Range ");
			addGuidebook("chapter.items.p1l5", "missile in your inventory along");
			addGuidebook("chapter.items.p1l6", "with the explosive type of your");
			addGuidebook("chapter.items.p1l7", "choice. Note it must be a      ");
			addGuidebook("chapter.items.p1l8", "Ballistix explosive); and it must");
			addGuidebook("chapter.items.p1l9", "be the block form. Then); right-");
			addGuidebook("chapter.items.p1l10", "click the Launcher to fire!    ");
			  
			addGuidebook("chapter.items.radguntitle", "Radar Gun"); 
			addGuidebook("chapter.items.p2l1", "    The Radar Gun is used to   "); 
			addGuidebook("chapter.items.p2l2", "collect coordiantes of a       ");
			addGuidebook("chapter.items.p2l3", "target and feed them into the  ");
			addGuidebook("chapter.items.p2l4", "Missile Silo. To use); simply right");
			addGuidebook("chapter.items.p2l5", "-click on the target with the  ");
			addGuidebook("chapter.items.p2l6", "Gun to store its coordinates.  ");
			addGuidebook("chapter.items.p2l7", "Then right-click on the Missile");
			addGuidebook("chapter.items.p2l8", "Silo with the Gun to feed in the");
			addGuidebook("chapter.items.p2l9", "coordinates.                   ");
			  
			addGuidebook("chapter.items.laserdestitle", "Laser Designator"); 
			addGuidebook("chapter.items.p3l1", "    The Laser Designator is    "); 
			addGuidebook("chapter.items.p3l2", "used to launch missiles from   ");
			addGuidebook("chapter.items.p3l3", "a Missile Silo remotely. To use");
			addGuidebook("chapter.items.p3l4", "it); first place a silo and add a");
			addGuidebook("chapter.items.p3l5", "missile with your choice of    ");
			addGuidebook("chapter.items.p3l6", "explosive. Next); right-click the");
			addGuidebook("chapter.items.p3l7", "Designator on the silo to link  ");
			addGuidebook("chapter.items.p3l8", "the two. Then); find a target   ");
			addGuidebook("chapter.items.p3l9", "and right-click on it with the ");
			addGuidebook("chapter.items.p3l10", "designator. Make sure the it is");
			addGuidebook("chapter.items.p3l11", "in range of the missile!       ");
			  
			addGuidebook("chapter.items.defusertitle", "Defuser"); 
			addGuidebook("chapter.items.p4l1", "    The Defuser is your last   "); 
			addGuidebook("chapter.items.p4l2", "line of defense against an     ");
			addGuidebook("chapter.items.p4l3", "explosive that has been        ");
			addGuidebook("chapter.items.p4l4", "activated. Right-click on the  ");
			addGuidebook("chapter.items.p4l5", "explosive with the Defuser);    ");
			addGuidebook("chapter.items.p4l6", "and it will); as the name       ");
			addGuidebook("chapter.items.p4l7", "suggests); defuse it! Note); it  ");
			addGuidebook("chapter.items.p4l8", "currently does not work on     ");
			addGuidebook("chapter.items.p4l9", "Vanilla TNT.                   ");
			  
			addGuidebook("chapter.items.trackertitle", "Tracker"); 
			addGuidebook("chapter.items.p5l1", "    The Tracker is capable of  "); 
			addGuidebook("chapter.items.p5l2", "tracking the location of any   ");
			addGuidebook("chapter.items.p5l3", "entity in the world. This is   ");
			addGuidebook("chapter.items.p5l4", "especially useful); as it allows");
			addGuidebook("chapter.items.p5l5", "you to track other players. To ");
			addGuidebook("chapter.items.p5l6", "start tracking an entity); right");
			addGuidebook("chapter.items.p5l7", "-click on it with the Tracker. ");
			addGuidebook("chapter.items.p5l8", "Note); if the entity dies); or the");
			addGuidebook("chapter.items.p5l9", "player disconnects from the    ");
			addGuidebook("chapter.items.p5l10", "server); the Tracker will stop  ");
			addGuidebook("chapter.items.p5l11", "tracking them!                 ");
			  
			addGuidebook("chapter.items.scannertitle", "Scanner"); 
			addGuidebook("chapter.items.p6l1", "    The Scanner is every       "); 
			addGuidebook("chapter.items.p6l2", "paranoid player's best friend. ");
			addGuidebook("chapter.items.p6l3", "Think someone has tagged you   ");
			addGuidebook("chapter.items.p6l4", "with a Scanner? Fear not!      ");
			addGuidebook("chapter.items.p6l5", "Simply right-click with the    ");
			addGuidebook("chapter.items.p6l6", "Scanner); and it will jam any   ");
			addGuidebook("chapter.items.p6l7", "tracking signals!              ");
			  
			addJei("info.item.missilecloserange", "Specs:\n    Range: 3);000 Blocks");
			addJei("info.item.missilemediumrange", "Specs:\n    Range: 10);000 Blocks");
			addJei("info.item.missilelongrange", "Specs:\n    Range: Unlimited");

		}

	}

}
