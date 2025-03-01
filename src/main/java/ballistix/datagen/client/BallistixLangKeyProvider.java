package ballistix.datagen.client;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import electrodynamics.datagen.client.ElectrodynamicsLangKeyProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.World;

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
	        addBlock(BallistixBlocks.blockRadar, "Search Radar");
	        addBlock(BallistixBlocks.blockFireControlRadar, "Fire Control Radar");
	        addBlock(BallistixBlocks.blockSamTurret, "SAM Turret");
	        addBlock(BallistixBlocks.blockEsmTower, "ESM Tower");
	        addBlock(BallistixBlocks.blockCiwsTurret, "CIWS Turret");
	        addBlock(BallistixBlocks.blockLaserTurret, "Laser Turret");
	        addBlock(BallistixBlocks.blockRailgunTurret, "Railgun Turret");

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
			
			addItem(BallistixItems.ITEM_AAMISSILE, "Ballistic Rocket");
	        addItem(BallistixItems.ITEM_BULLET, "20mm Bullet");

	        addContainer("missilesilo", "Missile Silo");
	        addContainer("samturret", "SAM Turret");
	        addContainer("searchradar", "Search Radar");
	        addContainer("firecontrolradar", "Fire Control Radar");
	        addContainer("esmtower", "ESM Tower");
	        addContainer("ciwsturret", "CIWS Turret");
	        addContainer("laserturret", "Laser Turret");
	        addContainer("railgunturret", "Railgun Turret");

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


	        addChatMessage("radargun.text", "Coordinates: %s");
	        addChatMessage("radargun.turretsucess", "Bound");
	        addChatMessage("radargun.turrettoofar", "Turret too far away!");
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
	        addTooltip("missile.range", "Range: %s Blocks");
	        addTooltip("missile.unlimited", "Unlimited");
	        addTooltip("missilesilo.charge", "Charge: %1$s / %2$s");

	        addTooltip("turret.blockrange", "Block Range");
	        addTooltip("turret.entityrange", "Entity Range");
	        addTooltip("turret.maxrange", "Max: %s");
	        addTooltip("turret.minrange", "Min: %s");
	        addTooltip("turret.targetmode", "Target Mode");
	        addTooltip("turret.targetmodeplayers", "Only Players");
	        addTooltip("turret.targetmodeliving", "All Living");

	        addTooltip("radar.frequencymanager", "Frequency Manager");
	        addTooltip("radar.frequencymanager.delete", "Delete");

	        addTooltip("radar.redstone", "Redstone");
	        addTooltip("radar.redstone.enabled", "Enabled");
	        addTooltip("radar.redstone.disabled", "Disabled");

	        addTooltip("turret.whitelistmanager", "Whitelist Manager");

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

	        addSubtitle(BallistixSounds.SOUND_ANTIMATTEREXPLOSION, "Antimatter bomb detonates");
	        addSubtitle(BallistixSounds.SOUND_DARKMATTER, "Dark matter bomb detonates");
	        addSubtitle(BallistixSounds.SOUND_NUCLEAREXPLOSION, "Nuclear bomb detonates");
	        addSubtitle(BallistixSounds.SOUND_EMPEXPLOSION, "EMP detonates");
	        addSubtitle(BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER, "Missile is fired from rocket lancher");
	        addSubtitle(BallistixSounds.SOUND_MISSILE_SILO, "Missile launches from silo");
	        addSubtitle(BallistixSounds.SOUND_RADAR, "Radar pulses");
	        addSubtitle(BallistixSounds.SOUND_FIRECONTROLRADAR, "Fire Control Radar tracks");
	        addSubtitle(BallistixSounds.SOUND_CIWS_TURRETFIRING, "CIWS Turret Fires");
	        addSubtitle(BallistixSounds.SOUND_LASER_TURRETFIRING, "Laser Turret fires");

	        addDimension(World.OVERWORLD.location().getPath(), "The Overworld");
	        addDimension(World.NETHER.location().getPath(), "The Nether");
	        addDimension(World.END.location().getPath(), "The End");

	        addGuidebook(References.ID, "Ballistix");

	        addGuidebook("chapter.missilesilo", "Missile Silo");
	        addGuidebook("chapter.missilesilo.l1", "The Missile Silo is used, as the name suggests, to launch various missiles with different types of warheads. The Silo has 3 available missile types with the following block ranges:");
	        addGuidebook("chapter.missilesilo.range", "%1$s : %2$s");
	        addGuidebook("chapter.missilesilo.close", "Close-Range");
	        addGuidebook("chapter.missilesilo.medium", "Medium-Range");
	        addGuidebook("chapter.missilesilo.long", "Long-Range");
	        addGuidebook("chapter.missilesilo.unlimited", "Unlimited");
	        addGuidebook("chapter.missilesilo.l2", "Each missile type is capable of carrying any explosive as a warhead.");

	        addGuidebook("chapter.missilesilo.l3", "To load an explosive into a missile, first place the missile of your choice into the silo in its designated slot. Next, choose an explosive type and place it into its designated spot in the GUI. Next, program the target destination into the coordinate boxes. This can either be done manually or with a Radar Gun. Finally, " +
	                "once this is all completed, to fire the missile, apply a redstone signal to the silo.");

	        addGuidebook("chapter.items", "Items");
	        addGuidebook("chapter.items.rocketlauncher1", "The Rocket Launcher is capable of firing Close-Range missiles with any explosive warhead attached as a projectile. To fire it, have a Close-Range missile in your inventory along with the explosive type of your choice. Note, it must be a Ballistix explosive, and it must be the block form. Once this is done, hold " +
	                "the Launcher and Right-Click to fire!");

	        addGuidebook("chapter.items.radargun1", "The Radar Gun is used to collect coordinates of a target and feed them into the Missile Silo instead of programming them manually. To use the Radar Gun, simply Right-Click on the target with the Gun to store its coordinates. This will expend %s and store the coordinates to the Gun. Then Right-Click on the Missile Silo " +
	                "with the Gun to feed in the coordinates. Alternatively, the coordinates can be imported using the \"Sync\" slot in the GUI.");

	        addGuidebook("chapter.items.laserdesignator1", "The Laser Designator is used to launch missiles from a Missile Silo remotely. To use it, first place a Silo and prepare a missile with your choice of warhead. Next, Right-Click the Designator on the Silo to link the Designator to the Silo's frequency. Alternatively, the frequency can be linked via the \"Sync\" slot. " +
	                "Note, another the benefit of Designator is that you will not need to pre-program coordinates to the Missile Silo.");
	        addGuidebook("chapter.items.laserdesignator2", "With the Silo prepared and the Designator linked to the Silo, all the remains is to find a suitable target. When you find one, simply Right-Click on it with the Designator. This will expend %s, automatically feed the target's coordinates into the Silo, and fire the missile. Note, make sure the target is actually " +
	                "in range of the missile in the Silo!");

	        addGuidebook("chapter.items.defuser1", "The Defuser is your only hope of stopping an explosive from detonating once it has been activated. To use it, Right-Click on the explosive with the Defuser, and it will, as the name suggests, defuse it! This will expend %s and drop the explosive that was defused on the ground. Note, the Defuser also works on Vanilla TNT.");

	        addGuidebook("chapter.items.tracker1", "The Tracker is capable of tracking the location of any entity in the world, player or otherwise. This is especially useful, as it allows you to track other players and send them Thermonuclear War...care packages from afar. To start tracking an entity, simply Right-Click on it with the Tracker. This will expend %s. Note the " +
	                "Tracker only keeps tabs on the X and Z coordinates of the entity along with what dimension they're in, meaning there is still a little guesswork involved. If the entity dies or the player disconnects from the server, the Tracker will stop tracking them!");

	        addGuidebook("chapter.items.scanner1", "The Scanner is every paranoid player's best friend. Think someone has tagged you with a Tracker and is trying to nuke your prestigious dirt house? Fear not; Simply right-click with the Scanner. This will expend %s and jam any tracking signals if they are present!");

	        addGuidebook("chapter.missiledefense","Missile Defense");

	        addGuidebook("chapter.missiledefense.l1", "While the best defense is not to be found, what do you do if your base gets discovered, and now someone has launched a nuclear missile at it? The answer is quite simple: shoot the missile down. Ballistix offers a wide selections of methods to accomplish this task. The following pages will cover these various methods, their " +
	                "strengths, and their weaknesses.");

	        addGuidebook("chapter.missiledefense.searchradar1", "The Search Radar is a highly valuable tool, as it serves as an early warning device for incoming missiles. Being able to detect missiles out to a range of %s blocks, the device will emit a redstone signal upon detection, and will continue to emit a signal while said missile remains detected. Note however that the Search " +
	                "Radar does not discriminate between the missiles it detects, meaning it can detect the missiles you launch as well! To prevent this, you can whitelist certain launch frequencies to exclude them from detection. To add frequencies, select the Frequency Manager tab inside the radar's GUI:");
	        addGuidebook("chapter.missiledefense.searchradar2", "Note the whitelist mode must be enabled for the frequencies to actually be ignored. Disabling whitelist mode won't wipe any stored frequencies! The radar must also be placed above-ground in order to work.");

	        addGuidebook("chapter.missiledefense.firecontrolradar1","The Fire Control Radar is an upgraded and more powerful variant of the Search Radar. The radar is able to lock onto an incoming missile and relay the target information to linked turrets. To link a turret, shift+right-click the radar with a %1$s. Then, shift+right-click the Radar Gun on the turret of choice. There is no limit " +
	                "to the number of turrets that can be bound to a single Fire Control Radar, however the turret can be no more than %2$s blocks away. Note that all turrets bound to a radar will fire at the same target the radar is tracking! A turret will not be able to lock onto a missile if it is not bound to a radar.");
	        addGuidebook("chapter.missiledefense.firecontrolradar2","Similarly to the Search Radar, the Fire Control Radar must be placed above-ground in order to work. Unlike the Search variant, it has a more limited range of %s blocks. A useful feature to note is the radar can be controlled via redstone.");

	        addGuidebook("chapter.missiledefense.esmtower1","The ESM Tower is used to detect nearby radars. The tower can detect radars within a %s block range. Search Radars are weaker by nature, so the tower will only be able to identify if one is within its detection range. Fire Control Radars on the other hand will have their exact positions listed if they are active! Note " +
	                "that an ESM Tower will have its exact position detected by a Search Radar! A detected ESM Tower will cause the radar to emit a redstone signal just as with a missile.");

	        addGuidebook("chapter.missiledefense.samturret1","The SAM Turret is a turret dedicated to long-range missile defense. Firing %1$ss, the turret can engage targets up to %2$s blocks! This range can be further increased with range upgrades, however note that the turret will be less accurate past its base range. The turret is highly accurate, and can hold up to 10 rockets " +
	                "at a time. A Ballistic Rocket will outright destroy a missile upon impact. Note the turret will need to wait %3$s ticks between firing! The turret has a maximum elevation of 90 degrees and a maximum depression of 45 degrees. It should be noted that the turret can only engage missiles. Furthermore, once a missile is within 150 blocks, the turret will no longer be able to engage " +
	                "it! The turret must also have a line of sight to the missile.");

	        addGuidebook("chapter.missiledefense.ciwsturret1","The Close-In Weapons System or CIWS Turret is designed as a last line of defense against an incoming missile. It fires %1$ss at an impressive 20 rounds / second! The turret is reasonably accurate, but accuracy quickly decreases the further a target is from it. It has a base range of %2$s blocks that can be increased " +
	                "with range upgrades. Each bullet will inflict one damage to an incoming missile with each missile having %3$s health. The turret must have a direct line of site to the missile however. The turret can only hold up to 128 rounds. Unlike the SAM Turret, the CIWS has no minimum range. The turret has a maximum elevation of 90 degrees and a maximum depression of 45 degrees.");
	        addGuidebook("chapter.missiledefense.ciwsturret2", "The CIWS can also be used against players and other mobs, and does not need to be bound to a radar to do so. Note it still will be able to engage mobs if bound to a radar, but will prioritize any detected missiles. The detection range is one quarter the turret's current missile engagement range, and the turret must " +
	                "have a clear line of site to engage. While it will target all mobs, you are able to whitelist certain players. To do so, select the Whitelist Manager tab:");
	        addGuidebook("chapter.missiledefense.ciwsturret3", "There, you can enter the name of the players you wish to whitelist to the turret. Note the name of the player who places the turret is added automatically. Bullets deal 10 damage upon impact!");
	        addGuidebook("chapter.missiledefense.ciwsturret4", "While you may not be able to whitelist certain mobs, the turret can have its firing mode programmed to include or exclude mobs when searching for a target. This can be selected via the \"Targeting Mode\" tab:");

	        addGuidebook("chapter.missiledefense.laserturret1","The Laser Turret offers an alternative to the CIWS and SAM turrets, as it does not utilize ammunition. However this comes at the cost of being incredibly power hungry. The turret deals 1 damage/tick, however this is dependent on range. The further a target is from the turret, the less damage it will deal. The turret " +
	                "has a range of %s blocks, however this cannot be upgraded. It has no minimum engagement range. The turret has a maximum elevation of 90 degrees and a maximum depression of 45 degrees.");
	        addGuidebook("chapter.missiledefense.laserturret2","On top of being incredibly power hungry, the turret also has a heat buffer that builds up while firing. If the temperature of the turret reaches past a certain point, it will need to cool down before it can engage targets again! Like with the CIWS, the Laser Turret can attack entities and players within a quarter of its " +
	                "missile targeting range. Mobs and players are additionally set on fire when damaged by the laser turret. It must have a clear line of site to the mob also.");

	        addGuidebook("chapter.missiledefense.railgunturret1","The Railgun Turret is a hybrid between the CIWS and SAM turrets. It fires %1$ss that destroy a missile on contact. It is able to engage missiles up to %2$s blocks, and this range can be increased with range upgrades. It has no minimum engagement range. The turret must wait %3$s ticks between shots, and can " +
	                "only hold up to 64 rods at a time. Like with the Laser Turret, it is incredibly power-hungry. The turret has a maximum elevation of 45 degrees and a maximum depression of 45 degrees.");
	        addGuidebook("chapter.missiledefense.railgunturret2","Like with the CIWS and Laser Turrets, the Railgun Turret can engage players and mobs up to one quarter the distance of its missile engagement range. The steel rod will deal 20 damage on impact, but the turret must have a clear line of site to engage!");


	        addJei("info.item.missilecloserange", "Specs:\n    Range: 3000 Blocks");
	        addJei("info.item.missilemediumrange", "Specs:\n    Range: 10 000 Blocks");
	        addJei("info.item.missilelongrange", "Specs:\n    Range: Unlimited");

		}

	}

}
