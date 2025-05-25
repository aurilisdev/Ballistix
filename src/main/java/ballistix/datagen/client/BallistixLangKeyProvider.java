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
import net.minecraft.data.DataGenerator;
import net.minecraft.world.World;
import voltaic.datagen.utils.client.BaseLangKeyProvider;

public class BallistixLangKeyProvider extends BaseLangKeyProvider {

	public BallistixLangKeyProvider(DataGenerator gen, Locale locale) {
		super(gen, locale, Ballistix.ID);
	}

	@Override
	protected void addTranslations() {

		switch (locale) {
		case EN_US:
		default:

			add("itemGroup.itemgroup" + Ballistix.ID + "main", "Ballistix");

			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.obsidian), "Obsidian TNT");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.condensive), "Condensive Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.attractive), "Attractive Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.repulsive), "Repulsive Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.incendiary), "Incendiary Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.breaching), "Breaching Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel), "Shrapnel Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.debilitation), "Debilitation Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.chemical), "Chemical Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric), "Thermobaric Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.contagious), "Contagious Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation), "Fragmentation Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.emp), "EMP Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.nuclear), "Nuclear Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.antimatter), "Antimatter Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter), "Large Antimatter Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter), "Darkmatter Explosive");
			addBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(SubtypeBlast.landmine), "Landmine");

			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), "Launcher Control Panel T1");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), "Launcher Control Panel T2");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), "Launcher Control Panel T3");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), "Launcher Support Frame T1");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), "Launcher Support Frame T2");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), "Launcher Support Frame T3");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1), "Launcher Platform T1");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), "Launcher Platform T2");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), "Launcher Platform T3");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar), "Search Radar");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar), "Fire Control Radar");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret), "SAM Turret");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), "ESM Tower");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret), "CIWS Turret");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret), "Laser Turret");
			addBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret), "Railgun Turret");

			addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.condensive), "Condensive Grenade");
			addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.attractive), "Attractive Grenade");
			addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.repulsive), "Repulsive Grenade");
			addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.incendiary), "Incendiary Grenade");
			addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.shrapnel), "Shrapnel Grenade");
			addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.debilitation), "Debilitation Grenade");
			addItem(BallistixItems.ITEMS_GRENADE.getValue(SubtypeGrenade.chemical), "Chemical Grenade");

			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.obsidian), "Minecart with Obsidian TNT");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.condensive), "Minecart with Condensive Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.attractive), "Minecart with Attractive Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.repulsive), "Minecart with Repulsive Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.incendiary), "Minecart with Incendiary Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.breaching), "Minecart with Breaching Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.shrapnel), "Minecart with Shrapnel Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.debilitation), "Minecart with Debilitation Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.chemical), "Minecart with Chemical Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.thermobaric), "Minecart with Thermobaric Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.contagious), "Minecart with Contagious Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.emp), "Minecart with EMP Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.fragmentation), "Minecart with Fragmentation Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.nuclear), "Minecart with Nuclear Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.antimatter), "Minecart with Antimatter Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.largeantimatter), "Minecart with Large Antimatter Explosive");
			addItem(BallistixItems.ITEMS_MINECART.getValue(SubtypeMinecart.darkmatter), "Minecart with Darkmatter Explosive");

			addItem(BallistixItems.ITEM_DUSTPOISON, "Poison Dust");
			//addItem(BallistixItems.ITEM_RANGEUPGRADE, "Range Upgrade");

			addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier1), "T1 Missile");
			addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier2), "T2 Missile");
			addItem(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.tier3), "T3 Missile");

			addItem(BallistixItems.ITEM_AAMISSILE, "Surface-to-Air Missile");
			addItem(BallistixItems.ITEM_AAMISSILEMK2, "Anti-Ballistic Missile");
			addItem(BallistixItems.ITEM_BULLET, "20mm Bullet");

			addItem(BallistixItems.ITEM_ROCKETLAUNCHER, "Rocket Launcher");
			addItem(BallistixItems.ITEM_RADARGUN, "Radar Gun");
			addItem(BallistixItems.ITEM_LASERDESIGNATOR, "Laser Designator");
			addItem(BallistixItems.ITEM_TRACKER, "Tracker");
			addItem(BallistixItems.ITEM_SCANNER, "Scanner");
			addItem(BallistixItems.ITEM_DEFUSER, "Defuser");

			addContainer("launchercontrolpaneltier1", "Launcher Control Panel T1");
			addContainer("launchercontrolpaneltier2", "Launcher Control Panel T2");
			addContainer("launchercontrolpaneltier3", "Launcher Control Panel T3");
			addContainer("launcherplatformtier1", "Launcher Platform T1");
			addContainer("launcherplatformtier2", "Launcher Platform T2");
			addContainer("launcherplatformtier3", "Launcher Platform T3");
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

			addTooltip("aamissile.hitrate", "Hit rate: %s");

			
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
			addSubtitle(BallistixSounds.SOUND_RODHITTINGGROUND, "Rod Impacts Ground");
			addSubtitle(BallistixSounds.SOUND_RAILGUNKINETIC, "Railgun Turret Fires");

			addDimension(World.OVERWORLD.location().getPath(), "The Overworld");
			addDimension(World.NETHER.location().getPath(), "The Nether");
			addDimension(World.END.location().getPath(), "The End");

			addGuidebook(Ballistix.ID, "Ballistix");

			addGuidebook("chapter.missilesilo", "Missile Silo");
			addGuidebook("chapter.missilesilo.l1.1", "The Missile Silo is a multiblock used, as the name suggests, to launch various missiles with different types of warheads. The Silo is composed of three different blocks:");
			addGuidebook("chapter.missilesilo.controlpanel", "Control Panel");
			addGuidebook("chapter.missilesilo.launchplatform", "Launch Platform");
			addGuidebook("chapter.missilesilo.supportframe", "Support Frame");
			addGuidebook("chapter.missilesilo.l1.2", "Each come in three tiers which provide successive buffs to the silo.");
			addGuidebook("chapter.missilesilo.l2", "The %s is used to provide targeting information to the silo and instructs the Launch Platform to fire. It requires power to function, and can be instructed to launch with either a redstone signal or by using a Laser Designator. The silo will be on cooldown upon firing a missile, but will continue to fire missiles while it has a redstone " +
					"signal once the cooldown is over. The Tier 1 Control Panel is the cheapest, but can only specify the X and Z coordinates of a target. The Tier 2 Panel is more expensive, but gains the ability to specify the Y variant. The missile will detonate once it reaches this Y value too, allowing for more precise strikes. The Tier 3 Control Panel is the most expensive, but gains the ability to " +
					"specify the launch frequency of the silo.");
			addGuidebook("chapter.missilesilo.l3.1", "The %1$s is what holds the missile and accompanying warhead. The Tier 1 variant can launch missiles %2$s blocks, the Tier 2 variant can launch blocks %3$s blocks, and the Tier 3 Variant can launch missiles %4$s Blocks. There are three types of missiles that can be loaded with increasing ranges:");
			addGuidebook("chapter.missilesilo.range", "%1$s : %2$s");
			addGuidebook("chapter.missilesilo.close", "Tier 1");
			addGuidebook("chapter.missilesilo.medium", "Tier 2");
			addGuidebook("chapter.missilesilo.long", "Tier 3");
			addGuidebook("chapter.missilesilo.unlimited", "Unlimited");
			addGuidebook("chapter.missilesilo.l3.2", "Each missile type is capable of carrying any explosive as a warhead. It should be noted that any Launch Platform can fire any missile tier, but it will only launch the missile as far as either the missile's range or the Launch Platform's range, whichever is less.");

			addGuidebook("chapter.missilesilo.l4", "The %1$s is used to provide increased accuracy to the launched missile. The Launch Platform is able to fire missiles without one, but will incur an default inaccuracy of %2$s blocks, meaning the missile will land within %2$s blocks of the specified X and Z coordinates. The Tier 1 variant decreases the inaccuracy penalty to 30 blocks, " +
					"the Tier 2 variant decrease the inaccuracy to 15 blocks, while the Tier 3 variant provides perfect accuracy.");

			addGuidebook("chapter.items", "Items");
			addGuidebook("chapter.items.rocketlauncher1", "The Rocket Launcher is capable of firing Tier 1 missiles with any explosive warhead. To fire it, have a Tier 1 missile in your inventory along with the explosive type of your choice. Note, it must be a Ballistix explosive, and it must be the block form. Once this is done, hold " + "the Launcher and Right-Click to fire!");

			addGuidebook("chapter.items.radargun1",
					"The Radar Gun is used to collect coordinates of a target and feed them into the Missile Silo instead of programming them manually. To use the Radar Gun, simply Right-Click on the target with the Gun to store its coordinates. This will expend %s and store the coordinates to the Gun. Then Right-Click on the Missile Silo " + "with the Gun to feed in the coordinates. Alternatively, the coordinates can be imported using the \"Sync\" slot in the GUI.");

			addGuidebook("chapter.items.laserdesignator1", "The Laser Designator is used to launch missiles from a Missile Silo remotely. To use it, first place a Silo and prepare a missile with your choice of warhead. Next, Right-Click the Designator on the Silo to link the Designator to the Silo's frequency. Alternatively, the frequency can be linked via the \"Sync\" slot. " + "Note, another the benefit of Designator is that you will not need to pre-program coordinates to the Missile Silo.");
			addGuidebook("chapter.items.laserdesignator2", "With the Silo prepared and the Designator linked to the Silo, all the remains is to find a suitable target. When you find one, simply Right-Click on it with the Designator. This will expend %s, automatically feed the target's coordinates into the Silo, and fire the missile. Note, make sure the target is actually " + "in range of the missile in the Silo!");

			addGuidebook("chapter.items.defuser1", "The Defuser is your only hope of stopping an explosive from detonating once it has been activated. To use it, Right-Click on the explosive with the Defuser, and it will, as the name suggests, defuse it! This will expend %s and drop the explosive that was defused on the ground. Note, the Defuser also works on Vanilla TNT.");

			addGuidebook("chapter.items.tracker1", "The Tracker is capable of tracking the location of any entity in the world, player or otherwise. This is especially useful, as it allows you to track other players and send them Thermonuclear War...care packages from afar. To start tracking an entity, simply Right-Click on it with the Tracker. This will expend %s. Note the "
					+ "Tracker only keeps tabs on the X and Z coordinates of the entity along with what dimension they're in, meaning there is still a little guesswork involved. If the entity dies or the player disconnects from the server, the Tracker will stop tracking them!");

			addGuidebook("chapter.items.scanner1", "The Scanner is every paranoid player's best friend. Think someone has tagged you with a Tracker and is trying to nuke your prestigious dirt house? Fear not; Simply right-click with the Scanner. This will expend %s and jam any tracking signals if they are present!");

			addGuidebook("chapter.missiledefense", "Missile Defense");

			addGuidebook("chapter.missiledefense.l1", "While the best defense is not to be found, what do you do if your base gets discovered, and now someone has launched a nuclear missile at it? The answer is quite simple: shoot the missile down. Ballistix offers a wide selections of methods to accomplish this task. The following pages will cover these various methods, their " + "strengths, and their weaknesses.");

			addGuidebook("chapter.missiledefense.searchradar1", "The Search Radar is a highly valuable tool, as it serves as an early warning device for incoming missiles. Being able to detect missiles or ESM Towers out to a range of %s blocks, the device will emit a redstone signal upon detection, and will continue to emit a signal while it is detecting something. The Radar will " +
					"read a comparator signal of 8 if it is only detecting an ESM Tower, and will read a signal of 15 if it is detecting both missiles and ESM Towers. Note however that the Search Radar does not discriminate between the missiles it detects, meaning it can detect the missiles you launch as well! To prevent this, you can whitelist certain launch frequencies to exclude them from " +
					"detection. To add frequencies, select the Frequency Manager tab inside the radar's GUI:");
			addGuidebook("chapter.missiledefense.searchradar2", "Note the whitelist mode must be enabled for the frequencies to actually be ignored. Disabling whitelist mode won't wipe any stored frequencies! The radar must also be placed above-ground in order to work.");

			addGuidebook("chapter.missiledefense.firecontrolradar1", "The Fire Control Radar is an upgraded and more powerful variant of the Search Radar. The radar is able to lock onto an incoming missile and relay the target information to linked turrets. To link a turret, shift+right-click the radar with a %1$s. Then, shift+right-click the Radar Gun on the turret of choice. There is no limit "
					+ "to the number of turrets that can be bound to a single Fire Control Radar, however the turret can be no more than %2$s blocks away. Note that all turrets bound to a radar will fire at the same target the radar is tracking! A turret will not be able to lock onto a missile if it is not bound to a radar.");
			addGuidebook("chapter.missiledefense.firecontrolradar2", "Similarly to the Search Radar, the Fire Control Radar must be placed above-ground in order to work. Unlike the Search variant, it has a more limited range of %s blocks. A useful feature to note is the radar can be controlled via redstone.");

			addGuidebook("chapter.missiledefense.esmtower1", "The ESM Tower is used to detect nearby radars. The Tower can detect radars within a %s block range. Search Radars are weaker by nature, so the tower will only be able to identify if one is within its detection range. Fire Control Radars on the other hand will have their exact positions listed if they are active! Note "
					+ "that an ESM Tower will have its exact position detected by a Search Radar! A detected ESM Tower will cause the radar to emit a redstone signal just as with a missile.");
			addGuidebook("chapter.missiledefense.esmtower2", "Using the tower does not come with risks however! A missile silo can be configured to counter-launch if an ESM Tower is picked up by the Search Radar. A silo can be linked to a Search Radar by inputting the Radar's coordinates into the silo's target fields. Upon receiving a redstone signal, the silo will launch at the " +
					"ESM Tower's position.");

			addGuidebook("chapter.missiledefense.samturret1", "The SAM Turret is a turret dedicated to long-range missile defense. Firing %1$ss, the turret can engage targets up to %2$s blocks! This range can be further increased with range upgrades. On top of this, the missiles launched are homing so long as the Fire Control Radar is tracking a target, granting an increased chance " +
					"to hit the target!. The turret is highly accurate, and can hold up to 10 rockets at a time. A Smart Missile will outright destroy a Ballistic Missile, however it only has a %4$s chance to do so! Note the turret will need to wait %3$s ticks between firing! The turret has a maximum elevation of 90 degrees and a maximum depression of 45 degrees. It should be noted that the turret " +
					"can only engage missiles. Furthermore, once a missile is within 100 blocks, the turret will no longer be able to engage it!");

			addGuidebook("chapter.missiledefense.ciwsturret1", "The Close-In Weapons System or CIWS Turret is designed as a last line of defense against an incoming missile. It fires %1$ss at an impressive 20 rounds / second! The turret is reasonably accurate, but accuracy quickly decreases the further a target is from it. It has a base range of %2$s blocks that can be increased "
					+ "with range upgrades. Each bullet will inflict one damage to an incoming missile with each missile having %3$s health. The turret must have a direct line of site to the missile however. The turret can only hold up to 128 rounds. Unlike the SAM Turret, the CIWS has no minimum range. The turret has a maximum elevation of 90 degrees and a maximum depression of 45 degrees.");
			addGuidebook("chapter.missiledefense.ciwsturret2",
					"The CIWS can also be used against players and other mobs, and does not need to be bound to a radar to do so. Note it still will be able to engage mobs if bound to a radar, but will prioritize any detected missiles. The detection range is one quarter the turret's current missile engagement range, and the turret must " + "have a clear line of site to engage. While it will target all mobs, you are able to whitelist certain players. To do so, select the Whitelist Manager tab:");
			addGuidebook("chapter.missiledefense.ciwsturret3", "There, you can enter the name of the players you wish to whitelist to the turret. Note the name of the player who places the turret is added automatically. Bullets deal 10 damage upon impact!");
			addGuidebook("chapter.missiledefense.ciwsturret4", "While you may not be able to whitelist certain mobs, the turret can have its firing mode programmed to include or exclude mobs when searching for a target. This can be selected via the \"Targeting Mode\" tab:");

			addGuidebook("chapter.missiledefense.laserturret1", "The Laser Turret offers an alternative to the CIWS and SAM turrets, as it does not utilize ammunition. However this comes at the cost of being incredibly power hungry. The turret deals 1 damage/tick, however this is dependent on range. The further a target is from the turret, the less damage it will deal. The turret "
					+ "has a range of %s blocks, however this cannot be upgraded. It has no minimum engagement range. The turret has a maximum elevation of 90 degrees and a maximum depression of 45 degrees.");
			addGuidebook("chapter.missiledefense.laserturret2", "On top of being incredibly power hungry, the turret also has a heat buffer that builds up while firing. If the temperature of the turret reaches past a certain point, it will need to cool down before it can engage targets again! Like with the CIWS, the Laser Turret can attack entities and players within a quarter of its "
					+ "missile targeting range. Mobs and players are additionally set on fire when damaged by the laser turret. It must have a clear line of site to the mob also.");

			addGuidebook("chapter.missiledefense.railgunturret1", "The Railgun Turret is a hybrid between the CIWS and SAM turrets. It fires Steel Rods that destroy a missile on contact. It is able to engage missiles up to %2$s blocks, and this range can be increased with range upgrades. It has no minimum engagement range. The turret must wait %3$s ticks between shots, and can "
					+ "only hold up to 64 rods at a time. Like with the Laser Turret, it is incredibly power-hungry. The turret has a maximum elevation of 45 degrees and a maximum depression of 45 degrees.");
			addGuidebook("chapter.missiledefense.railgunturret2", "Like with the CIWS and Laser Turrets, the Railgun Turret can engage players and mobs up to one quarter the distance of its missile engagement range. The steel rod will deal 20 damage on impact, but the turret must have a clear line of site to engage!");

			addGuidebook("chapter.missiledefense.missilesilo", "Missile Silo");
			addGuidebook("chapter.missiledefense.missilesilo1", "Another method of missile defense altogether is to repurpose a missile silo to fire %1$ss. The Smart Missile MK2 is capable of reaching higher speeds than its MK1 counterpart, and boasts a %2$s chance to destroy a missile! However, the silo can only hold five at a time, and must be linked to a Fire Control Radar. " +
					"To link a missile silo to a radar, enter the radar's XYZ coordinates into the silo where the target coordinates typically go. The MK2 can only be fired with a redstone signal, and the explosives slot must be empty. The Fire Control Radar will emit a comparator signal of 15 if actively tracking a target. However, as with the MK1, a MK2 can only be fired if the incoming missile " +
					"is more than 100 blocks away. It should be noted that the MK2, while faster, cannot turn as fast due to its size.");

			addJei("info.item.missilecloserange", "Specs:\n    Range: 3000 Blocks");
			addJei("info.item.missilemediumrange", "Specs:\n    Range: 10 000 Blocks");
			addJei("info.item.missilelongrange", "Specs:\n    Range: Unlimited");

		}

	}

}
