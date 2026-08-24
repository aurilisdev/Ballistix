package ballistix.api.missile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.compatibility.TessellateCompat;
import ballistix.registers.BallistixAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.GAME)
public class MissileManager {

    private static final Set<UUID> MISSILE_TASKS = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> BULLET_TASKS = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> RAILGUN_TASKS = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> SAM_TASKS = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void initialize(ServerStartedEvent event) {
	ServerLevel overworld = event.getServer().overworld();
	overworld.getData(BallistixAttachmentTypes.SILO_FREQUENCIES);
	overworld.getData(BallistixAttachmentTypes.ACTIVE_MISSILES);
	overworld.getData(BallistixAttachmentTypes.ACTIVE_BULLETS);
	overworld.getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS);
	overworld.getData(BallistixAttachmentTypes.ACTIVE_SAMS);
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
	ServerLevel overworld = getOverworld();
	tickAll(event.getServer(), overworld.getData(BallistixAttachmentTypes.ACTIVE_MISSILES),
		VirtualMissile::blockPosition, VirtualMissile::tick, VirtualMissile::hasExploded, MISSILE_TASKS);
	tickAll(event.getServer(), overworld.getData(BallistixAttachmentTypes.ACTIVE_BULLETS),
		VirtualProjectile.VirtualBullet::blockPosition, VirtualProjectile.VirtualBullet::tick,
		VirtualProjectile.VirtualBullet::hasExploded, BULLET_TASKS);
	tickAll(event.getServer(), overworld.getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS),
		VirtualProjectile.VirtualRailgunRound::blockPosition, VirtualProjectile.VirtualRailgunRound::tick,
		VirtualProjectile.VirtualRailgunRound::hasExploded, RAILGUN_TASKS);
	tickAll(event.getServer(), overworld.getData(BallistixAttachmentTypes.ACTIVE_SAMS),
		VirtualProjectile.VirtualSAM::blockPosition, VirtualProjectile.VirtualSAM::tick,
		VirtualProjectile.VirtualSAM::hasExploded, SAM_TASKS);
    }

    private static <T> void tickAll(MinecraftServer server,
	    ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, T>> data, Function<T, BlockPos> position,
	    BiConsumer<T, ServerLevel> ticker, Predicate<T> exploded, Set<UUID> inFlight) {
	data.forEach((key, active) -> {
	    ServerLevel level = server.getLevel(key);
	    if (level == null) {
		return;
	    }
	    if (!TessellateCompat.isLoaded()) {
		active.forEach((id, value) -> tickOne(active, id, value, level, ticker, exploded));
		return;
	    }
	    Map<Long, List<Map.Entry<UUID, T>>> batches = new HashMap<>();
	    active.entrySet().forEach(entry -> {
		BlockPos pos = position.apply(entry.getValue());
		long chunk = net.minecraft.world.level.ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
		batches.computeIfAbsent(chunk, ignored -> new ArrayList<>()).add(entry);
	    });
	    batches.values().forEach(batch -> {
		List<Map.Entry<UUID, T>> claimed = batch.stream()
			.filter(entry -> inFlight.add(entry.getKey()))
			.toList();
		if (claimed.isEmpty()) {
		    return;
		}
		try {
		    BlockPos owner = position.apply(claimed.getFirst().getValue());
		    TessellateCompat.runOnRegion(level, owner, () -> {
			try {
			    claimed.forEach(entry -> {
				T value = entry.getValue();
				if (active.get(entry.getKey()) == value) {
				    tickOne(active, entry.getKey(), value, level, ticker, exploded);
				}
			    });
			} finally {
			    claimed.forEach(entry -> inFlight.remove(entry.getKey()));
			}
		    });
		} catch (RuntimeException e) {
		    claimed.forEach(entry -> inFlight.remove(entry.getKey()));
		    throw e;
		}
	    });
	});
    }

    private static <T> void tickOne(ConcurrentHashMap<UUID, T> active, UUID id, T value, ServerLevel level,
	    BiConsumer<T, ServerLevel> ticker, Predicate<T> exploded) {
	ticker.accept(value, level);
	if (exploded.test(value)) {
	    active.remove(id, value);
	}
    }

    public static void addMissile(ResourceKey<Level> key, VirtualMissile missile) {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_MISSILES)
		.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>()).put(missile.getId(), missile);
    }

    public static void removeMissile(ResourceKey<Level> level, UUID id) {
	ConcurrentHashMap<UUID, VirtualMissile> missiles = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_MISSILES).get(level);
	if (missiles != null) {
	    missiles.remove(id);
	}
    }

    public static Collection<VirtualMissile> getMissilesForLevel(ResourceKey<Level> level) {
	ConcurrentHashMap<UUID, VirtualMissile> missiles = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_MISSILES).get(level);
	return missiles == null ? List.of() : missiles.values();
    }

    @Nullable
    public static VirtualMissile getMissile(ResourceKey<Level> level, UUID id) {
	ConcurrentHashMap<UUID, VirtualMissile> missiles = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_MISSILES).get(level);
	return missiles == null ? null : missiles.get(id);
    }

    public static void wipeAllMissiles() {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_MISSILES).clear();
    }

    public static void addBullet(ResourceKey<Level> key, VirtualProjectile.VirtualBullet bullet) {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_BULLETS)
		.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>()).put(bullet.id, bullet);
    }

    @Nullable
    public static VirtualProjectile.VirtualBullet getBullet(ResourceKey<Level> level, UUID id) {
	ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet> bullets = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_BULLETS).get(level);
	return bullets == null ? null : bullets.get(id);
    }

    public static Collection<VirtualProjectile.VirtualBullet> getBulletsForLevel(ResourceKey<Level> level) {
	ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet> bullets = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_BULLETS).get(level);
	return bullets == null ? List.of() : bullets.values();
    }

    public static void wipeAllBullets() {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_BULLETS).clear();
    }

    public static void addRailgunRound(ResourceKey<Level> key, VirtualProjectile.VirtualRailgunRound railgun) {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS)
		.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>()).put(railgun.id, railgun);
    }

    @Nullable
    public static VirtualProjectile.VirtualRailgunRound getRailgunRound(ResourceKey<Level> level, UUID id) {
	ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound> rounds = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS).get(level);
	return rounds == null ? null : rounds.get(id);
    }

    public static Collection<VirtualProjectile.VirtualRailgunRound> getRailgunRoundsForLevel(ResourceKey<Level> level) {
	ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound> rounds = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS).get(level);
	return rounds == null ? List.of() : rounds.values();
    }

    public static void wipeAllRailgunRounds() {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS).clear();
    }

    public static void addSAM(ResourceKey<Level> key, VirtualProjectile.VirtualSAM bullet) {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_SAMS)
		.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>()).put(bullet.id, bullet);
    }

    @Nullable
    public static VirtualProjectile.VirtualSAM getSAM(ResourceKey<Level> level, UUID id) {
	ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM> sams = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_SAMS).get(level);
	return sams == null ? null : sams.get(id);
    }

    public static Collection<VirtualProjectile.VirtualSAM> getSAMsForLevel(ResourceKey<Level> level) {
	ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM> sams = getOverworld()
		.getData(BallistixAttachmentTypes.ACTIVE_SAMS).get(level);
	return sams == null ? List.of() : sams.values();
    }

    public static void wipeAllSAMs() {
	getOverworld().getData(BallistixAttachmentTypes.ACTIVE_SAMS).clear();
    }

    private static ServerLevel getOverworld() {
	return ServerLifecycleHooks.getCurrentServer().overworld();
    }

}
