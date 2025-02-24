package ballistix.common.settings;

import electrodynamics.api.configuration.BooleanValue;
import electrodynamics.api.configuration.Configuration;
import electrodynamics.api.configuration.DoubleValue;
import electrodynamics.api.configuration.IntValue;

@Configuration(name = "Ballistix")
public class Constants {
	@DoubleValue(def = 45.0)
	public static double EXPLOSIVE_ANTIMATTER_RADIUS = 45.0;
	@DoubleValue(def = 20.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_ANTIMATTER_DURATION = 10.0;
	@DoubleValue(def = 45.0)
	public static double EXPLOSIVE_EMP_RADIUS = 45.0;
	@DoubleValue(def = 80.0)
	public static double EXPLOSIVE_LARGEANTIMATTER_RADIUS = 80.0;
	@DoubleValue(def = 200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_LARGEANTIMATTER_DURATION = 30.0;
	@DoubleValue(def = 1.0)
	public static double EXPLOSIVE_ATTRACTIVE_SIZE = 1.0;
	@DoubleValue(def = 1.0)
	public static double EXPLOSIVE_REPULSIVE_SIZE = 1.0;
	@DoubleValue(def = 2.0)
	public static double EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH = 2.0;
	@DoubleValue(def = 10.0)
	public static double EXPLOSIVE_BREACHING_SIZE = 10.0;
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_CHEMICAL_SIZE = 7.0;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_CHEMICAL_DURATION = 1200.0;
	@DoubleValue(def = 2.5)
	public static double EXPLOSIVE_CONDENSIVE_SIZE = 2.5;
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_CONTAGIOUS_SIZE = 7.0;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_CONTAGIOUS_DURATION = 1200.0;
	@DoubleValue(def = 50.0)
	public static double EXPLOSIVE_DARKMATTER_RADIUS = 50.0;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_DARKMATTER_DURATION = 1200.0;
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_DEBILITATION_SIZE = 7.0;
	@DoubleValue(def = 1200.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_DEBILITATION_DURATION = 1200.0;
	@DoubleValue(def = 50.0)
	public static double EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT = 50.0;
	@DoubleValue(def = 7.0)
	public static double EXPLOSIVE_INCENDIARY_RADIUS = 7.0;
	@DoubleValue(def = 35.0)
	public static double EXPLOSIVE_NUCLEAR_SIZE = 35.0;
	@DoubleValue(def = 30.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_NUCLEAR_DURATION = 30.0;
	@DoubleValue(def = 80.0)
	public static double EXPLOSIVE_NUCLEAR_ENERGY = 80.0;
	@DoubleValue(def = 10.0)
	public static double EXPLOSIVE_OBSIDIAN_SIZE = 10.0;
	@DoubleValue(def = 25.0)
	public static double EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT = 25.0;
	@DoubleValue(def = 20.0)
	public static double EXPLOSIVE_THERMOBARIC_SIZE = 20.0;
	@DoubleValue(def = 15.0, comment = "Value is in ticks.")
	public static double EXPLOSIVE_THERMOBARIC_DURATION = 15.0;
	@DoubleValue(def = 45.0)
	public static double EXPLOSIVE_THERMOBARIC_ENERGY = 45.0;
	@DoubleValue(def = 10000, comment = "Usage per launch in joules")
	public static double MISSILESILO_USAGE = 10000;
	@IntValue(def = 60)
	public static int ROCKET_LAUNCHER_COOLDOWN_TICKS = 60;

	@DoubleValue(def = 1000.0)
	public static double ESM_TOWER_USAGE_PER_TICK = 1000.0;
	@DoubleValue(def = 100)
	public static double ESM_TOWER_SEARCH_RADIUS = 100.0;

	@DoubleValue(def = 10000, comment = "Usage in watt for radar")
	public static double RADAR_USAGE = 10000;
	@DoubleValue(def = 600)
	public static int RADAR_RANGE = 600;

	@DoubleValue(def = 10000, comment = "Usage in watt for fire control radar")
	public static double FIRE_CONTROL_RADAR_USAGE = 10000;
	@DoubleValue(def = 550)
	public static int FIRE_CONTROL_RADAR_RANGE = 550;
	@DoubleValue(def = 32.0, comment = "How far a turret can be from a fire control radar")
	public static double MAX_DISTANCE_FROM_RADAR = 32.0;

	@DoubleValue(def = 1000)
	public static double SAM_TURRET_USAGEPERTICK = 1000;
	@DoubleValue(def = 300)
	public static double SAM_TURRET_BASE_RANGE = 300;
	@DoubleValue(def = 0.2)
	public static double SAM_TURRET_ROTATIONSPEEDRADIANS = 0.2;
	@IntValue(def = 100)
	public static int SAM_TURRET_COOLDOWN = 100;
	@DoubleValue(def = 0.02)
	public static double SAM_INNACCURACY = 0.02;

	@DoubleValue(def = 1000)
	public static double CIWS_TURRET_USAGEPERTICK = 1000;
	@DoubleValue(def = 100)
	public static double CIWS_TURRET_BASE_RANGE = 100;
	@DoubleValue(def = 0.6)
	public static double CIWS_TURRET_ROTATIONSPEEDRADIANS = 0.6;
	@DoubleValue(def = 0.05)
	public static double CIWS_INNACCURACY = 0.05;

	@DoubleValue(def = 10000)
	public static double LASER_TURRET_USAGEPERTICK = 10000;
	@DoubleValue(def = 100)
	public static double LASER_TURRET_BASE_RANGE = 300;
	@DoubleValue(def = 0.2)
	public static double LASER_TURRET_ROTATIONSPEEDRADIANS = 0.2;
	@DoubleValue(def = 100)
	public static double LASER_TURRET_MAXHEAT = 100;
	@DoubleValue(def = 40)
	public static double LASER_TURRET_COOLTHRESHHOLD = 40;
	@DoubleValue(def = 1)
	public static double LASER_TURRET_BASE_DAMAGE = 1;

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


	@DoubleValue(def = 1.01, comment = "The amount of inaccuracy each range upgrade will add to the turret. Affects shots past the base range. Set to 1 to disable.")
	public static double RANGE_INCREASE_INACCURACY_MULTIPLIER = 1.001;

	@IntValue(def = 100)
	public static int MISSILE_HEALTH = 100;
	@IntValue(def = 3000, comment = "Set to -1 for unlimited range")
	public static int CLOSERANGE_MISSILE_RANGE = 3000;
	@IntValue(def = 10000, comment = "Set to -1 for unlimited range")
	public static int MEDIUMRANGE_MISSILE_RANGE = 10000;
	@IntValue(def = -1, comment = "Set to -1 for unlimited range")
	public static int LONGRANGE_MISSILE_RANGE = -1;
	@BooleanValue(def = true, comment = "Whether explosions will be cached; may use a lot of memory!")
	public static boolean SHOULD_CACHE_EXPLOSIONS = true;

}
