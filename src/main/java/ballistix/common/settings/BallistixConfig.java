package ballistix.common.settings;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BallistixConfig {
    public static BallistixConfig INSTANCE;

    public ModConfigSpec SPEC;

    /* === Fields === */

    public ModConfigSpec.BooleanValue SHOULD_CACHE_EXPLOSIONS;
    public ModConfigSpec.BooleanValue SHOULD_MULTITHREAD_RAYTRACING;

    // Tier 1
    public ModConfigSpec.DoubleValue EXPLOSIVE_OBSIDIAN_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CONDENSIVE_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ATTRACTIVE_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH;
    public ModConfigSpec.DoubleValue EXPLOSIVE_REPULSIVE_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_INCENDIARY_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CHEMICAL_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CHEMICAL_DURATION;
    public ModConfigSpec.IntValue EXPLOSIVE_ANVIL_ANVILSPERBLAST;
    public ModConfigSpec.DoubleValue EXPLOSIVE_INFESTIVE_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_INFESTIVE_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DEBILITATION_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DEBILITATION_DURATION;

    // Tier 2
    public ModConfigSpec.DoubleValue EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CONTAGIOUS_SIZE;
    public ModConfigSpec.IntValue VIRUS_EFFECT_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_CONTAGIOUS_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_BREACHING_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_BREACHING_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_BREACHING_ENERGY;
    public ModConfigSpec.DoubleValue EXPLOSIVE_THERMOBARIC_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_THERMOBARIC_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_THERMOBARIC_ENERGY;
    public ModConfigSpec.DoubleValue EXPLOSIVE_SONIC_MAXHARDNESS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_SONIC_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_SONIC_VELOCITY;
    public ModConfigSpec.DoubleValue EXPLOSIVE_SONIC_DURATION;

    // Tier 3
    public ModConfigSpec.IntValue EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS;
    public ModConfigSpec.IntValue EXPLOSIVE_ANTIGRAVITY_CHUNKDURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR;
    public ModConfigSpec.IntValue EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT;
    public ModConfigSpec.IntValue EXPLOSIVE_ANTIGRAVITY_MAXBLOCKCHECKS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_EMP_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_SIZE;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_ENERGY;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_RADIATION_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_NUCLEAR_RADIATION_DURATION_REAL_DAYS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ENDOTHERMIC_MAXHARDNESS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ENDOTHERMIC_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ENDOTHERMIC_VELOCITY;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ENDOTHERMIC_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_EXOTHERMIC_MAXHARDNESS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_EXOTHERMIC_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_EXOTHERMIC_CHANCE_FOR_LAVA;
    public ModConfigSpec.DoubleValue EXPLOSIVE_EXOTHERMIC_CHANCE_TO_BURN;
    public ModConfigSpec.DoubleValue EXPLOSIVE_EXOTHERMIC_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ENDER_RADIUS;
    public ModConfigSpec.IntValue EXPLOSIVE_ENDER_ENDERMANCOUNT;
    public ModConfigSpec.DoubleValue EXPLOSIVE_HYPERSONIC_MAXHARDNESS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_HYPERSONIC_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_HYPERSONIC_VELOCITY;
    public ModConfigSpec.DoubleValue EXPLOSIVE_HYPERSONIC_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ANTIMATTER_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_ANTIMATTER_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_LARGEANTIMATTER_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_LARGEANTIMATTER_DURATION;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DARKMATTER_RADIUS;
    public ModConfigSpec.DoubleValue EXPLOSIVE_DARKMATTER_DURATION;
    public ModConfigSpec.IntValue EXPLOSIVE_DARKMATTER_PERSISTANCE;
    public ModConfigSpec.IntValue EXPLOSIVE_DARKMATTER_MOVEMENTTICKS;
    public ModConfigSpec.IntValue EXPLOSIVE_DARKMATTER_REPEATDURATION;

    /* MISSILE SILO */
    public ModConfigSpec.DoubleValue MISSILESILO_USAGE;
    public ModConfigSpec.IntValue MISSILE_HEALTH;
    public ModConfigSpec.IntValue LAUNCHER_PLATFORM_RANGE_T1;
    public ModConfigSpec.IntValue LAUNCHER_PLATFORM_RANGE_T2;
    public ModConfigSpec.IntValue LAUNCHER_PLATFORM_RANGE_T3;
    public ModConfigSpec.IntValue VLS_RANGE;
    public ModConfigSpec.IntValue LAUNCH_PLATFORM_DEFAULT_INACCURACY;

    /* ESM TOWER */
    public ModConfigSpec.DoubleValue ESM_TOWER_USAGE_PER_TICK;
    public ModConfigSpec.DoubleValue ESM_TOWER_SEARCH_RADIUS;

    /* SEARCH RADAR */
    public ModConfigSpec.DoubleValue RADAR_USAGE;
    public ModConfigSpec.IntValue RADAR_RANGE;

    /* FIRE CONTROL RADAR */
    public ModConfigSpec.DoubleValue FIRE_CONTROL_RADAR_USAGE;
    public ModConfigSpec.IntValue FIRE_CONTROL_RADAR_RANGE;
    public ModConfigSpec.DoubleValue MAX_DISTANCE_FROM_RADAR;

    /* SAM TURRET */
    public ModConfigSpec.DoubleValue SAM_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue SAM_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue SAM_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.IntValue SAM_TURRET_COOLDOWN;

    /* SAM ENTITY */
    public ModConfigSpec.DoubleValue SAM_ENTITY_TURNINGSPEEDRADIANS;
    public ModConfigSpec.DoubleValue SAM_TOP_SPEED;
    public ModConfigSpec.DoubleValue SAM_ACCELERATION;
    public ModConfigSpec.DoubleValue SAM_MINTURNSPEED_PERC;
    public ModConfigSpec.DoubleValue SAM_CHANCE_TO_DESTROY;

    /* ANTI-BALLISTIC ENTITY */
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_ENTITY_TURNINGSPEEDRADIANS;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_TOP_SPEED;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_ACCELERATION;
    public ModConfigSpec.DoubleValue ANTIBALLISTICMISSILE_MINTURNSPEED_PERC;

    public ModConfigSpec.DoubleValue RANGE_INCREASE_INACCURACY_MULTIPLIER;

    /* CIWS TURRET */
    public ModConfigSpec.DoubleValue CIWS_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue CIWS_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue CIWS_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.DoubleValue CIWS_INNACCURACY;

    /* LASER TURRET */
    public ModConfigSpec.DoubleValue LASER_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue LASER_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue LASER_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.DoubleValue LASER_TURRET_MAXHEAT;
    public ModConfigSpec.DoubleValue LASER_TURRET_COOLTHRESHHOLD;
    public ModConfigSpec.DoubleValue LASER_TURRET_BASE_DAMAGE;

    /* RAILGUN TURRET */
    public ModConfigSpec.DoubleValue RAILGUN_TURRET_USAGEPERTICK;
    public ModConfigSpec.DoubleValue RAILGUN_TURRET_BASE_RANGE;
    public ModConfigSpec.DoubleValue RAILGUN_TURRET_ROTATIONSPEEDRADIANS;
    public ModConfigSpec.IntValue RAILGUN_TURRET_COOLDOWN;
    public ModConfigSpec.DoubleValue RAILGUN_INNACCURACY;

    /* PROXIMITY DETECTOR */
    public ModConfigSpec.DoubleValue PROXIMITYDETECTOR_USAGEPERTICK;

    /* ITEMS */
    public ModConfigSpec.IntValue ROCKET_LAUNCHER_COOLDOWN_TICKS;
    public ModConfigSpec.IntValue LASER_DESIGNATOR_RANGE;

    public BallistixConfig() {
	var builder = new ModConfigSpec.Builder();

	builder.push("common");

	// Explosives (global + subsections)
	builder.push("explosives");
	SHOULD_CACHE_EXPLOSIONS = builder.comment("Whether explosions will be cached; may use a lot of memory!")
		.define("should_cache_explosions", true);
	SHOULD_MULTITHREAD_RAYTRACING = builder.comment(
		"NB! This is ONLY useful for very slow pc's. Like very slow. It is very BAD for slow - mediocre - good pc's and will make raytracing SLOWER for these!!!")
		.define("should_multithread_raytracing", false);

	// Tier 1
	builder.push("tier1");
	EXPLOSIVE_OBSIDIAN_SIZE = builder.defineInRange("explosive_obsidian_size", 10.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_CONDENSIVE_SIZE = builder.defineInRange("explosive_condensive_size", 2.5, 0, Double.MAX_VALUE);
	EXPLOSIVE_ATTRACTIVE_SIZE = builder.defineInRange("explosive_attractive_size", 1.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH = builder
		.defineInRange("explosive_attractive_repulsive_push_strength", 2.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_REPULSIVE_SIZE = builder.defineInRange("explosive_repulsive_size", 1.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_INCENDIARY_RADIUS = builder.defineInRange("explosive_incendiary_radius", 7.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT = builder.defineInRange("explosive_shrapnel_shrapnel_count", 25.0, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_CHEMICAL_SIZE = builder.defineInRange("explosive_chemical_size", 7.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_CHEMICAL_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_chemical_duration_ticks", 1200.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ANVIL_ANVILSPERBLAST = builder.defineInRange("explosive_anvil_anvilsperblast", 10, 0,
		Integer.MAX_VALUE);
	EXPLOSIVE_INFESTIVE_RADIUS = builder.defineInRange("explosive_infestive_radius", 7.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_INFESTIVE_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_infestive_duration_ticks", 20.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_DEBILITATION_SIZE = builder.defineInRange("explosive_debilitation_size", 7.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_DEBILITATION_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_debilitation_duration_ticks", 1200.0, 0, Double.MAX_VALUE);
	builder.pop(); // end tier1

	// Tier 2
	builder.push("tier2");
	EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT = builder.defineInRange("explosive_fragmentation_shrapnel_count", 50.0,
		0, Double.MAX_VALUE);
	EXPLOSIVE_CONTAGIOUS_SIZE = builder.defineInRange("explosive_contagious_size", 7.0, 0, Double.MAX_VALUE);
	VIRUS_EFFECT_RADIUS = builder.defineInRange("virus_effect_radius", 10, 0, Integer.MAX_VALUE);
	EXPLOSIVE_CONTAGIOUS_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_contagious_duration_ticks", 1200.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_BREACHING_SIZE = builder.defineInRange("explosive_breaching_size", 5.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_BREACHING_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_breaching_duration_ticks", 1.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_BREACHING_ENERGY = builder.defineInRange("explosive_breaching_energy", 60.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_THERMOBARIC_SIZE = builder.defineInRange("explosive_thermobaric_size", 30.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_THERMOBARIC_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_thermobaric_duration_ticks", 45.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_THERMOBARIC_ENERGY = builder.defineInRange("explosive_thermobaric_energy", 60.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_SONIC_MAXHARDNESS = builder.defineInRange("explosive_sonic_maxhardness", 2.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_SONIC_RADIUS = builder.defineInRange("explosive_sonic_radius", 10.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_SONIC_VELOCITY = builder.defineInRange("explosive_sonic_velocity", 1.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_SONIC_DURATION = builder.comment("Value is in ticks.").defineInRange("explosive_sonic_duration_ticks",
		20.0, 0, Double.MAX_VALUE);
	builder.pop(); // end tier2

	// Tier 3
	builder.push("tier3");
	EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS = builder.defineInRange("explosive_antigravity_chunkradius", 3, 0,
		Integer.MAX_VALUE);
	EXPLOSIVE_ANTIGRAVITY_CHUNKDURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_antigravity_chunkduration_ticks", 12000, 0, Integer.MAX_VALUE);
	EXPLOSIVE_ANTIGRAVITY_GRAVITYFACTOR = builder.defineInRange("explosive_antigravity_gravityfactor", 1.1, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_ANTIGRAVITY_MAXHEIGHT = builder.defineInRange("explosive_antigravity_maxheight", 300, 0,
		Integer.MAX_VALUE);
	EXPLOSIVE_ANTIGRAVITY_MAXBLOCKCHECKS = builder.comment("It is recommended to keep this value low")
		.defineInRange("explosive_antigravity_maxblockchecks", 1, 0, Integer.MAX_VALUE);
	EXPLOSIVE_EMP_RADIUS = builder.defineInRange("explosive_emp_radius", 45.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_SIZE = builder.defineInRange("explosive_nuclear_size", 45.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_nuclear_duration_ticks", 360.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_ENERGY = builder.defineInRange("explosive_nuclear_energy", 120.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_RADIATION_RADIUS = builder.defineInRange("explosive_nuclear_radiation_radius", 90.0, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_NUCLEAR_RADIATION_DURATION_REAL_DAYS = builder
		.defineInRange("explosive_nuclear_radiation_duration_real_days", 3.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ENDOTHERMIC_MAXHARDNESS = builder.defineInRange("explosive_endothermic_maxhardness", 1.0, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_ENDOTHERMIC_RADIUS = builder.defineInRange("explosive_endothermic_radius", 30.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ENDOTHERMIC_VELOCITY = builder.defineInRange("explosive_endothermic_velocity", 1.5, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_ENDOTHERMIC_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_endothermic_duration_ticks", 20.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_EXOTHERMIC_MAXHARDNESS = builder.defineInRange("explosive_exothermic_maxhardness", 2.0, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_EXOTHERMIC_RADIUS = builder.defineInRange("explosive_exothermic_radius", 40.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_EXOTHERMIC_CHANCE_FOR_LAVA = builder.defineInRange("explosive_exothermic_chance_for_lava", 0.01, 0,
		1);
	EXPLOSIVE_EXOTHERMIC_CHANCE_TO_BURN = builder.defineInRange("explosive_exothermic_chance_to_burn", 0.3, 0, 1);
	EXPLOSIVE_EXOTHERMIC_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_exothermic_duration_ticks", 20.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ENDER_RADIUS = builder.defineInRange("explosive_ender_radius", 9.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ENDER_ENDERMANCOUNT = builder.defineInRange("explosive_ender_endermancount", 10, 0,
		Integer.MAX_VALUE);
	EXPLOSIVE_HYPERSONIC_MAXHARDNESS = builder.defineInRange("explosive_hypersonic_maxhardness", 3.0, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_HYPERSONIC_RADIUS = builder.defineInRange("explosive_hypersonic_radius", 18, 0, Double.MAX_VALUE);
	EXPLOSIVE_HYPERSONIC_VELOCITY = builder.defineInRange("explosive_hypersonic_velocity", 1.5, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_HYPERSONIC_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_hypersonic_duration_ticks", 50.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ANTIMATTER_RADIUS = builder.defineInRange("explosive_antimatter_radius", 45.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_ANTIMATTER_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_antimatter_duration_ticks", 80.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_LARGEANTIMATTER_RADIUS = builder.defineInRange("explosive_largeantimatter_radius", 100.0, 0,
		Double.MAX_VALUE);
	EXPLOSIVE_LARGEANTIMATTER_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_largeantimatter_duration_ticks", 200.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_DARKMATTER_RADIUS = builder.defineInRange("explosive_darkmatter_radius", 50.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_DARKMATTER_DURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_darkmatter_duration_ticks", 1200.0, 0, Double.MAX_VALUE);
	EXPLOSIVE_DARKMATTER_PERSISTANCE = builder
		.comment("How many iterations the blast will perform. Set to -1 for infinite, 0 for none.")
		.defineInRange("explosive_darkmatter_persistance_iterations", 1200, Integer.MIN_VALUE,
			Integer.MAX_VALUE);
	EXPLOSIVE_DARKMATTER_MOVEMENTTICKS = builder.comment("Value is in ticks.")
		.defineInRange("explosive_darkmatter_movementticks", 5, 0, Integer.MAX_VALUE);
	EXPLOSIVE_DARKMATTER_REPEATDURATION = builder.comment("Value is in ticks.")
		.defineInRange("explosive_darkmatter_repeatduration_ticks", 1, 0, Integer.MAX_VALUE);
	builder.pop(); // end tier3
	builder.pop(); // end explosives

	// Missile silo
	builder.push("missile_silo");
	MISSILESILO_USAGE = builder.comment("Usage per launch in joules")
		.defineInRange("missilesilo_usage_joules_per_launch", 10000.0, 0, Double.MAX_VALUE);
	MISSILE_HEALTH = builder.defineInRange("missile_health", 100, 0, Integer.MAX_VALUE);
	LAUNCHER_PLATFORM_RANGE_T1 = builder.defineInRange("launcher_platform_range_t1", 500, 0, Integer.MAX_VALUE);
	LAUNCHER_PLATFORM_RANGE_T2 = builder.defineInRange("launcher_platform_range_t2", 3000, 0, Integer.MAX_VALUE);
	LAUNCHER_PLATFORM_RANGE_T3 = builder.defineInRange("launcher_platform_range_t3", 10000, 0, Integer.MAX_VALUE);
	VLS_RANGE = builder.defineInRange("vls_range", 500, 0, Integer.MAX_VALUE);
	LAUNCH_PLATFORM_DEFAULT_INACCURACY = builder.defineInRange("launch_platform_default_inaccuracy_deg", 45, 0,
		Integer.MAX_VALUE);
	builder.pop();

	// ESM tower
	builder.push("esm_tower");
	ESM_TOWER_USAGE_PER_TICK = builder.defineInRange("esm_tower_usage_per_tick", 1000.0, 0, Double.MAX_VALUE);
	ESM_TOWER_SEARCH_RADIUS = builder.defineInRange("esm_tower_search_radius", 200.0, 0, Double.MAX_VALUE);
	builder.pop();

	// Search radar
	builder.push("search_radar");
	RADAR_USAGE = builder.comment("Usage in watt for radar").defineInRange("radar_usage_watt", 10000.0, 0,
		Double.MAX_VALUE);
	RADAR_RANGE = builder.defineInRange("radar_range", 600, 0, Integer.MAX_VALUE);
	builder.pop();

	// Fire control radar
	builder.push("fire_control_radar");
	FIRE_CONTROL_RADAR_USAGE = builder.comment("Usage in watt for fire control radar")
		.defineInRange("fire_control_radar_usage_watt", 10000.0, 0, Double.MAX_VALUE);
	FIRE_CONTROL_RADAR_RANGE = builder.defineInRange("fire_control_radar_range", 550, 0, Integer.MAX_VALUE);
	MAX_DISTANCE_FROM_RADAR = builder.comment("How far a turret can be from a fire control radar")
		.defineInRange("max_distance_from_radar", 32.0, 0, Double.MAX_VALUE);
	builder.pop();

	// SAM turret
	builder.push("sam_turret");
	SAM_TURRET_USAGEPERTICK = builder.defineInRange("sam_turret_usage_per_tick", 1000.0, 0, Double.MAX_VALUE);
	SAM_TURRET_BASE_RANGE = builder.defineInRange("sam_turret_base_range", 300.0, 0, Double.MAX_VALUE);
	SAM_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("sam_turret_rotation_speed_radians", 0.2, 0,
		Double.MAX_VALUE);
	SAM_TURRET_COOLDOWN = builder.defineInRange("sam_turret_cooldown_ticks", 80, 0, Integer.MAX_VALUE);
	builder.pop();

	// SAM entity
	builder.push("sam_entity");
	SAM_ENTITY_TURNINGSPEEDRADIANS = builder.defineInRange("sam_entity_turning_speed_radians", 0.1, 0,
		Double.MAX_VALUE);
	SAM_TOP_SPEED = builder.defineInRange("sam_top_speed", 4.5, 0, Double.MAX_VALUE);
	SAM_ACCELERATION = builder.defineInRange("sam_acceleration", 0.065, 0, Double.MAX_VALUE);
	SAM_MINTURNSPEED_PERC = builder.defineInRange("sam_minturnspeed_perc", 0.25, 0, 1);
	SAM_CHANCE_TO_DESTROY = builder.defineInRange("sam_chance_to_destroy", 0.7, 0, 1);
	builder.pop();

	// Anti-ballistic entity
	builder.push("anti_ballistic_entity");
	ANTIBALLISTICMISSILE_ENTITY_TURNINGSPEEDRADIANS = builder
		.defineInRange("antiballisticmissile_entity_turning_speed_radians", 0.05, 0, Double.MAX_VALUE);
	ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY = builder.defineInRange("antiballisticmissile_chance_to_destroy", 0.95,
		0, 1);
	ANTIBALLISTICMISSILE_TOP_SPEED = builder.defineInRange("antiballisticmissile_top_speed", 5.0, 0,
		Double.MAX_VALUE);
	ANTIBALLISTICMISSILE_ACCELERATION = builder.defineInRange("antiballisticmissile_acceleration", 0.07, 0,
		Double.MAX_VALUE);
	ANTIBALLISTICMISSILE_MINTURNSPEED_PERC = builder.defineInRange("antiballisticmissile_minturnspeed_perc", 0.15,
		0, 1);
	RANGE_INCREASE_INACCURACY_MULTIPLIER = builder.comment(
		"The amount of inaccuracy each range upgrade will add to the turret. Affects shots past the base range. Set to 1 to disable.")
		.defineInRange("range_increase_inaccuracy_multiplier", 1.001, 0, Double.MAX_VALUE);
	builder.pop();

	// CIWS turret
	builder.push("ciws_turret");
	CIWS_TURRET_USAGEPERTICK = builder.defineInRange("ciws_turret_usage_per_tick", 1000.0, 0, Double.MAX_VALUE);
	CIWS_TURRET_BASE_RANGE = builder.defineInRange("ciws_turret_base_range", 100.0, 0, Double.MAX_VALUE);
	CIWS_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("ciws_turret_rotation_speed_radians", 0.6, 0,
		Double.MAX_VALUE);
	CIWS_INNACCURACY = builder.defineInRange("ciws_innaccuracy", 0.05, 0, Double.MAX_VALUE);
	builder.pop();

	// Laser turret
	builder.push("laser_turret");
	LASER_TURRET_USAGEPERTICK = builder.defineInRange("laser_turret_usage_per_tick", 10000.0, 0, Double.MAX_VALUE);
	LASER_TURRET_BASE_RANGE = builder.defineInRange("laser_turret_base_range", 300.0, 0, Double.MAX_VALUE);
	LASER_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("laser_turret_rotation_speed_radians", 0.2, 0,
		Double.MAX_VALUE);
	LASER_TURRET_MAXHEAT = builder.defineInRange("laser_turret_max_heat", 100.0, 0, Double.MAX_VALUE);
	LASER_TURRET_COOLTHRESHHOLD = builder.defineInRange("laser_turret_cool_threshold", 40.0, 0, Double.MAX_VALUE);
	LASER_TURRET_BASE_DAMAGE = builder.defineInRange("laser_turret_base_damage", 3.0, 0, Double.MAX_VALUE);
	builder.pop();

	// Railgun turret
	builder.push("railgun_turret");
	RAILGUN_TURRET_USAGEPERTICK = builder.defineInRange("railgun_turret_usage_per_tick", 10000.0, 0,
		Double.MAX_VALUE);
	RAILGUN_TURRET_BASE_RANGE = builder.defineInRange("railgun_turret_base_range", 300.0, 0, Double.MAX_VALUE);
	RAILGUN_TURRET_ROTATIONSPEEDRADIANS = builder.defineInRange("railgun_turret_rotation_speed_radians", 0.1, 0,
		Double.MAX_VALUE);
	RAILGUN_TURRET_COOLDOWN = builder.defineInRange("railgun_turret_cooldown_ticks", 100, 0, Integer.MAX_VALUE);
	RAILGUN_INNACCURACY = builder.defineInRange("railgun_innaccuracy", 0.05, 0, Double.MAX_VALUE);
	builder.pop();

	// Proximity detector
	builder.push("proximity_detector");
	PROXIMITYDETECTOR_USAGEPERTICK = builder.defineInRange("proximitydetector_usage_per_tick", 100.0, 0,
		Double.MAX_VALUE);
	builder.pop();

	// Items
	builder.push("items");
	ROCKET_LAUNCHER_COOLDOWN_TICKS = builder.defineInRange("rocket_launcher_cooldown_ticks", 60, 0,
		Integer.MAX_VALUE);
	LASER_DESIGNATOR_RANGE = builder.defineInRange("laser_designator_range", 1000, 0, Integer.MAX_VALUE);
	builder.pop();

	builder.pop(); // end common

	SPEC = builder.build();
    }
}
