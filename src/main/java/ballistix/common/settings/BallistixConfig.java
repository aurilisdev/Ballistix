package ballistix.common.settings;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BallistixConfig {
    public static BallistixConfig INSTANCE;

    public ModConfigSpec SPEC;

    // Explosives
    public ModConfigSpec.DoubleValue EXPLOSIVE_ANTIMATTER_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ANTIMATTER_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_EMP_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_LARGEANTIMATTER_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_LARGEANTIMATTER_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ATTRACTIVE_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_REPULSIVE_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH;
    public ModConfigSpec.DoubleValue EXPLOSIVE_BREACHING_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CHEMICAL_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CHEMICAL_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CONDENSIVE_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CONTAGIOUS_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CONTAGIOUS_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DARKMATTER_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DARKMATTER_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DEBILITATION_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DEBILITATION_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT;
    public ModConfigSpec.DoubleValue EXPLOSIVE_INCENDIARY_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_ENERGY;
    public ModConfigSpec.DoubleValue EXPLOSIVE_OBSIDIAN_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT;
    public ModConfigSpec.DoubleValue EXPLOSIVE_THERMOBARIC_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_THERMOBARIC_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_THERMOBARIC_ENERGY;

    // Missile silo / rocket
    public ModConfigSpec.DoubleValue MISSILESILO_USAGE;
    public ModConfigSpec.IntValue ROCKET_LAUNCHER_COOLDOWN_TICKS;

    // ESM tower
    public ModConfigSpec.DoubleValue ESM_TOWER_USAGE_PER_TICK;
    public ModConfigSpec.DoubleValue ESM_TOWER_SEARCH_RADIUS;

    // Radars
    public ModConfigSpec.DoubleValue RADAR_USAGE;
    public ModConfigSpec.IntValue RADAR_RANGE;
    public ModConfigSpec.DoubleValue FIRE_CONTROL_RADAR_USAGE;
    public ModConfigSpec.IntValue FIRE_CONTROL_RADAR_RANGE;
    public ModConfigSpec.DoubleValue MAX_DISTANCE_FROM_RADAR;

    // SAM
    public ModConfigSpec.DoubleValue SAM_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue SAM_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue SAM_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.IntValue SAM_TURRET_COOLDOWN;
    public ModConfigSpec.DoubleValue SAM_ENTITY_TURNINGSPEEDRADIANS;
    public ModConfigSpec.DoubleValue SAM_TOP_SPEED;
    public ModConfigSpec.DoubleValue SAM_ACCELERATION;
    public ModConfigSpec.DoubleValue SAM_MINTURNSPEED_PERC;
    public ModConfigSpec.DoubleValue SAM_CHANCE_TO_DESTROY;

    // ABM
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_ENTITY_TURNINGSPEEDRADIANS;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_TOP_SPEED;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_ACCELERATION;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_MINTURNSPEED_PERC;

    // CIWS
    public ModConfigSpec.DoubleValue CIWS_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue CIWS_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue CIWS_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.DoubleValue CIWS_INNACCURACY;

    // Laser
    public ModConfigSpec.DoubleValue LASER_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue LASER_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue LASER_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.DoubleValue LASER_TURRET_MAXHEAT;
    public ModConfigSpec.DoubleValue LASER_TURRET_COOLTHRESHHOLD;
    public ModConfigSpec.DoubleValue LASER_TURRET_BASE_DAMAGE;

    // Railgun
    public ModConfigSpec.DoubleValue RAILGUN_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue RAILGUN_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue RAILGUN_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.IntValue RAILGUN_TURRET_COOLDOWN;
    public ModConfigSpec.DoubleValue RAILGUN_INNACCURACY;

    // Upgrades / misc
    public ModConfigSpec.DoubleValue RANGE_INCREASE_INACCURACY_MULTIPLIER;

    // Missiles / launchers
    public ModConfigSpec.IntValue MISSILE_HEALTH;
    public ModConfigSpec.IntValue LAUNCHER_PLATFORM_RANGE_T1;
    public ModConfigSpec.IntValue LAUNCHER_PLATFORM_RANGE_T2;
    public ModConfigSpec.IntValue LAUNCHER_PLATFORM_RANGE_T3;
    public ModConfigSpec.IntValue LAUNCH_PLATFORM_DEFAULT_INACCURACY;

    // Cache / tools
    public ModConfigSpec.BooleanValue SHOULD_CACHE_EXPLOSIONS;
    public ModConfigSpec.IntValue LASER_DESIGNATOR_RANGE;

    public BallistixConfig() {
	ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

	// Explosives
	builder.push("explosives");
	EXPLOSIVE_ANTIMATTER_RADIUS = builder.comment("Default radius for antimatter explosive.")
		.defineInRange("explosive_antimatter_radius", 45.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_ANTIMATTER_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_antimatter_duration", 80.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_EMP_RADIUS = builder.defineInRange("explosive_emp_radius", 45.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_LARGEANTIMATTER_RADIUS = builder.defineInRange("explosive_largeantimatter_radius", 100.0, 0.0,
		Double.MAX_VALUE);
	EXPLOSIVE_LARGEANTIMATTER_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_largeantimatter_duration", 200.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_ATTRACTIVE_SIZE = builder.defineInRange("explosive_attractive_size", 1.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_REPULSIVE_SIZE = builder.defineInRange("explosive_repulsive_size", 1.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH = builder
		.defineInRange("explosive_attractive_repulsive_push_strength", 2.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_BREACHING_SIZE = builder.defineInRange("explosive_breaching_size", 10.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_CHEMICAL_SIZE = builder.defineInRange("explosive_chemical_size", 7.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_CHEMICAL_DURATION = builder.comment("Value is in ticks.").defineInRange("explosive_chemical_duration",
		1200.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_CONDENSIVE_SIZE = builder.defineInRange("explosive_condensive_size", 2.5, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_CONTAGIOUS_SIZE = builder.defineInRange("explosive_contagious_size", 7.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_CONTAGIOUS_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_contagious_duration", 1200.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_DARKMATTER_RADIUS = builder.defineInRange("explosive_darkmatter_radius", 50.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_DARKMATTER_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_darkmatter_duration", 1200.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_DEBILITATION_SIZE = builder.defineInRange("explosive_debilitation_size", 7.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_DEBILITATION_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_debilitation_duration", 1200.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT = builder.defineInRange("explosive_fragmentation_shrapnel_count", 50.0,
		0.0, Double.MAX_VALUE);
	EXPLOSIVE_INCENDIARY_RADIUS = builder.defineInRange("explosive_incendiary_radius", 7.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_SIZE = builder.defineInRange("explosive_nuclear_size", 45.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_DURATION = builder.comment("Value is in ticks.").defineInRange("explosive_nuclear_duration",
		360.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_ENERGY = builder.defineInRange("explosive_nuclear_energy", 120.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_OBSIDIAN_SIZE = builder.defineInRange("explosive_obsidian_size", 10.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT = builder.defineInRange("explosive_shrapnel_shrapnel_count", 25.0, 0.0,
		Double.MAX_VALUE);
	EXPLOSIVE_THERMOBARIC_SIZE = builder.defineInRange("explosive_thermobaric_size", 30.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_THERMOBARIC_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_thermobaric_duration", 45.0, 0.0, Double.MAX_VALUE);
	EXPLOSIVE_THERMOBARIC_ENERGY = builder.defineInRange("explosive_thermobaric_energy", 60.0, 0.0,
		Double.MAX_VALUE);
	builder.pop();

	// Missile silo / rocket
	builder.push("missile_silo");
	MISSILESILO_USAGE = builder.comment("Usage per launch in joules").defineInRange("missilesilo_usage", 10000.0,
		0.0, Double.MAX_VALUE);
	ROCKET_LAUNCHER_COOLDOWN_TICKS = builder.defineInRange("rocket_launcher_cooldown_ticks", 60, 0,
		Integer.MAX_VALUE);
	builder.pop();

	// ESM Tower
	builder.push("esm_tower");
	ESM_TOWER_USAGE_PER_TICK = builder.defineInRange("esm_tower_usage_per_tick", 1000.0, 0.0, Double.MAX_VALUE);
	ESM_TOWER_SEARCH_RADIUS = builder.defineInRange("esm_tower_search_radius", 200.0, 0.0, Double.MAX_VALUE);
	builder.pop();

	// Radars
	builder.push("radar");
	RADAR_USAGE = builder.comment("Usage in watt for radar").defineInRange("radar_usage", 10000.0, 0.0,
		Double.MAX_VALUE);
	RADAR_RANGE = builder.defineInRange("radar_range", 600, 0, Integer.MAX_VALUE);
	FIRE_CONTROL_RADAR_USAGE = builder.comment("Usage in watt for fire control radar")
		.defineInRange("fire_control_radar_usage", 10000.0, 0.0, Double.MAX_VALUE);
	FIRE_CONTROL_RADAR_RANGE = builder.defineInRange("fire_control_radar_range", 550, 0, Integer.MAX_VALUE);
	MAX_DISTANCE_FROM_RADAR = builder.comment("How far a turret can be from a fire control radar")
		.defineInRange("max_distance_from_radar", 32.0, 0.0, Double.MAX_VALUE);
	builder.pop();

	// SAM
	builder.push("sam");
	SAM_TURRET_USAGEPERTICK = builder.defineInRange("sam_turret_usagepertick", 1000.0, 0.0, Double.MAX_VALUE);
	SAM_TURRET_BASE_RANGE = builder.defineInRange("sam_turret_base_range", 300.0, 0.0, Double.MAX_VALUE);
	SAM_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("sam_turret_rotationspeedradians", 0.2, 0.0,
		Double.MAX_VALUE);
	SAM_TURRET_COOLDOWN = builder.defineInRange("sam_turret_cooldown", 100, 0, Integer.MAX_VALUE);
	SAM_ENTITY_TURNINGSPEEDRADIANS = builder.defineInRange("sam_entity_turningspeedradians", 0.1, 0.0,
		Double.MAX_VALUE);
	SAM_TOP_SPEED = builder.defineInRange("sam_top_speed", 3.0, 0.0, Double.MAX_VALUE);
	SAM_ACCELERATION = builder.defineInRange("sam_acceleration", 0.04, 0.0, Double.MAX_VALUE);
	SAM_MINTURNSPEED_PERC = builder.defineInRange("sam_minturnspeed_perc", 0.25, 0.0, 1.0);
	SAM_CHANCE_TO_DESTROY = builder.defineInRange("sam_chance_to_destroy", 0.5, 0.0, 1.0);
	builder.pop();

	// Anti-ballistic missile
	builder.push("abm");
	ANTIBALLISTICMISSILE_ENTITY_TURNINGSPEEDRADIANS = builder
		.defineInRange("antiballisticmissile_entity_turningspeedradians", 0.05, 0.0, Double.MAX_VALUE);
	ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY = builder.defineInRange("antiballisticmissile_chance_to_destroy", 0.85,
		0.0, 1.0);
	ANTIBALLISTICMISSILE_TOP_SPEED = builder.defineInRange("antiballisticmissile_top_speed", 5.0, 0.0,
		Double.MAX_VALUE);
	ANTIBALLISTICMISSILE_ACCELERATION = builder.defineInRange("antiballisticmissile_acceleration", 0.01, 0.0,
		Double.MAX_VALUE);
	ANTIBALLISTICMISSILE_MINTURNSPEED_PERC = builder.defineInRange("antiballisticmissile_minturnspeed_perc", 0.15,
		0.0, 1.0);
	builder.pop();

	// CIWS
	builder.push("ciws");
	CIWS_TURRET_USAGEPERTICK = builder.defineInRange("ciws_turret_usagepertick", 1000.0, 0.0, Double.MAX_VALUE);
	CIWS_TURRET_BASE_RANGE = builder.defineInRange("ciws_turret_base_range", 100.0, 0.0, Double.MAX_VALUE);
	CIWS_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("ciws_turret_rotationspeedradians", 0.6, 0.0,
		Double.MAX_VALUE);
	CIWS_INNACCURACY = builder.defineInRange("ciws_innaccuracy", 0.05, 0.0, Double.MAX_VALUE);
	builder.pop();

	// Laser
	builder.push("laser");
	LASER_TURRET_USAGEPERTICK = builder.defineInRange("laser_turret_usagepertick", 10000.0, 0.0, Double.MAX_VALUE);
	LASER_TURRET_BASE_RANGE = builder.defineInRange("laser_turret_base_range", 300.0, 0.0, Double.MAX_VALUE);
	LASER_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("laser_turret_rotationspeedradians", 0.2, 0.0,
		Double.MAX_VALUE);
	LASER_TURRET_MAXHEAT = builder.defineInRange("laser_turret_maxheat", 100.0, 0.0, Double.MAX_VALUE);
	LASER_TURRET_COOLTHRESHHOLD = builder.defineInRange("laser_turret_coolthreshhold", 40.0, 0.0, Double.MAX_VALUE);
	LASER_TURRET_BASE_DAMAGE = builder.defineInRange("laser_turret_base_damage", 1.0, 0.0, Double.MAX_VALUE);
	builder.pop();

	// Railgun
	builder.push("railgun");
	RAILGUN_TURRET_USAGEPERTICK = builder.defineInRange("railgun_turret_usagepertick", 10000.0, 0.0,
		Double.MAX_VALUE);
	RAILGUN_TURRET_BASE_RANGE = builder.defineInRange("railgun_turret_base_range", 300.0, 0.0, Double.MAX_VALUE);
	RAILGUN_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("railgun_turret_rotationspeedradians", 0.1, 0.0,
		Double.MAX_VALUE);
	RAILGUN_TURRET_COOLDOWN = builder.defineInRange("railgun_turret_cooldown", 100, 0, Integer.MAX_VALUE);
	RAILGUN_INNACCURACY = builder.defineInRange("railgun_innaccuracy", 0.05, 0.0, Double.MAX_VALUE);
	builder.pop();

	// Misc
	builder.push("misc");
	RANGE_INCREASE_INACCURACY_MULTIPLIER = builder.comment(
		"The amount of inaccuracy each range upgrade will add to the turret. Affects shots past the base range. Set to 1 to disable.")
		.defineInRange("range_increase_inaccuracy_multiplier", 1.001, 1.0, Double.MAX_VALUE);
	MISSILE_HEALTH = builder.defineInRange("missile_health", 100, 0, Integer.MAX_VALUE);
	LAUNCHER_PLATFORM_RANGE_T1 = builder.defineInRange("launcher_platform_range_t1", 500, 0, Integer.MAX_VALUE);
	LAUNCHER_PLATFORM_RANGE_T2 = builder.defineInRange("launcher_platform_range_t2", 3000, 0, Integer.MAX_VALUE);
	LAUNCHER_PLATFORM_RANGE_T3 = builder.defineInRange("launcher_platform_range_t3", 10000, 0, Integer.MAX_VALUE);
	LAUNCH_PLATFORM_DEFAULT_INACCURACY = builder.defineInRange("launch_platform_default_inaccuracy", 45, 0,
		Integer.MAX_VALUE);
	SHOULD_CACHE_EXPLOSIONS = builder.comment("Whether explosions will be cached; may use a lot of memory!")
		.define("should_cache_explosions", true);
	LASER_DESIGNATOR_RANGE = builder.defineInRange("laser_designator_range", 1000, 0, Integer.MAX_VALUE);
	builder.pop();

	SPEC = builder.build();
    }
}
