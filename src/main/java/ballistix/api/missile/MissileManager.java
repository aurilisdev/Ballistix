package ballistix.api.missile;

import ballistix.Ballistix;
import ballistix.api.capability.CapabilityActiveBullets;
import ballistix.api.capability.CapabilityActiveMissiles;
import ballistix.api.capability.CapabilityActiveRailgunRounds;
import ballistix.api.capability.CapabilityActiveSAMs;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
public class MissileManager {

	// Only fires post
	@SubscribeEvent
	public static void tick(ServerTickEvent event) {
		
		if(event.phase == Phase.START) {
			return;
		}

		ServerWorld overworld = getOverworld();
		
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

			for (Map.Entry<RegistryKey<World>, HashMap<UUID, VirtualMissile>> entry : missiles.activeMissiles.entrySet()) {

				ServerWorld level = server.getLevel(entry.getKey());

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

		LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

		if (lazyOptionalBullets.isPresent()) {

			CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();

			for (Map.Entry<RegistryKey<World>, HashMap<UUID, VirtualProjectile.VirtualBullet>> entry : bullets.activeBullets.entrySet()) {

				ServerWorld level = server.getLevel(entry.getKey());

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

		LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

		if (lazyOptionalRailgun.isPresent()) {

			CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();

			for (Map.Entry<RegistryKey<World>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> entry : railgunrounds.activeRailgunRounds.entrySet()) {

				ServerWorld level = server.getLevel(entry.getKey());

				// level isn't loaded
				if (level == null) {
					continue;
				}

				Iterator<Map.Entry<UUID, VirtualProjectile.VirtualRailgunRound>> it = entry.getValue().entrySet().iterator();

				while (it.hasNext()) {

					Map.Entry<UUID, VirtualProjectile.VirtualRailgunRound> active = it.next();

					active.getValue().tick(level);

					if (active.getValue().hasExploded()) {
						it.remove();
					}

				}

			}

		}

		LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld.getCapability(BallistixCapabilities.ACTIVE_SAMS);

		if (lazyOptionalSAMs.isPresent()) {

			CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();

			for (Map.Entry<RegistryKey<World>, HashMap<UUID, VirtualProjectile.VirtualSAM>> entry : sams.activeSAMs.entrySet()) {

				ServerWorld level = server.getLevel(entry.getKey());

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

	public static void addMissile(RegistryKey<World> key, VirtualMissile missile) {

		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

			HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(key, new HashMap<>());

			virtual.put(missile.getId(), missile);

			missiles.activeMissiles.put(key, virtual);
		}

	}

	public static void removeMissile(RegistryKey<World> level, UUID id) {

		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

			HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(level, new HashMap<>());

			virtual.remove(id);
		}

	}

	public static Collection<VirtualMissile> getMissilesForLevel(RegistryKey<World> level) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

			HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(level, new HashMap<>());

			return virtual.values();

		}

		return new HashSet<>();
	}

	@Nullable
	public static VirtualMissile getMissile(RegistryKey<World> level, UUID id) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

			HashMap<UUID, VirtualMissile> virtual = missiles.activeMissiles.getOrDefault(level, new HashMap<>());

			return virtual.get(id);

		}

		return null;
	}
	
	public static void wipeAllMissiles() {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveMissiles> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_MISSILES);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveMissiles missiles = lazyOptionalMissiles.resolve().get();

			missiles.activeMissiles.clear();

		}
	}

	public static void addBullet(RegistryKey<World> key, VirtualProjectile.VirtualBullet bullet) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

		if (lazyOptionalBullets.isPresent()) {

			CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();
			
			HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = bullets.activeBullets.getOrDefault(key, new HashMap<>());

			virtual.put(bullet.id, bullet);

			bullets.activeBullets.put(key, virtual);
			
		}	

	}

	@Nullable
	public static VirtualProjectile.VirtualBullet getBullet(RegistryKey<World> level, UUID id) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

		if (lazyOptionalBullets.isPresent()) {

			CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();
			
			HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = bullets.activeBullets.getOrDefault(level, new HashMap<>());

			return virtual.get(id);
			
		}	

		return null;
	}
	
	public static Collection<VirtualProjectile.VirtualBullet> getBulletsForLevel(RegistryKey<World> level) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveBullets> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveBullets bullets = lazyOptionalMissiles.resolve().get();

			HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = bullets.activeBullets.getOrDefault(level, new HashMap<>());

			return virtual.values();

		}

		return new HashSet<>();
	}
	
	public static void wipeAllBullets() {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveBullets> lazyOptionalBullets = overworld.getCapability(BallistixCapabilities.ACTIVE_BULLETS);

		if (lazyOptionalBullets.isPresent()) {

			CapabilityActiveBullets bullets = lazyOptionalBullets.resolve().get();
			
			bullets.activeBullets.clear();
			
		}	
	}

	public static void addRailgunRound(RegistryKey<World> key, VirtualProjectile.VirtualRailgunRound railgun) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

		if (lazyOptionalRailgun.isPresent()) {

			CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();
			
			HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = railgunrounds.activeRailgunRounds.getOrDefault(key, new HashMap<>());

			virtual.put(railgun.id, railgun);

			railgunrounds.activeRailgunRounds.put(key, virtual);
		
		}	
	}

	@Nullable
	public static VirtualProjectile.VirtualRailgunRound getRailgunRound(RegistryKey<World> level, UUID id) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

		if (lazyOptionalRailgun.isPresent()) {

			CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();
			
			HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = railgunrounds.activeRailgunRounds.getOrDefault(level, new HashMap<>());

			return virtual.get(id);
			
		}	

		return null;
	}
	
	public static Collection<VirtualProjectile.VirtualRailgunRound> getRailgunRoundsForLevel(RegistryKey<World> level) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveRailgunRounds rounds = lazyOptionalMissiles.resolve().get();

			HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = rounds.activeRailgunRounds.getOrDefault(level, new HashMap<>());

			return virtual.values();

		}

		return new HashSet<>();
	}
	
	public static void wipeAllRailgunRounds() {
		
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveRailgunRounds> lazyOptionalRailgun = overworld.getCapability(BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS);

		if (lazyOptionalRailgun.isPresent()) {

			CapabilityActiveRailgunRounds railgunrounds = lazyOptionalRailgun.resolve().get();
			
			railgunrounds.activeRailgunRounds.clear();
			
		}	
		
	}

	public static void addSAM(RegistryKey<World> key, VirtualProjectile.VirtualSAM bullet) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld.getCapability(BallistixCapabilities.ACTIVE_SAMS);

		if (lazyOptionalSAMs.isPresent()) {

			CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();
			
			HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = sams.activeSAMs.getOrDefault(key, new HashMap<>());

			virtual.put(bullet.id, bullet);

			sams.activeSAMs.put(key, virtual);
			
		}	

	}

	@Nullable
	public static VirtualProjectile.VirtualSAM getSAM(RegistryKey<World> level, UUID id) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld.getCapability(BallistixCapabilities.ACTIVE_SAMS);

		if (lazyOptionalSAMs.isPresent()) {

			CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();
			
			HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = sams.activeSAMs.getOrDefault(level, new HashMap<>());

			return virtual.get(id);
			
		}	
		return null;
	}
	
	public static Collection<VirtualProjectile.VirtualSAM> getSAMsForLevel(RegistryKey<World> level) {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveSAMs> lazyOptionalMissiles = overworld.getCapability(BallistixCapabilities.ACTIVE_SAMS);

		if (lazyOptionalMissiles.isPresent()) {

			CapabilityActiveSAMs rounds = lazyOptionalMissiles.resolve().get();

			HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = rounds.activeSAMs.getOrDefault(level, new HashMap<>());

			return virtual.values();

		}

		return new HashSet<>();
	}
	
	public static void wipeAllSAMs() {
		ServerWorld overworld = getOverworld();

		LazyOptional<CapabilityActiveSAMs> lazyOptionalSAMs = overworld.getCapability(BallistixCapabilities.ACTIVE_SAMS);

		if (lazyOptionalSAMs.isPresent()) {

			CapabilityActiveSAMs sams = lazyOptionalSAMs.resolve().get();
			
			sams.activeSAMs.clear();
			
		}	
	}

	private static ServerWorld getOverworld() {
		return ServerLifecycleHooks.getCurrentServer().overworld();
	}

}
