package ballistix.datagen.client;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.Level;
import voltaic.datagen.utils.client.BaseLangKeyProvider;

public class BallistixLangKeyProvider extends BaseLangKeyProvider {

    public BallistixLangKeyProvider(PackOutput output, Locale locale) {
	super(output, locale, Ballistix.ID);
    }

    @Override
    protected void addTranslations() {

	switch (locale) {
	case EN_US:
	default:

	    addCreativeTab("main", "Ballistix");

	    // Tier 0
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.obsidian), "Obsidian TNT");
	    // Tier 1
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.condensive), "Condensive Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.incendiary), "Incendiary Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.chemical), "Chemical Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.anvil), "Anvil Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.attractive), "Attractive Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.repulsive), "Repulsive Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel), "Shrapnel Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.infestive), "Infestive Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.debilitation), "Debilitation Explosive");
	    // Tier 2
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation), "Fragmentation Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.contagious), "Contagious Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.sonic), "Sonic Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.breaching), "Breaching Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric), "Thermobaric Explosive");
	    // Tier 3
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.antigravity), "Anti-Gravity Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.emp), "EMP Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.endothermic), "Endothermic Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.exothermic), "Exothermic Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.ender), "Ender Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.hypersonic), "Hypersonic Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.rejuvination), "Rejuvenation Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.nuclear), "Nuclear Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.antimatter), "Antimatter Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter),
		    "Large Antimatter Explosive");
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter), "Dark Matter Explosive (WIP)");
	    // Other
	    addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.landmine), "Landmine");

	    addBlock(
		    BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1),
		    "Control Panel T1");
	    addBlock(
		    BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2),
		    "Control Panel T2");
	    addBlock(
		    BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3),
		    "Control Panel T3");
	    addBlock(
		    BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1),
		    "Support Frame T1");
	    addBlock(
		    BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2),
		    "Support Frame T2");
	    addBlock(
		    BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3),
		    "Support Frame T3");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1),
		    "Launch Platform T1");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2),
		    "Launch Platform T2");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3),
		    "Launch Platform T3");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls),
		    "Vertical Launch Silo");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar), "Search Radar");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar),
		    "Fire Control Radar");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret), "SAM Turret");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), "ESM Tower");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret),
		    "CIWS Turret");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret),
		    "Laser Turret");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret),
		    "Railgun Turret");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.airraidsiren),
		    "Air Raid Siren");
	    addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.proximitydetector),
		    "Proximity Detector");

	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.condensive), "Condensive Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.attractive), "Attractive Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.repulsive), "Repulsive Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.incendiary), "Incendiary Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.shrapnel), "Shrapnel Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.debilitation), "Debilitation Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.chemical), "Chemical Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.anvil), "Anvil Grenade");
	    addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.infestive), "Infestive Grenade");

	    // Tier 0
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.obsidian), "Minecart with Obsidian TNT");
	    // Tier 1
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.condensive),
		    "Minecart with Condensive Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.anvil), "Minecart with Anvil Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.attractive),
		    "Minecart with Attractive Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.repulsive),
		    "Minecart with Repulsive Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.incendiary),
		    "Minecart with Incendiary Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.shrapnel),
		    "Minecart with Shrapnel Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.chemical),
		    "Minecart with Chemical Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.infestive),
		    "Minecart with Infestive Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.debilitation),
		    "Minecart with Debilitation Explosive");
	    // Tier 2
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.breaching),
		    "Minecart with Breaching Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.thermobaric),
		    "Minecart with Thermobaric Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.contagious),
		    "Minecart with Contagious Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.fragmentation),
		    "Minecart with Fragmentation Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.sonic), "Minecart with Sonic Explosive");
	    // Tier 3
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.antigravity),
		    "Minecart with Anti-Gravity Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.emp), "Minecart with EMP Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.endothermic),
		    "Minecart with Endothermic Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.exothermic),
		    "Minecart with Exothermic Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.ender), "Minecart with Ender Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.hypersonic),
		    "Minecart with Hypersonic Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.rejuvination),
		    "Minecart with Rejuvenation Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.nuclear), "Minecart with Nuclear Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.antimatter),
		    "Minecart with Antimatter Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.largeantimatter),
		    "Minecart with Large Antimatter Explosive");
	    addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.darkmatter),
		    "Minecart with Darkmatter Explosive");

	    addItem(BallistixItems.ITEM_DUSTPOISON, "Poison Dust");
	    // addItem(BallistixItems.ITEM_RANGEUPGRADE, "Range Upgrade");

	    addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1), "Tier 1 Missile");
	    addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2), "Tier 2 Missile");
	    addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3), "Tier 3 Missile");
	    addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.cluster), "Cluster Missile");
	    addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.clustershard), "Cluster Missile Warhead");

	    addItem(BallistixItems.ITEM_AAMISSILE, "Surface-to-Air Missile");
	    addItem(BallistixItems.ITEM_AAMISSILEMK2, "Anti-Ballistic Missile");
	    addItem(BallistixItems.ITEM_BULLET, "20mm Bullet");

	    addItem(BallistixItems.ITEM_ROCKETLAUNCHER, "Rocket Launcher");
	    addItem(BallistixItems.ITEM_RADARGUN, "Radar Gun");
	    addItem(BallistixItems.ITEM_LASERDESIGNATOR, "Laser Designator");
	    addItem(BallistixItems.ITEM_TRACKER, "Tracker");
	    addItem(BallistixItems.ITEM_SCANNER, "Jammer");
	    addItem(BallistixItems.ITEM_DEFUSER, "Defuser");

	    addContainer("launchercontrolpaneltier1", "Control Panel T1");
	    addContainer("launchercontrolpaneltier2", "Control Panel T2");
	    addContainer("launchercontrolpaneltier3", "Control Panel T3");
	    addContainer("launcherplatformtier1", "Launch Platform T1");
	    addContainer("launcherplatformtier2", "Launch Platform T2");
	    addContainer("launcherplatformtier3", "Launch Platform T3");
	    addContainer("vls", "Vertical Launch Silo");
	    addContainer("samturret", "SAM Turret");
	    addContainer("searchradar", "Search Radar");
	    addContainer("firecontrolradar", "Fire Control Radar");
	    addContainer("esmtower", "ESM Tower");
	    addContainer("ciwsturret", "CIWS Turret");
	    addContainer("laserturret", "Laser Turret");
	    addContainer("railgunturret", "Railgun Turret");
	    addContainer("proximitydetector", "Proximity Detector");
	    addContainer("airraidsiren", "Air Raid Siren");

	    addGuiLabel("missilesilo.missile", "Missile");
	    addGuiLabel("missilesilo.explosive", "Explosive");
	    addGuiLabel("missilesilo.x", "X");
	    addGuiLabel("missilesilo.y", "Y");
	    addGuiLabel("missilesilo.z", "Z");
	    addGuiLabel("missilesilo.freq", "" + '\u039b');
	    addGuiLabel("missilesilo.sync", "Sync");

	    addDamageSource("chemicalgas", "%s opened the Zyklon-B without reading the instructions!");
	    addDamageSource("shrapnel", "%s was hit by shrapnel!");
	    addDamageSource("laserturret", "%s found the green laser pointer!");
	    addDamageSource("ciwsbullet", "%s saw the WIZ!");
	    addDamageSource("railgunround", "%s found that E really does equal MC^2!");
	    addDamageSource("virus", "%s got more than the sniffles!");

	    add("effect.ballistix.virus", "Virus");
	    add("effect.ballistix.toxin", "Toxin");
	    add("effect.ballistix.frostbite", "Frostbite");

	    addChatMessage("radargun.text", "Coordinates: %s");
	    addChatMessage("radargun.inserted", "Inserted Coordinates Successfully: %s");
	    addChatMessage("radargun.turretsucess", "Bound");
	    addChatMessage("radargun.turrettoofar", "Turret too far away!");
	    addChatMessage("laserdesignator.launch", "Launching all missiles with frequency %s to targets: ");
	    addChatMessage("laserdesignator.launchsend", "-> %s");
	    addChatMessage("laserdesignator.setfrequency", "Set frequency to: %s");
	    addChatMessage("scanner.cleared", "Cleared all trackers targeting %s.");

	    addTooltip("radargun.pos", "Stored: %s");
	    addTooltip("radargun.notag", "No Coordinates Stored");
	    addTooltip("laserdesignator.frequency", "Frequency: %s");
	    addTooltip("laserdesignator.nofrequency", "Unbound");
	    addTooltip("laserdesignator.invalidfreq", "Set a frequency for the silo");
	    addTooltip("laserdesignator.signalrange", "Signal Range: %s Blocks");
	    addTooltip("tracker.tracking", "Tracking: %s");
	    addTooltip("tracker.none", "NONE");
	    addTooltip("missile.range", "Range: %s Blocks");
	    addTooltip("missile.unlimited", "Unlimited");
	    addTooltip("missile.maxbombtier", "Max Tier: %s");
	    addTooltip("missilesilo.charge", "Charge: %1$s / %2$s");
	    addTooltip("missilesilo.maxtier", "Max Tier: %s");
	    addTooltip("missilesilo.accuracy", "Inaccuracy: %s");

	    addTooltip("turret.blockrange", "Block Range");
	    addTooltip("turret.entityrange", "Entity Range");
	    addTooltip("turret.maxrange", "Max: %s");
	    addTooltip("turret.minrange", "Min: %s");
	    addTooltip("turret.targetmode", "Target Mode");
	    addTooltip("turret.targetmodeplayers", "Only Players");
	    addTooltip("turret.targetmodeliving", "All Living");
	    addTooltip("turret.targetmodenone", "None");

	    addTooltip("radar.frequencymanager", "Frequency Manager");
	    addTooltip("radar.frequencymanager.delete", "Delete");

	    addTooltip("radar.redstone", "Redstone");
	    addTooltip("radar.redstone.enabled", "Enabled");
	    addTooltip("radar.redstone.disabled", "Disabled");

	    addTooltip("turret.whitelistmanager", "Whitelist Manager");

	    addTooltip("aamissile.hitrate", "Hit rate: %s");
	    addTooltip("explosive.tier", "Tier: %s");
	    addTooltip("silo.launch", "LAUNCH");

	    addTooltip("airraidsiren.toggle", "Redstone-activated");

	    addGuiLabel("turret.radar", "Radar: ");
	    addGuiLabel("turret.radarnone", "None");
	    addGuiLabel("turret.status", "Status: %s");
	    addGuiLabel("turret.statusunlinked", "Unlinked");
	    addGuiLabel("turret.statusnopower", "No Power");
	    addGuiLabel("turret.statuscooldown", "Cooldown %s");
	    addGuiLabel("turret.statusnotarget", "No Target");
	    addGuiLabel("turret.statusoutofrange", "Out of Range");
	    addGuiLabel("turret.statusnoammo", "No Ammo");
	    addGuiLabel("turret.statusoverheated", "Overheated");
	    addGuiLabel("turret.statusgood", "Good");
	    addGuiLabel("turret.temperature", "Temperature: %s");

	    addGuiLabel("turret.playerwhitelist.add", "Add");
	    addGuiLabel("turret.playerwhitelist.newplayer", "New Player");

	    addGuiLabel("radar.tracking", "Tracking: ");
	    addGuiLabel("radar.frequencywhitelist.mode", "Whitelist Mode");
	    addGuiLabel("radar.frequencywhitelist.enabled", "Enabled");
	    addGuiLabel("radar.frequencywhitelist.disabled", "Disabled");
	    addGuiLabel("radar.frequencywhitelist.list", "Whitelisted");
	    addGuiLabel("radar.frequencywhitelist.add", "Add");
	    addGuiLabel("radar.bearingunknown", "Bearing Unknown");
	    addGuiLabel("radar.bearing", "Bearing: %1$s" + '\u00B0' + " - %2$s" + '\u00B0');

	    addGuiLabel("esmtower.nosearchradars", "No Search Radars Detected");
	    addGuiLabel("esmtower.searchradardetected", "Search Radar Detected");
	    addGuiLabel("esmtower.detectedfirecontrolradars", "Fire Control Radars");

	    addGuiLabel("proximitydetector.detectionrange", "Detection Range");
	    addGuiLabel("proximitydetector.xcoord", "X-Coord:");
	    addGuiLabel("proximitydetector.ycoord", "Y-Coord:");
	    addGuiLabel("proximitydetector.zcoord", "Z-Coord:");
	    addGuiLabel("proximitydetector.min", "Min");
	    addGuiLabel("proximitydetector.max", "Max");
	    addGuiLabel("proximitydetector.detectionfield", "Detection Field");
	    addGuiLabel("proximitydetector.showfield", "Show");
	    addGuiLabel("proximitydetector.hidefield", "Hide");

	    addGuiLabel("airraidsiren.volume", "Volume");
	    addGuiLabel("airraidsiren.pitch", "Pitch");
	    addGuiLabel("airraidsiren.radius", "Radius");

	    addSubtitle(BallistixSounds.SOUND_ANTIMATTEREXPLOSION, "Antimatter bomb detonates");
	    addSubtitle(BallistixSounds.SOUND_LARGE_ANTIMATTEREXPLOSION, "Large Antimatter bomb detonates");
	    addSubtitle(BallistixSounds.SOUND_DARKMATTER, "Dark matter bomb detonates");
	    addSubtitle(BallistixSounds.SOUND_NUCLEAREXPLOSION, "Nuclear bomb detonates");
	    addSubtitle(BallistixSounds.SOUND_EMPEXPLOSION, "EMP detonates");
	    addSubtitle(BallistixSounds.SOUND_SONICEXPLOSION, "Sonic bomb detonates");
	    addSubtitle(BallistixSounds.SOUND_HYPERSONICSONICEXPLOSION, "Hypersonic bomb detonates");
	    addSubtitle(BallistixSounds.SOUND_ENDOTHERMICBEAM, "Endothermic bomb detonates");
	    addSubtitle(BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER, "Missile is fired from rocket lancher");
	    addSubtitle(BallistixSounds.SOUND_MISSILE_SILO, "Missile launches from silo");
	    addSubtitle(BallistixSounds.SOUND_RADAR, "Radar pulses");
	    addSubtitle(BallistixSounds.SOUND_FIRECONTROLRADAR, "Fire Control Radar tracks");
	    addSubtitle(BallistixSounds.SOUND_CIWS_TURRETFIRING, "CIWS Turret Fires");
	    addSubtitle(BallistixSounds.SOUND_LASER_TURRETFIRING, "Laser Turret fires");
	    addSubtitle(BallistixSounds.SOUND_RODHITTINGGROUND, "Rod Impacts Ground");
	    addSubtitle(BallistixSounds.SOUND_RAILGUNKINETIC, "Railgun Turret Fires");
	    addSubtitle(BallistixSounds.SOUND_VLSLAUNCH, "VLS Fires");
	    addSubtitle(BallistixSounds.SOUND_AIRRAIDSIREN, "Air Raid Siren sounds");

	    addDimension(Level.OVERWORLD.location().getPath(), "The Overworld");
	    addDimension(Level.NETHER.location().getPath(), "The Nether");
	    addDimension(Level.END.location().getPath(), "The End");

	    addGuidebook(Ballistix.ID, "Ballistix");

	    addGuidebook("chapter.explosives", "Explosives");

	    addGuidebook("chapter.explosives.l1",
		    """
		    	Ballistix offers a wide variety of explosives to choose from. Each explosive will have its own specialty, with some focusing on maximum destruction while others focus on biological and chemical warfare. Before we continue, it is important to note that some explosives will have both a "radius" and an "energy". The radius of \
		    	an explosive determines the overall area it can effect. The energy on the other hand determines what amount of blocks in that area will be destroyed starting at the center of the blast. The harder a block, the more energy the blast will lose destroying it, reducing the effective area of the explosive. For example, a blast with a small radius and a high energy will destroy a \
		    	smaller area, but will be able to more effectively destroy the blocks inside of its radius. Keep this in mind when selecting explosives for a task! It should be noted here that most explosives can have their parameters tweaked in the config file.""");
	    addGuidebook("chapter.explosives.tier", "Tier: %s");
	    addGuidebook("chapter.explosives.radius", "Radius: %s blocks");
	    addGuidebook("chapter.explosives.energy", "Energy: %s");
	    addGuidebook("chapter.explosives.fuse", "Fuse: %s ticks");
	    addGuidebook("chapter.explosives.duration", "Duration: %s ticks");

	    addGuidebook("chapter.explosives.obsidian1", "An enhanced form of TNT.");
	    addGuidebook("chapter.explosives.condensive1", "A weaker but fast-detonating TNT.");
	    addGuidebook("chapter.explosives.attractive1",
		    "Does a small amount of terrain damage and pulls entities towards its center and hurts them.");
	    addGuidebook("chapter.explosives.repulsive1",
		    "Does a small amount of terrain damage pushes entities away from its center and hurts them.");
	    addGuidebook("chapter.explosives.incendiary1", "Sets all blocks inside its radius on fire.");
	    addGuidebook("chapter.explosives.shrapnel1",
		    "Shoots %s shrapnel pieces in a spherical pattern that hurt entities on contact.");
	    addGuidebook("chapter.explosives.chemical1",
		    "Emits a cloud of gas that gives entities inside of it Nausea, Mining Fatigue, Slowness, and Toxin effects for %s ticks. Entities standing within the gas cloud will also be hurt while inside.");
	    addGuidebook("chapter.explosives.anvil1",
		    "Shoots %s anvils in a spherical pattern that hurt entities on contact.");
	    addGuidebook("chapter.explosives.infestive1",
		    "Does no terrain damage, but infests blocks within its radius with Silverfish.");
	    addGuidebook("chapter.explosives.debilitation1",
		    "Emits a cloud of gas that gives entities inside of it the Nausea effect for %1$s ticks and Mining Fatigue and Slowness effects for %2$s ticks.");

	    addGuidebook("chapter.explosives.fragmentation1",
		    "Shoots %s shrapnel pieces in a spherical pattern that explode and hurt entities on contact.");
	    addGuidebook("chapter.explosives.contagious1",
		    "Emits a cloud of gas that gives entities inside of it Blindness, Weakness, and Hunger effects for %s ticks. Entities standing within the gas cloud will also be hurt while inside. Entities will also be infected with a deadly virus that deals damage over time, will infect other nearby entities, and can only be removed with "
			    + "curative items!");
	    addGuidebook("chapter.explosives.breaching1",
		    "A specialized small explosive for destroying hardened materials.");
	    addGuidebook("chapter.explosives.thermobaric1", "A very large and powerful conventional explosive.");
	    addGuidebook("chapter.explosives.sonic1",
		    "Emits a powerful sonic wave that sends weaker blocks flying and harms enemies within its radius. It does not actually destroy blocks though.");

	    addGuidebook("chapter.explosives.antigravity1",
		    "Inverts gravity in a %1$s by %1$s chunk radius for %2$s ticks. This impacts all entities and missiles immediately, and will start to impact blocks over time.");
	    addGuidebook("chapter.explosives.emp1",
		    "Drains all machines and items of energy that are within its radius.");
	    addGuidebook("chapter.explosives.nuclear1", "Nuke go boom. Radiation go oww.");
	    addGuidebook("chapter.explosives.endothermic1",
		    "Freezes blocks, liquids, and entities within its radius, then scatters them like a Sonic Explosive would.");
	    addGuidebook("chapter.explosives.exothermic1",
		    "Destroys, burns, and melts blocks within its radius. Also sets all entities within its radius on fire.");
	    addGuidebook("chapter.explosives.ender1", "Teleports all entities caught within its radius to the End.");
	    addGuidebook("chapter.explosives.hypersonic1", "A more powerful version of the Sonic Explosive.");
	    addGuidebook("chapter.explosives.rejuvination1", "Resets the chunk it is detonated in.");
	    addGuidebook("chapter.explosives.antimatter1",
		    "A very powerful explosive that uses Antimatter to generate the explosion energy.");
	    addGuidebook("chapter.explosives.largeantimatter1", "A more powerful version of the Antimatter Explosive.");
	    addGuidebook("chapter.explosives.darkmatter1",
		    "Creates a singularity that sucks in blocks and entities. After reaching its full size, the singularity will begin to move freely!");

	    addGuidebook("chapter.missileoffense", "Missile Offense");
	    addGuidebook("chapter.missileoffense.l1.1",
		    "Ballistix offers two main methods of missile offense. The first of these is the Missile Silo. The Missile Silo is a multiblock used to launch various missiles with different types of warheads. The Silo is composed of three different blocks:");
	    addGuidebook("chapter.missileoffense.controlpanel", "Control Panel");
	    addGuidebook("chapter.missileoffense.launchplatform", "Launch Platform");
	    addGuidebook("chapter.missileoffense.supportframe", "Support Frame");
	    addGuidebook("chapter.missileoffense.l1.2",
		    "Each come in three tiers which provide successive buffs to the silo and any tier can be combined with the other tier, with a Tier 3 set being the strongest missile silo possible!");
	    addGuidebook("chapter.missileoffense.l2",
		    "The %1$s is used to provide targeting information to the Missile Silo and instructs it to fire. It will require power to function however. The %1$s can be instructed to launch either manually via the GUI, a redstone signal, or by using a Laser Designator. The silo will be on cooldown upon successfully firing a missile. "
			    + "However it will fire again once the cooldown is over if it still has a redstone signal.");
	    addGuidebook("chapter.missileoffense.l3",
		    "The %1$s is the cheapest, but can only specify the X and Z coordinates of a target. The %2$s is more expensive, but gains the ability to specify the Y variant. Also, the missile will detonate once it reaches the specified Y value if launched from a Tier 2 or higher, allowing for more precise strikes. The %3$s is the most "
			    + "expensive, but gains the ability to also specify the launch frequency of the silo and the missile.");
	    addGuidebook("chapter.missileoffense.l4",
		    "The %1$s is what holds the missile and accompanying warhead. It should be noted that a missile must have a warhead to be able to launch. The %2$s has a maximum range of %3$s blocks, and can support up to Tier 1 missiles and explosives. The %4$s has a maximum range of %5$s blocks, and can support up to Tier 2 missiles and "
			    + "explosives. The %6$s has a maximum range of %7$s blocks, and can support up to Tier 3 missiles and explosives.");
	    addGuidebook("chapter.missileoffense.l5",
		    "On the subject of tiers, missiles can support explosives of tiers less than or equal to their tier. This means a Tier 2 missile can support a Tier 1 or Tier 2 explosive warhead, but not a Tier 3 warhead. It is important to keep this in mind when attempting to launch an attack!");
	    addGuidebook("chapter.missileoffense.l6",
		    "The %1$s is used to increase the accuracy of the missile silo when launching a missile. A Missile Silo is more than capable of launching a missile without a support frame. However this will incur an inaccuracy of %2$s blocks. This means the missile will land within a square radius of %3$s blocks of the specified target. The %4$s "
			    + "will reduce this inaccuracy to %5$s blocks. The %6$s will reduce the inaccuracy further to %7$s blocks, while the %8$s will reduce inaccuracy all the way to %9$s blocks!");
	    addGuidebook("chapter.missileoffense.l7",
		    "To assembly the Missile Silo, first place the %1$s. Then place the %2$s next to the %1$s facing the center of the block. Finally place the %3$s opposite the %2$s and facing the center of the %1$s. The %2$s will require power to function. If Electrodynamics is installed, it will require Joules at the appropriate Voltage. "
			    + "Otherwise the block can use FE from any mod. Items can be fed in from any side of the %1$s. Missiles launched from a Missile Silo will fly up to 500 blocks, travel horizontally till they near the target, and then arc back down.");
	    addGuidebook("chapter.missileoffense.l8",
		    "The second method of missile offense Ballistix offers is the %1$s. The VLS is a compact version of the Tier 1 Silo with the missile targeting features of a %2$s. While its range is limited to only %3$s blocks, it allows you to set up mobile attach sites with ease. These can then be used in combination with your main Missile "
			    + "Silos, allowing you attach the enemy from multiple directions. Furthermore, since the VLS can only fire Tier 1 missiles, it can be used to wear down enemy defenses while your more valuable missiles are still on the way!");
	    addGuidebook("chapter.missileoffense.l9",
		    "The VLS requires power to function. If Electrodynamics is installed, it will require Joules at the appropriate Voltage. Otherwise it will use FE from any mod. Power and items can be fed in from any side. Missiles fired from the VLS will launch vertically, arc towards their target, and then travel in a straight line.");

	    addGuidebook("chapter.items", "Items");
	    addGuidebook("chapter.items.rocketlauncher1",
		    "The Rocket Launcher is capable of firing Tier 1 missiles with any Tier 1explosive warhead. To fire it, have a Tier 1 missile in your inventory along with the explosive type of your choice. Once this is done, hold the Launcher in your hand and Right-Click to fire!");

	    addGuidebook("chapter.items.radargun1",
		    "The Radar Gun is used to collect coordinates of a target and feed them into the Missile Silo instead of programming them manually. To use the Radar Gun, simply Right-Click on the target with the Gun to store its coordinates. This will expend %s and store the coordinates to the Gun. Then Right-Click on the Silo's Control Panel "
			    + "with the Gun to feed in the coordinates. Alternatively, the coordinates can be imported using the \"Sync\" slot in the GUI. The Radar Gun is also compatible with the VLS!");

	    addGuidebook("chapter.items.laserdesignator1",
		    "The Laser Designator is used to launch missiles from a Missile Silo or VLS remotely up to %1$s blocks away from the silo. To use it, first place a Missile Silo and load it. Next, Right-Click the Designator on the Silo to link the Designator to the Silo's frequency. Alternatively, the frequency can be linked via the "
			    + "\"Sync\" slot.");
	    addGuidebook("chapter.items.laserdesignator2",
		    "With the Silo prepared and the Designator linked to the Silo, all the remains is to find a suitable target. When you find one, simply Right-Click on it with the Designator. This will expend %1$s, automatically feed the target's coordinates into the Silo, and fire the missile. Note, make sure the target is actually "
			    + "in range of the missile in the Silo! The coordinates entered by the Designator will also persist after firing.");

	    addGuidebook("chapter.items.defuser1",
		    "The Defuser is your only hope of stopping an explosive from detonating once it has been activated. To use it, Right-Click on the explosive with the Defuser, and it will, as the name suggests, defuse it! This will expend %s and drop the explosive that was defused on the ground. Note, the Defuser also works on Vanilla TNT.");

	    addGuidebook("chapter.items.tracker1",
		    "The Tracker is capable of tracking the location of any entity in the world, player or otherwise. This is especially useful, as it allows you to track other players and send them Thermonuclear War...care packages from afar. To start tracking an entity, simply Right-Click on it with the Tracker. This will expend %s. Note the "
			    + "Tracker only keeps tabs on the X and Z coordinates of the entity along with what dimension they're in, meaning there is still a little guesswork involved. If the entity dies or the player disconnects from the server, the Tracker will stop tracking them!");
	    addGuidebook("chapter.items.tracker2",
		    "Another fun trick you can do with the Tracker is to use it to launch missiles at the entity it is tracking. Simply right-click the tracker on a Control Panel or VLS as you would with the Radar Gun, and it will input the X and Z coordinates to their respective targeting fields. Note, the Y value will always be input as 0 since "
			    + "the Tracker does not store that, and this will not work if nothing is currently being tracked!");

	    addGuidebook("chapter.items.scanner1",
		    "The Jammer is every paranoid player's best friend. Think someone has tagged you with a Tracker and is trying to nuke your prestigious dirt house? Fear not; Simply right-click with the Jammer. This will expend %s and jam any tracking signals if they are present!");

	    addGuidebook("chapter.missiledefense", "Missile Defense");

	    addGuidebook("chapter.missiledefense.l1",
		    "While the best defense is not to be found, what do you do if your base gets discovered, and now someone has launched a nuclear missile at it? The answer is quite simple: shoot the missile down. Ballistix offers a wide selections of methods to accomplish this task. The following pages will cover these various methods, their "
			    + "strengths, and their weaknesses.");
	    addGuidebook("chapter.missiledefense.l2",
		    "The cornerstone of all Ballistix missile defenses is the %1$s. The %1$s tracks incoming missiles and provides the targeting information to turrets that are linked to it. The Radar has a range of %2$s blocks and can only track one missile at a time. It will track the missile closest to it, and all turrets bound to the radar "
			    + "will target the same missile. The Radar is powered from the bottom. If Electrodynamics is installed, it will require Joules at the appropriate Voltage. Otherwise it will use FE. The radar can be toggled with redstone if enabled, and will read a comparitor signal of 15 if actively tracking a missile.");
	    addGuidebook("chapter.missiledefense.l3.1",
		    "To link a turret to the radar, hover over the block with a %1$s and Shift + Right-Click the block:");
	    addGuidebook("chapter.missiledefense.l3.2",
		    "This will store the radar's position. Then, hover over the turret of choice and Shift + Right-Click with the %1$s in hand:");
	    addGuidebook("chapter.missiledefense.l3.3",
		    "Note the turret must be within %1$s blocks of the radar to be able to be bound. Next we will cover the different turrets offered, their stats, and their strengths and weaknesses.");

	    addGuidebook("chapter.missiledefense.turretminrange", "Min Range: %s");
	    addGuidebook("chapter.missiledefense.turretmaxrange", "Max Range: %s");
	    addGuidebook("chapter.missiledefense.turretammo", "Ammo: %s");
	    addGuidebook("chapter.missiledefense.turretelevation", "Elevation: +%s" + '\u02DA');
	    addGuidebook("chapter.missiledefense.turretdepression", "Depression: -%s" + '\u02DA');

	    addGuidebook("chapter.missiledefense.samturret1",
		    """
		    	The SAM Turret specializes in long-range missile defense. Its base maximum range can also be further upgraded with Range Upgrades. The %1$s it fires is homing so long as the radar the turret is linked to remains operational, thus increasing the chance of a successful hit. A SAM has a %2$s chance to destroy a missile upon impact! \
		    	Upon firing, the turret will need to wait %3$s ticks before it is able to fire again. The effectiveness of the turret comes at a price however, as it can only engage missiles. Also, once a missile is within the minimum range of the turret, it will not be able to fire! The turret is powered from the bottom. If Electrodynamics is installed, it will require Joules at the appropriate \
		    	Voltage. Otherwise it will use FE.""");

	    addGuidebook("chapter.missiledefense.ciwsturret1",
		    """
		    	The Close-In Weapons System or CIWS Turret is designed as a last line of defense against an incoming missile, boasting an impressive 20 rounds / second! The turret is reasonably accurate, but accuracy quickly decreases the further a target is from it. Its base range can be increased with Range Upgrades. Each %1$s will \
		    	inflict one damage to an incoming missile with each missile having %2$s health. The turret must have a direct line of sight to the missile, and it can only hold up to 128 rounds. The CIWS is able to engage both missiles and entities, and bullets deal 10 damage upon hitting an entity. The turret is powered from the bottom. If Electrodynamics is installed, it will require Joules \
		    	at the appropriate Voltage. Otherwise it will use FE.""");

	    addGuidebook("chapter.missiledefense.laserturret1",
		    """
		    	The Laser Turret offers an alternative to the CIWS and SAM turrets, as it does not utilize ammunition. However this comes at the cost of being incredibly power hungry. On top of being incredibly power hungry, the turret also has a heat buffer that builds up while firing. If the temperature of the turret reaches past \
		    	a certain point, the turret will need to cool down before it can engage targets again. The turret deals a maximum of one damage/tick, however the further a target is from the turret, the less damage it will deal. The maximum range of the turret also cannot be upgraded. The Laser Turret can engage both missiles and entities, however it must have a clear line of sight to the target. \
		    	Entities damaged by the turret will also be set on fire! The turret is powered from the bottom. If Electrodynamics is installed, it will require Joules at the appropriate Voltage. Otherwise it will use FE.""");
	    addGuidebook("chapter.missiledefense.laserturret.energy", "Energy");

	    addGuidebook("chapter.missiledefense.railgunturret1",
		    "The Railgun Turret is a hybrid between the CIWS and SAM turrets. It fires Steel Rods that destroy a missile on contact. Without Electrodynamics installed, one can use iron ingots. Its maximum range can also be increased with Range Upgrades. The turret must wait %1$s ticks between shots, and like with the Laser Turret, it is incredibly power-hungry. The Railgun Turret can "
			    + "engage both missiles and entities. The steel rod will deal 20 damage on hitting and entity, but the turret must have a clear line of sight to engage! The turret is powered from the bottom. If Electrodynamics is installed, it will require Joules at the appropriate Voltage. Otherwise it will use FE.");
	    addGuidebook("chapter.missiledefense.railgunturret.rod", "Steel Rod");

	    addGuidebook("chapter.missiledefense.l4",
		    "Turrets that can engage entities as well as players can only target entities up to 1/4 their maximum range away. They will not require a radar however to be able to do this. Turrets that are linked to a radar can still engage entities, however they will prioritize any missile the radar is tracking.");

	    addGuidebook("chapter.missiledefense.l5.1",
		    "Turrets that can target entities will be able to be toggled between targeting only players or all living entities via  the \"Targeting Mode\" tab:");
	    addGuidebook("chapter.missiledefense.l5.2",
		    "There is no ability to specify the mobs the turret targets, however you are able to whitelist certain players. This can be accomplished via the Whitelist Manager tab:");
	    addGuidebook("chapter.missiledefense.l5.3",
		    "There, you can enter the name of the players you wish to whitelist to the turret. The name of the player who places the turret is added automatically. Note that whitelist mode must be enabled to actually work! If you disable whitelist mode on a previously white-listed turret, the names in the list won't be wiped! You can "
			    + "disable a turret from attacking all entities and players by setting the Targeting Mode to \"None\".");

	    addGuidebook("chapter.missiledefense.antiballisticmissile1",
		    "The most powerful form of missile defense available is the %1$s. The %1$s is capable of reaching higher speeds than its SAM counterpart, and boasts a %2$s chance to destroy a missile! To launch one, place it in a Missile Silo with the warhead slot empty. The Silo must be linked to a Fire Control Radar, and will only "
			    + "fire if the radar is actively tracking an incoming missile and receives a redstone signal. To link a missile silo to a radar, enter the radar's coordinates into the silo where the target coordinates typically go.");
	    addGuidebook("chapter.missiledefense.antiballisticmissile2",
		    "While the redstone requirement may sound complicated, recall the Fire Control Radar will read a redstone signal of 15 via a comparator while actively tracking an incoming missile. Therefore, automating the sequence is as simple as this:");

	    addGuidebook("chapter.missiledefense.l6",
		    "It is important to note at this point that having only one turret or Anti-Ballistic missile silo is not a good defense. Optimal defenses will have multiple turrets of different types to ensure they are not easily overwhelmed. Before we continue, if you stop reading here you will be able to successfully set up missile defenses "
			    + "to protect your base. However if you continue reading, you will see that there is a higher skill ceiling for Ballistix Defenses!");

	    addGuidebook("chapter.missiledefense.esmtower1",
		    "The ESM Tower is used to detect nearby radars within a %s block range. Fire Control Radars have their exact positions listed by the Tower if they are active! The Tower is powered from the bottom. If Electrodynamics is installed, it will require Joules at the appropriate Voltage. Otherwise it will use FE. Note the tower "
			    + "must be above ground to work!");

	    addGuidebook("chapter.missiledefense.l7",
		    "The ESM Tower's existence means that if you leave your Fire Control Radar running all the time, you will be a very easy target for base hunters! However simply turning the Radar off is not a good solution, as then you will be left a sitting duck. Fortunately, there is a way you can fight back against any potential base hunter.");

	    addGuidebook("chapter.missiledefense.searchradar1",
		    """
		    	The Search Radar is a lower-powered radar that can be utilized as an early warning device. It is able to detect missiles or ESM Towers out to a range of %s blocks. Due to the block being a weaker radiation source, the ESM Tower will only be able to determine that a Search Radar is in its detection range without listing \
		    	any coordinates. The Radar can detect an unlimited amount of incoming missiles or ESM Towers, but more importantly however, the Radar will emit a redstone signal upon detecting something. It will also read a comparator signal of 0 if only detecting missiles, a signal of 8 if it is only detecting ESM Towers, and will read a signal of 15 if it is detecting both missiles \
		    	and ESM Towers. You can therefore use these redstone signals to automate when your defenses flip on!""");
	    addGuidebook("chapter.missiledefense.searchradar2",
		    "It is important to note however that the Search Radar does not discriminate between the missiles it detects, meaning it can detect the missiles you launch as well! To prevent this, you can whitelist certain launch frequencies to exclude them from detection. To add frequencies, select the Frequency Manager tab inside "
			    + "the radar's GUI:");
	    addGuidebook("chapter.missiledefense.searchradar3",
		    "Note the whitelist mode must be enabled for the frequencies to actually be ignored. Disabling whitelist mode won't wipe any stored frequencies! The Fire Control Radar has an identical whitelist feature for reference. The Search Radar must be placed above-ground in order to work and is powered from the bottom. "
			    + "If Electrodynamics is installed, it will require Joules at the appropriate Voltage. Otherwise it will use FE.");

	    addGuidebook("chapter.missiledefense.l8",
		    "\"The Search Radar is great and all, but I can still be detected by an ESM Tower even with all of my fancy redstone\" you might be saying to yourself right now. Don't worry, as there is one final trick you can implement. While the Search Radar is not strong enough to actively provide "
			    + "targeting data to turrets, it can still provide location data. We can make use of this fact!");
	    addGuidebook("chapter.missiledefense.l9",
		    "By feeding the coordinates of a detected ESM Tower into a missile silo prepped to launch, you can effectively counter-fire upon any ESM Tower the moment one is detected. The threat of a nuclear missile being inbound will give any ESM Tower user pause!");
	    addGuidebook("chapter.missiledefense.l10",
		    "To configure this counter-launch feature is identical to how the Anti-Ballistic missile setup is configured with the Fire Control radar. Input the Search Radar's coordinates into the targeting field of the silo, and provide it a redstone signal to fire when the Search Radar detects an ESM Tower:");

	    addGuidebook("chapter.missiledefense.l11",
		    "Remember the best Ballistix defense is still not being found. Any active defenses you do construct should have redundancies built in. ");

	    addGuidebook("chapter.misc", "Misc.");

	    addGuidebook("chapter.misc.clustermissile1",
		    "The Cluster Missile is the most lethal attack option Ballistix has to offer. The missile can carry up to Tier 3 explosives. Its lethality comes from the fact that upon final decent, the missile splits into 5 separate warheads, instantly creating 5 times the targets for air defense to shoot down! The catch however is "
			    + "that it takes 5 explosive warheads to launch in the first place, and can only be fired from the Missile Silo. This means if the missile is destroyed before splitting, you end up losing 5 times the resources. Ideally, Cluster Missiles will be apart of larger attacks.");

	    addGuidebook("chapter.misc.airraidsiren1",
		    "The Air Raid Siren is as the name suggests. It is activated with a redstone signal and does not require power. It can have its range, pitch, and volume adjusted via the GUI.");

	    addGuidebook("chapter.misc.proximitydetector1",
		    "The Proximity Detector can be used to detect entities inside of its detection range. When detecting an entity, it will emit a redstone signal of 15. It requires energy to function however. If Electodynamics is installed, it will require joules at the appropriate voltage. Otherwise it will use FE.");
	    addGuidebook("chapter.misc.proximitydetector2.1",
		    "The Detector has a maximum range of 9 blocks in any direction. This can be programmed via the detection range fields in its GUI:");
	    addGuidebook("chapter.misc.proximitydetector2.2",
		    "It should be noted that the minimum fields will be interpreted as negative even though the value entered is positive. To visualize the area the Detector will be checking, you can use the \"Detection Field\" button to toggle displaying it.");
	    addGuidebook("chapter.misc.proximitydetector3.1",
		    "The Detector can toggle between detecting all living entities, only players, or nothing at all. This can be cycled via the \"Targeting Mode\" tab in its GUI:");
	    addGuidebook("chapter.misc.proximitydetector3.2",
		    "If the Detector is set to \"None\", then it will effectively be disabled. When it comes to player detection, you are able to exclude certain players from detection via the \"Whitelist Manager\" tab in the GUI. Players who are in this list will not trip the Detector when inside its field. It should be noted the player "
			    + "who placed the Detector will be automatically added to the list.");
	    addGuidebook("chapter.misc.proximitydetector3.3",
		    "Please note all of the above functionality assumes that the \"Whitelist Mode\" is disabled:");
	    addGuidebook("chapter.misc.proximitydetector3.4",
		    "If this mode is enabled, the Detector will now exclusively emit a redstone signal only if a player in its Whitelist is detected. This is regardless if the Targeting Mode is set to all living or not.");
	    addGuidebook("chapter.misc.proximitydetector4",
		    "One final note on the Proximity Detector is that any player or entity holding a Jammer will not be picked up by it, even if they are on the Whitelist. This can be especially useful when trying to bypass someone's base defenses!");

	    addJei("info.item.missilecloserange", "Specs:\n    Range: 3000 Blocks");
	    addJei("info.item.missilemediumrange", "Specs:\n    Range: 10 000 Blocks");
	    addJei("info.item.missilelongrange", "Specs:\n    Range: Unlimited");

	    // Sections
	    addConfiguration("common", "Common");
	    addConfiguration("explosives", "Explosives");
	    addConfiguration("should_multithread_raytracing", "Should Multithread Raytracing (READ COMMENT!!!)");
	    addConfiguration("tier1", "Tier 1");
	    addConfiguration("tier2", "Tier 2");
	    addConfiguration("tier3", "Tier 3");
	    addConfiguration("missile_silo", "Missile Silo");
	    addConfiguration("esm_tower", "ESM Tower");
	    addConfiguration("search_radar", "Search Radar");
	    addConfiguration("fire_control_radar", "Fire Control Radar");
	    addConfiguration("sam_turret", "SAM Turret");
	    addConfiguration("sam_entity", "SAM Entity");
	    addConfiguration("anti_ballistic_entity", "Anti-Ballistic Entity");
	    addConfiguration("ciws_turret", "CIWS Turret");
	    addConfiguration("laser_turret", "Laser Turret");
	    addConfiguration("railgun_turret", "Railgun Turret");
	    addConfiguration("proximity_detector", "Proximity Detector");
	    addConfiguration("items", "Items");

	    // Explosives (global)
	    addConfiguration("should_cache_explosions", "Cache Explosions");

	    // Explosives → Tier 1
	    addConfiguration("explosive_obsidian_size", "Obsidian Size");
	    addConfiguration("explosive_condensive_size", "Condensive Size");
	    addConfiguration("explosive_attractive_size", "Attractive Size");
	    addConfiguration("explosive_attractive_repulsive_push_strength", "Attractive/Repulsive Push Strength");
	    addConfiguration("explosive_repulsive_size", "Repulsive Size");
	    addConfiguration("explosive_incendiary_radius", "Incendiary Radius");
	    addConfiguration("explosive_shrapnel_shrapnel_count", "Shrapnel Count");
	    addConfiguration("explosive_chemical_size", "Chemical Size");
	    addConfiguration("explosive_chemical_duration_ticks", "Chemical Duration (ticks)");
	    addConfiguration("explosive_anvil_anvilsperblast", "Anvils per Blast");
	    addConfiguration("explosive_infestive_radius", "Infestive Radius");
	    addConfiguration("explosive_infestive_duration_ticks", "Infestive Duration (ticks)");
	    addConfiguration("explosive_debilitation_size", "Debilitation Size");
	    addConfiguration("explosive_debilitation_duration_ticks", "Debilitation Duration (ticks)");

	    // Explosives → Tier 2
	    addConfiguration("explosive_fragmentation_shrapnel_count", "Fragmentation Shrapnel Count");
	    addConfiguration("explosive_contagious_size", "Contagious Size");
	    addConfiguration("virus_effect_radius", "Virus Effect Radius");
	    addConfiguration("explosive_contagious_duration_ticks", "Contagious Duration (ticks)");
	    addConfiguration("explosive_breaching_size", "Breaching Size");
	    addConfiguration("explosive_breaching_duration_ticks", "Breaching Duration (ticks)");
	    addConfiguration("explosive_breaching_energy", "Breaching Energy");
	    addConfiguration("explosive_thermobaric_size", "Thermobaric Size");
	    addConfiguration("explosive_thermobaric_duration_ticks", "Thermobaric Duration (ticks)");
	    addConfiguration("explosive_thermobaric_energy", "Thermobaric Energy");
	    addConfiguration("explosive_sonic_maxhardness", "Sonic Max Hardness");
	    addConfiguration("explosive_sonic_radius", "Sonic Radius");
	    addConfiguration("explosive_sonic_velocity", "Sonic Velocity");
	    addConfiguration("explosive_sonic_duration_ticks", "Sonic Duration (ticks)");

	    // Explosives → Tier 3
	    addConfiguration("explosive_antigravity_chunkradius", "Antigravity Chunk Radius");
	    addConfiguration("explosive_antigravity_chunkduration_ticks", "Antigravity Chunk Duration (ticks)");
	    addConfiguration("explosive_antigravity_gravityfactor", "Antigravity Gravity Factor");
	    addConfiguration("explosive_antigravity_maxheight", "Antigravity Max Height");
	    addConfiguration("explosive_antigravity_maxblockchecks", "Antigravity Max Block Checks");
	    addConfiguration("explosive_emp_radius", "EMP Radius");
	    addConfiguration("explosive_nuclear_size", "Nuclear Size");
	    addConfiguration("explosive_nuclear_duration_ticks", "Nuclear Duration (ticks)");
	    addConfiguration("explosive_nuclear_energy", "Nuclear Energy");
	    addConfiguration("explosive_nuclear_radiation_radius", "Nuclear Radiation Radius");
	    addConfiguration("explosive_endothermic_maxhardness", "Endothermic Max Hardness");
	    addConfiguration("explosive_endothermic_radius", "Endothermic Radius");
	    addConfiguration("explosive_endothermic_velocity", "Endothermic Velocity");
	    addConfiguration("explosive_endothermic_duration_ticks", "Endothermic Duration (ticks)");
	    addConfiguration("explosive_exothermic_maxhardness", "Exothermic Max Hardness");
	    addConfiguration("explosive_exothermic_radius", "Exothermic Radius");
	    addConfiguration("explosive_exothermic_chance_for_lava", "Exothermic Chance for Lava");
	    addConfiguration("explosive_exothermic_chance_to_burn", "Exothermic Chance to Burn");
	    addConfiguration("explosive_exothermic_duration_ticks", "Exothermic Duration (ticks)");
	    addConfiguration("explosive_ender_radius", "Ender Radius");
	    addConfiguration("explosive_ender_endermancount", "Ender Enderman Count");
	    addConfiguration("explosive_hypersonic_maxhardness", "Hypersonic Max Hardness");
	    addConfiguration("explosive_hypersonic_radius", "Hypersonic Radius");
	    addConfiguration("explosive_hypersonic_velocity", "Hypersonic Velocity");
	    addConfiguration("explosive_hypersonic_duration_ticks", "Hypersonic Duration (ticks)");
	    addConfiguration("explosive_antimatter_radius", "Antimatter Radius");
	    addConfiguration("explosive_antimatter_duration_ticks", "Antimatter Duration (ticks)");
	    addConfiguration("explosive_largeantimatter_radius", "Large Antimatter Radius");
	    addConfiguration("explosive_largeantimatter_duration_ticks", "Large Antimatter Duration (ticks)");
	    addConfiguration("explosive_darkmatter_radius", "Dark Matter Radius");
	    addConfiguration("explosive_darkmatter_duration_ticks", "Dark Matter Duration (ticks)");
	    addConfiguration("explosive_darkmatter_persistance_iterations", "Dark Matter Persistence Iterations");
	    addConfiguration("explosive_darkmatter_movementticks", "Dark Matter Movement Ticks");
	    addConfiguration("explosive_darkmatter_repeatduration_ticks", "Dark Matter Repeat Duration (ticks)");

	    // Missile Silo
	    addConfiguration("missilesilo_usage_joules_per_launch", "Silo Usage per Launch (J)");
	    addConfiguration("missile_health", "Missile Health");
	    addConfiguration("launcher_platform_range_t1", "Launcher Platform Range T1");
	    addConfiguration("launcher_platform_range_t2", "Launcher Platform Range T2");
	    addConfiguration("launcher_platform_range_t3", "Launcher Platform Range T3");
	    addConfiguration("vls_range", "VLS Range");
	    addConfiguration("launch_platform_default_inaccuracy_deg", "Launch Platform Default Inaccuracy (deg)");

	    // ESM Tower
	    addConfiguration("esm_tower_usage_per_tick", "ESM Usage per Tick");
	    addConfiguration("esm_tower_search_radius", "ESM Search Radius");

	    // Search Radar
	    addConfiguration("radar_usage_watt", "Radar Usage (W)");
	    addConfiguration("radar_range", "Radar Range");

	    // Fire Control Radar
	    addConfiguration("fire_control_radar_usage_watt", "Fire Control Radar Usage (W)");
	    addConfiguration("fire_control_radar_range", "Fire Control Radar Range");
	    addConfiguration("max_distance_from_radar", "Max Distance from Radar");

	    // SAM Turret
	    addConfiguration("sam_turret_usage_per_tick", "SAM Usage per Tick");
	    addConfiguration("sam_turret_base_range", "SAM Base Range");
	    addConfiguration("sam_turret_rotation_speed_radians", "SAM Rotation Speed (rad)");
	    addConfiguration("sam_turret_cooldown_ticks", "SAM Cooldown (ticks)");

	    // SAM Entity
	    addConfiguration("sam_entity_turning_speed_radians", "SAM Entity Turning Speed (rad)");
	    addConfiguration("sam_top_speed", "SAM Top Speed");
	    addConfiguration("sam_acceleration", "SAM Acceleration");
	    addConfiguration("sam_minturnspeed_perc", "SAM Min Turn Speed (%)");
	    addConfiguration("sam_chance_to_destroy", "SAM Chance to Destroy");

	    // Anti-Ballistic Entity
	    addConfiguration("antiballisticmissile_entity_turning_speed_radians", "ABM Turning Speed (rad)");
	    addConfiguration("antiballisticmissile_chance_to_destroy", "ABM Chance to Destroy");
	    addConfiguration("antiballisticmissile_top_speed", "ABM Top Speed");
	    addConfiguration("antiballisticmissile_acceleration", "ABM Acceleration");
	    addConfiguration("antiballisticmissile_minturnspeed_perc", "ABM Min Turn Speed (%)");

	    // Shared / Misc
	    addConfiguration("range_increase_inaccuracy_multiplier", "Range Increase Inaccuracy Multiplier");

	    // CIWS Turret
	    addConfiguration("ciws_turret_usage_per_tick", "CIWS Usage per Tick");
	    addConfiguration("ciws_turret_base_range", "CIWS Base Range");
	    addConfiguration("ciws_turret_rotation_speed_radians", "CIWS Rotation Speed (rad)");
	    addConfiguration("ciws_innaccuracy", "CIWS Inaccuracy");

	    // Laser Turret
	    addConfiguration("laser_turret_usage_per_tick", "Laser Usage per Tick");
	    addConfiguration("laser_turret_base_range", "Laser Base Range");
	    addConfiguration("laser_turret_rotation_speed_radians", "Laser Rotation Speed (rad)");
	    addConfiguration("laser_turret_max_heat", "Laser Max Heat");
	    addConfiguration("laser_turret_cool_threshold", "Laser Cool Threshold");
	    addConfiguration("laser_turret_base_damage", "Laser Base Damage");

	    // Railgun Turret
	    addConfiguration("railgun_turret_usage_per_tick", "Railgun Usage per Tick");
	    addConfiguration("railgun_turret_base_range", "Railgun Base Range");
	    addConfiguration("railgun_turret_rotation_speed_radians", "Railgun Rotation Speed (rad)");
	    addConfiguration("railgun_turret_cooldown_ticks", "Railgun Cooldown (ticks)");
	    addConfiguration("railgun_innaccuracy", "Railgun Inaccuracy");

	    // Proximity Detector
	    addConfiguration("proximitydetector_usage_per_tick", "Proximity Detector Usage per Tick");

	    // Items
	    addConfiguration("rocket_launcher_cooldown_ticks", "Rocket Launcher Cooldown (ticks)");
	    addConfiguration("laser_designator_range", "Laser Designator Range");

	}

    }

}
