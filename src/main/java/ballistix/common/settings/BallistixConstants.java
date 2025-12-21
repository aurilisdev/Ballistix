package ballistix.common.settings;

import voltaic.api.configuration.*;

@Configuration(name = "Ballistix")
public class BallistixConstants {

	/* EXPLOSIVES */

	@BooleanValue(def = true, comment = "Whether explosions will be cached; may use a lot of memory!")
	public static boolean SHOULD_CACHE_EXPLOSIONS = true;

	// Tier 1

	//Obsidian
	@DoubleValue(def = 10.0)
	public static double EXPLOSIVE_OBSIDIAN_SIZE = 10.0;
	//Condensive
	@DoubleValue(def = 2.5)
	public static double EXPLOSIVE_CONDENSIVE_SIZE = 2.5;
	//Attractive
	@DoubleValue(def = 1.0)
	public static double EXPLOSIVE_ATTRACTIVE_SIZE = 1.0;
	@DoubleValue(def = 2.0)
	public static double EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH = 2.0;
	//repulsive
	@DoubleValue(def = 1.0)
	public static double EXPLOSIVE_REPULSIVE_SIZE = 1.0;
	//incendiary
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_INCENDIARY_RADIUS = 7.0;
	//shrapnel
	@DoubleValue(def = 25.0)
	public static double EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT = 25.0;
	//chemical
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_CHEMICAL_SIZE = 7.0;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_CHEMICAL_DURATION = 1200.0;
	//anvil
	@IntValue(def = 20)
	public static int EXPLOSIVE_ANVIL_ANVILSPERBLAST = 10;
	//infestive
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_INFESTIVE_RADIUS = 7;
	@DoubleValue(def = 20.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_INFESTIVE_DURATION = 20.0;
	//debilitation
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_DEBILITATION_SIZE = 7.0;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_DEBILITATION_DURATION = 1200.0;

	// TIER 2

	//fragmentation
	@DoubleValue(def = 50.0)
	public static double EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT = 50.0;
	//contagious
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_CONTAGIOUS_SIZE = 7.0;
	@IntValue(def = 10)
	public static int VIRUS_EFFECT_RADIUS = 10;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_CONTAGIOUS_DURATION = 1200.0;
	//breaching
	@DoubleValue(def = 5.0)
	public static double EXPLOSIVE_BREACHING_SIZE = 5.0;
	@DoubleValue(def = 1, comment = "Value is in ticks.")
	public static double EXPLOSIVE_BREACHING_DURATION = 1;
	@DoubleValue(def = 60.0)
	public static double EXPLOSIVE_BREACHING_ENERGY = 60.0;
	//thermobaric
	@DoubleValue(def = 30.0)
	public static double EXPLOSIVE_THERMOBARIC_SIZE = 30.0;
	@DoubleValue(def = 45.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_THERMOBARIC_DURATION = 45.0;
	@DoubleValue(def = 60.0)
	public static double EXPLOSIVE_THERMOBARIC_ENERGY = 60.0;
	//sonic
	@DoubleValue(def = 2.0)
	public static double EXPLOSIVE_SONIC_MAXHARDNESS = 2.0;
	@DoubleValue(def = 15.0)
	public static double EXPLOSIVE_SONIC_RADIUS = 20;
	@DoubleValue(def = 5.0)
	public static double EXPLOSIVE_SONIC_VELOCITY = 5.0;
	@DoubleValue(def = 20.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_SONIC_DURATION = 20.0;

	// TIER 3

	//antigravity
	@IntValue(def = 3)
	public static int EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS = 3;
	@IntValue(def = 12000, comment = "Value is in ticks.")
	public static int EXPLOSIVE_ANTIGRAVITY_CHUNKDURATION = 12000;
	@DoubleValue(def = 1.1)
	public static double EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR = 1.1;
	@IntValue(def = 300)
	public static int EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT = 300;
	@IntValue(def = 1, comment = "It is recommended to keep this value low")
	public static int EXPLOSIVE_ANTIGRAVITY_MAXBLOCKCHECKS = 1;
	//emp
	@DoubleValue(def = 45.0)
	public static double EXPLOSIVE_EMP_RADIUS = 45.0;
	//nuclear
	@DoubleValue(def = 45.0)
	public static double EXPLOSIVE_NUCLEAR_SIZE = 45.0;
	@DoubleValue(def = 360.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_NUCLEAR_DURATION = 360.0;
	@DoubleValue(def = 120.0)
	public static double EXPLOSIVE_NUCLEAR_ENERGY = 120.0;
	@DoubleValue(def = 90.0)
	public static double EXPLOSIVE_NUCLEAR_RADIATION_RADIUS = 90.0;
	//endothermic
	@DoubleValue(def = 1)
	public static double EXPLOSIVE_ENDOTHERMIC_MAXHARDNESS = 1;
	@DoubleValue(def = 30.0)
	public static double EXPLOSIVE_ENDOTHERMIC_RADIUS = 30;
	@DoubleValue(def = 5.0)
	public static double EXPLOSIVE_ENDOTHERMIC_VELOCITY = 5.0;
	@DoubleValue(def = 20.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_ENDOTHERMIC_DURATION = 20.0;
	//exothermic
	@DoubleValue(def = 2)
	public static double EXPLOSIVE_EXOTHERMIC_MAXHARDNESS = 2;
	@DoubleValue(def = 40.0)
	public static double EXPLOSIVE_EXOTHERMIC_RADIUS = 40;
	@DoubleValue(def = 0.01)
	public static double EXPLOSIVE_EXOTHERMIC_CHANCE_FOR_LAVA = 0.01;
	@DoubleValue(def = 0.3)
	public static double EXPLOSIVE_EXOTHERMIC_CHANCE_TO_BURN = 0.3;
	@DoubleValue(def = 20.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_EXOTHERMIC_DURATION = 20.0;
	//ender
	@DoubleValue(def = 9.0)
	public static double EXPLOSIVE_ENDER_RADIUS = 9.0;
	@IntValue(def = 10)
	public static int EXPLOSIVE_ENDER_ENDERMANCOUNT = 10;
	//hypersonic
	@DoubleValue(def = 3.0)
	public static double EXPLOSIVE_HYPERSONIC_MAXHARDNESS = 3.0;
	@DoubleValue(def = 30.0)
	public static double EXPLOSIVE_HYPERSONIC_RADIUS = 30;
	@DoubleValue(def = 5.0)
	public static double EXPLOSIVE_HYPERSONIC_VELOCITY = 5.0;
	@DoubleValue(def = 160.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_HYPERSONIC_DURATION = 160.0;
	//rejuvination
	//antimatter
	@DoubleValue(def = 45.0)
	public static double EXPLOSIVE_ANTIMATTER_RADIUS = 45.0;
	@DoubleValue(def = 80.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_ANTIMATTER_DURATION = 80.0;
	//large antimatter
	@DoubleValue(def = 100.0)
	public static double EXPLOSIVE_LARGEANTIMATTER_RADIUS = 100.0;
	@DoubleValue(def = 200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_LARGEANTIMATTER_DURATION = 200.0;
	//dark matter
	@DoubleValue(def = 50.0)
	public static double EXPLOSIVE_DARKMATTER_RADIUS = 50.0;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_DARKMATTER_DURATION = 1200.0;
	@IntValue(def = 1200, comment = "How many iterations the blast will perform. Set to -1 for infinite, 0 for none.")
	public static int EXPLOSIVE_DARKMATTER_PERSISTANCE = 1200;
	@IntValue(def = 5, comment = "Value is in ticks.")
	public static int EXPLOSIVE_DARKMATTER_MOVEMENTTICKS = 5;
	@IntValue(def = 1, comment = "Value is in ticks.")
	public static int EXPLOSIVE_DARKMATTER_REPEATDURATION = 1;


	/* MISSILE SILO */

	@DoubleValue(def = 10000, comment = "Usage per launch in joules")
	public static double MISSILESILO_USAGE = 10000;
	@IntValue(def = 100)
	public static int MISSILE_HEALTH = 100;
	@IntValue(def = 500)
	public static int LAUNCHER_PLATFORM_RANGE_T1 = 500;
	@IntValue(def = 3000)
	public static int LAUNCHER_PLATFORM_RANGE_T2 = 3000;
	@IntValue(def = 10000)
	public static int LAUNCHER_PLATFORM_RANGE_T3 = 10000;
	@IntValue(def = 500)
	public static int VLS_RANGE = 500;
	@IntValue(def = 45)
	public static int LAUNCH_PLATFORM_DEFAULT_INACCURACY = 45;


	/* ESM TOWER */

	@DoubleValue(def = 1000.0)
	public static double ESM_TOWER_USAGE_PER_TICK = 1000.0;
	@DoubleValue(def = 100)
	public static double ESM_TOWER_SEARCH_RADIUS = 200.0;

	/* SEARCH RADAR */

	@DoubleValue(def = 10000, comment = "Usage in watt for radar")
	public static double RADAR_USAGE = 10000;
	@IntValue(def = 600)
	public static int RADAR_RANGE = 600;

	/* FIRE CONTROL RADAR */

	@DoubleValue(def = 10000, comment = "Usage in watt for fire control radar")
	public static double FIRE_CONTROL_RADAR_USAGE = 10000;
	@IntValue(def = 550)
	public static int FIRE_CONTROL_RADAR_RANGE = 550;
	@DoubleValue(def = 32.0, comment = "How far a turret can be from a fire control radar")
	public static double MAX_DISTANCE_FROM_RADAR = 32.0;

	/* SAM TURRET */

	@DoubleValue(def = 1000)
	public static double SAM_TURRET_USAGEPERTICK = 1000;
	@DoubleValue(def = 300)
	public static double SAM_TURRET_BASE_RANGE = 300;
	@DoubleValue(def = 0.2)
	public static double SAM_TURRET_ROTATIONSPEEDRADIANS = 0.2;
	@IntValue(def = 100)
	public static int SAM_TURRET_COOLDOWN = 100;

	/* SAM ENTITY */

	@DoubleValue(def = 0.1)
	public static double SAM_ENTITY_TURNINGSPEEDRADIANS = 0.1;
	@FloatValue(def = 3.0F)
	public static float SAM_TOP_SPEED = 3.0F;
	@FloatValue(def = 0.04F)
	public static float SAM_ACCELERATION = 0.04F;
	@FloatValue(def = 0.25F)
	public static float SAM_MINTURNSPEED_PERC = 0.25F;
	@DoubleValue(def = 0.5)
	public static double SAM_CHANCE_TO_DESTROY = 0.5;

	/* ANTI-BALLISTIC ENTITY */

	@DoubleValue(def = 0.05)
	public static double ANTIBALLISTICMISSILE_ENTITY_TURNINGSPEEDRADIANS = 0.05;
	@DoubleValue(def = 0.85)
	public static double ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY = 0.85;
	@FloatValue(def = 5.0F)
	public static float ANTIBALLISTICMISSILE_TOP_SPEED = 5.0F;
	@FloatValue(def = 0.04F)
	public static float ANTIBALLISTICMISSILE_ACCELERATION = 0.04F;
	@FloatValue(def = 0.15F)
	public static float ANTIBALLISTICMISSILE_MINTURNSPEED_PERC = 0.15F;

	@DoubleValue(def = 1.01, comment = "The amount of inaccuracy each range upgrade will add to the turret. Affects shots past the base range. Set to 1 to disable.")
	public static double RANGE_INCREASE_INACCURACY_MULTIPLIER = 1.001;

	/* CIWS TURRET */

	@DoubleValue(def = 1000)
	public static double CIWS_TURRET_USAGEPERTICK = 1000;
	@DoubleValue(def = 100)
	public static double CIWS_TURRET_BASE_RANGE = 100;
	@DoubleValue(def = 0.6)
	public static double CIWS_TURRET_ROTATIONSPEEDRADIANS = 0.6;
	@DoubleValue(def = 0.05)
	public static double CIWS_INNACCURACY = 0.05;

	/* LASER TURRET */

	@DoubleValue(def = 10000)
	public static double LASER_TURRET_USAGEPERTICK = 10000;
	@DoubleValue(def = 300)
	public static double LASER_TURRET_BASE_RANGE = 300;
	@DoubleValue(def = 0.2)
	public static double LASER_TURRET_ROTATIONSPEEDRADIANS = 0.2;
	@DoubleValue(def = 100)
	public static double LASER_TURRET_MAXHEAT = 100;
	@DoubleValue(def = 40)
	public static double LASER_TURRET_COOLTHRESHHOLD = 40;
	@DoubleValue(def = 1)
	public static double LASER_TURRET_BASE_DAMAGE = 1;

	/* RAILGUN TURRET */

	@DoubleValue(def = 10000)
	public static double RAILGUN_TURRET_USAGEPERTICK = 10000;
	@DoubleValue(def = 300)
	public static double RAILGUN_TURRET_BASE_RANGE = 300;
	@DoubleValue(def = 0.1)
	public static double RAILGUN_TURRET_ROTATIONSPEEDRADIANS = 0.1;
	@IntValue(def = 100)
	public static int RAILGUN_TURRET_COOLDOWN = 100;
	@DoubleValue(def = 0.05)
	public static double RAILGUN_INNACCURACY = 0.05;

	/* PROXIMITY DETECTOR */

	@DoubleValue(def = 100)
	public static double PROXIMITYDETECTOR_USAGEPERTICK = 100;


	/* ITEMS */

	@IntValue(def = 60)
	public static int ROCKET_LAUNCHER_COOLDOWN_TICKS = 60;
	@IntValue(def = 1000)
	public static int LASER_DESIGNATOR_RANGE = 1000;

}
