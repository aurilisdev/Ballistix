package ballistix.api.missile;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.capability.CapabilityActiveBullets;
import ballistix.api.capability.CapabilityActiveMissiles;
import ballistix.api.capability.CapabilityActiveRailgunRounds;
import ballistix.api.capability.CapabilityActiveSAMs;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
public class MissileManager {

    // Only fires post
    @SubscribeEvent
    public static void tick(ServerTickEvent event) {

	if (event.phase == Phase.START) {
	    return;
	}

	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

	    for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> entry : missiles.activeMissiles
		    .entrySet()) {

		ServerLevel level = event.getServer().getLevel(entry.getKey());

		// level isn't loaded
		if (level == null) {
		    continue;
		}

		Iterator<Map.Entry<UUID, VirtualMissile>> it = entry.getValue().entrySet().iterator();

		while (it.hasNext()) {

		    Map.Entry<UUID, VirtualMissile> active = it.next();

		    active.getValue().tick(level);

		    if (active.getValue().hasExploded()) {
			it.remove();
		    }

		}

	    }
	}

	LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld
		.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

	if (lazyOptionalBullets.isPresent()) {

	    CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();

	    for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualBullet>> entry : bullets.activeBullets
		    .entrySet()) {

		ServerLevel level = event.getServer().getLevel(entry.getKey());

		// level isn't loaded
		if (level == null) {
		    continue;
		}

		Iterator<Map.Entry<UUID, VirtualProjectile.VirtualBullet>> it = entry.getValue().entrySet().iterator();

		while (it.hasNext()) {

		    Map.Entry<UUID, VirtualProjectile.VirtualBullet> active = it.next();

		    active.getValue().tick(level);

		    if (active.getValue().hasExploded()) {
			it.remove();
		    }

		}

	    }
	}

	LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld
		.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

	if (lazyOptionalRailgun.isPresent()) {

	    CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();

	    for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> entry : railgunrounds.activeRailgunRounds
		    .entrySet()) {

		ServerLevel level = event.getServer().getLevel(entry.getKey());

		// level isn't loaded
		if (level == null) {
		    continue;
		}

		Iterator<Map.Entry<UUID, VirtualProjectile.VirtualRailgunRound>> it = entry.getValue().entrySet()
			.iterator();

		while (it.hasNext()) {

		    Map.Entry<UUID, VirtualProjectile.VirtualRailgunRound> active = it.next();

		    active.getValue().tick(level);

		    if (active.getValue().hasExploded()) {
			it.remove();
		    }

		}

	    }

	}

	LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld
		.getCapability(BallistixCapabilities.ACTIVE_SAMS);

	if (lazyOptionalSAMs.isPresent()) {

	    CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();

	    for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>> entry : sams.activeSAMs
		    .entrySet()) {

		ServerLevel level = event.getServer().getLevel(entry.getKey());

		// level isn't loaded
		if (level == null) {
		    continue;
		}

		Iterator<Map.Entry<UUID, VirtualProjectile.VirtualSAM>> it = entry.getValue().entrySet().iterator();

		while (it.hasNext()) {

		    Map.Entry<UUID, VirtualProjectile.VirtualSAM> active = it.next();

		    active.getValue().tick(level);

		    if (active.getValue().hasExploded()) {
			it.remove();
		    }

		}

	    }

	}

    }

    public static void addMissile(ResourceKey<Level> key, VirtualMissile missile) {

	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

	    HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(key, new HashMap<>());

	    virtual.put(missile.getId(), missile);

	    missiles.activeMissiles.put(key, virtual);
	}

    }

    public static void removeMissile(ResourceKey<Level> level, UUID id) {

	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

	    HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(level, new HashMap<>());

	    virtual.remove(id);
	}

    }

    public static Collection<VirtualMissile> getMissilesForLevel(ResourceKey<Level> level) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

	    HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(level, new HashMap<>());

	    return virtual.values();

	}

	return new HashSet<>();
    }

    @Nullable
    public static VirtualMissile getMissile(ResourceKey<Level> level, UUID id) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

	    HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(level, new HashMap<>());

	    return virtual.get(id);

	}

	return null;
    }

    public static void wipeAllMissiles() {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

	    missiles.activeMissiles.clear();

	}
    }

    public static void addBullet(ResourceKey<Level> key, VirtualProjectile.VirtualBullet bullet) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld
		.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

	if (lazyOptionalBullets.isPresent()) {

	    CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = bullets.activeBullets.getOrDefault(key,
		    new HashMap<>());

	    virtual.put(bullet.id, bullet);

	    bullets.activeBullets.put(key, virtual);

	}

    }

    @Nullable
    public static VirtualProjectile.VirtualBullet getBullet(ResourceKey<Level> level, UUID id) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld
		.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

	if (lazyOptionalBullets.isPresent()) {

	    CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = bullets.activeBullets.getOrDefault(level,
		    new HashMap<>());

	    return virtual.get(id);

	}

	return null;
    }

    public static Collection<VirtualProjectile.VirtualBullet> getBulletsForLevel(ResourceKey<Level> level) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveBullets> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveBullets bullets = lazyOptionalMissiles.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = bullets.activeBullets.getOrDefault(level,
		    new HashMap<>());

	    return virtual.values();

	}

	return new HashSet<>();
    }

    public static void wipeAllBullets() {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld
		.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

	if (lazyOptionalBullets.isPresent()) {

	    CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();

	    bullets.activeBullets.clear();

	}
    }

    public static void addRailgunRound(ResourceKey<Level> key, VirtualProjectile.VirtualRailgunRound railgun) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld
		.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

	if (lazyOptionalRailgun.isPresent()) {

	    CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = railgunrounds.activeRailgunRounds
		    .getOrDefault(key, new HashMap<>());

	    virtual.put(railgun.id, railgun);

	    railgunrounds.activeRailgunRounds.put(key, virtual);

	}
    }

    @Nullable
    public static VirtualProjectile.VirtualRailgunRound getRailgunRound(ResourceKey<Level> level, UUID id) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld
		.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

	if (lazyOptionalRailgun.isPresent()) {

	    CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = railgunrounds.activeRailgunRounds
		    .getOrDefault(level, new HashMap<>());

	    return virtual.get(id);

	}

	return null;
    }

    public static Collection<VirtualProjectile.VirtualRailgunRound> getRailgunRoundsForLevel(ResourceKey<Level> level) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveRailgunRounds rounds = lazyOptionalMissiles.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = rounds.activeRailgunRounds
		    .getOrDefault(level, new HashMap<>());

	    return virtual.values();

	}

	return new HashSet<>();
    }

    public static void wipeAllRailgunRounds() {

	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld
		.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

	if (lazyOptionalRailgun.isPresent()) {

	    CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();

	    railgunrounds.activeRailgunRounds.clear();

	}

    }

    public static void addSAM(ResourceKey<Level> key, VirtualProjectile.VirtualSAM bullet) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld
		.getCapability(BallistixCapabilities.ACTIVE_SAMS);

	if (lazyOptionalSAMs.isPresent()) {

	    CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = sams.activeSAMs.getOrDefault(key, new HashMap<>());

	    virtual.put(bullet.id, bullet);

	    sams.activeSAMs.put(key, virtual);

	}

    }

    @Nullable
    public static VirtualProjectile.VirtualSAM getSAM(ResourceKey<Level> level, UUID id) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld
		.getCapability(BallistixCapabilities.ACTIVE_SAMS);

	if (lazyOptionalSAMs.isPresent()) {

	    CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = sams.activeSAMs.getOrDefault(level, new HashMap<>());

	    return virtual.get(id);

	}
	return null;
    }

    public static Collection<VirtualProjectile.VirtualSAM> getSAMsForLevel(ResourceKey<Level> level) {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveSAMs> lazyOptionalMissiles = overworld
		.getCapability(BallistixCapabilities.ACTIVE_SAMS);

	if (lazyOptionalMissiles.isPresent()) {

	    CapabilityActiveSAMs rounds = lazyOptionalMissiles.resolve().get();

	    HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = rounds.activeSAMs.getOrDefault(level,
		    new HashMap<>());

	    return virtual.values();

	}

	return new HashSet<>();
    }

    public static void wipeAllSAMs() {
	ServerLevel overworld = getOverworld();

	LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld
		.getCapability(BallistixCapabilities.ACTIVE_SAMS);

	if (lazyOptionalSAMs.isPresent()) {

	    CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();

	    sams.activeSAMs.clear();

	}
    }

    private static ServerLevel getOverworld() {
	return ServerLifecycleHooks.getCurrentServer().overworld();
    }

}
