package ballistix.api.missile;

import ballistix.References;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.registers.BallistixAttachmentTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;

@EventBusSubscriber(modid = References.ID, bus = EventBusSubscriber.Bus.GAME)
public class MissileManager {

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {

        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> missiles = overworld.getData(BallistixAttachmentTypes.ACTIVE_MISSILES);

        for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> entry : missiles.entrySet()) {

            ServerLevel level = event.getServer().getLevel(entry.getKey());

            //level isn't loaded
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

        overworld.setData(BallistixAttachmentTypes.ACTIVE_MISSILES, missiles);

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualBullet>> bullets = overworld.getData(BallistixAttachmentTypes.ACTIVE_BULLETS);

        for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualBullet>> entry : bullets.entrySet()) {

            ServerLevel level = event.getServer().getLevel(entry.getKey());

            //level isn't loaded
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

        overworld.setData(BallistixAttachmentTypes.ACTIVE_BULLETS, bullets);

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> railgunrounds = overworld.getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS);

        for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> entry : railgunrounds.entrySet()) {

            ServerLevel level = event.getServer().getLevel(entry.getKey());

            //level isn't loaded
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

        overworld.setData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS, railgunrounds);

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>> sams = overworld.getData(BallistixAttachmentTypes.ACTIVE_SAMS);

        for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>> entry : sams.entrySet()) {

            ServerLevel level = event.getServer().getLevel(entry.getKey());

            //level isn't loaded
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

        overworld.setData(BallistixAttachmentTypes.ACTIVE_SAMS, sams);

    }

    public static void addMissile(ResourceKey<Level> key, VirtualMissile missile) {

        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_MISSILES);

        HashMap<UUID, VirtualMissile> virtual = data.getOrDefault(key, new HashMap<>());

        virtual.put(missile.getId(), missile);

        data.put(key, virtual);

        overworld.setData(BallistixAttachmentTypes.ACTIVE_MISSILES, data);

    }

    public static void removeMissile(ResourceKey<Level> level, UUID id) {

        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_MISSILES);

        HashMap<UUID, VirtualMissile> virtual = data.getOrDefault(level, new HashMap<>());

        virtual.remove(id);

        overworld.setData(BallistixAttachmentTypes.ACTIVE_MISSILES, data);

    }

    public static Collection<VirtualMissile> getMissilesForLevel(ResourceKey<Level> level) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_MISSILES);

        HashMap<UUID, VirtualMissile> virtual = data.getOrDefault(level, new HashMap<>());

        return virtual.values();
    }

    @Nullable
    public static VirtualMissile getMissile(ResourceKey<Level> level, UUID id) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_MISSILES);

        HashMap<UUID, VirtualMissile> virtual = data.getOrDefault(level, new HashMap<>());

        return virtual.get(id);
    }

    public static void wipeAllMissiles() {
        getOverworld().removeData(BallistixAttachmentTypes.ACTIVE_MISSILES);
    }

    public static void addBullet(ResourceKey<Level> key, VirtualProjectile.VirtualBullet bullet) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualBullet>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_BULLETS);

        HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = data.getOrDefault(key, new HashMap<>());

        virtual.put(bullet.id, bullet);

        data.put(key, virtual);

        overworld.setData(BallistixAttachmentTypes.ACTIVE_BULLETS, data);
    }

    @Nullable
    public static VirtualProjectile.VirtualBullet getBullet(ResourceKey<Level> level, UUID id) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualBullet>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_BULLETS);

        HashMap<UUID, VirtualProjectile.VirtualBullet> virtual = data.getOrDefault(level, new HashMap<>());

        return virtual.get(id);
    }

    public static void wipeAllBullets() {
        getOverworld().removeData(BallistixAttachmentTypes.ACTIVE_BULLETS);
    }

    public static void addRailgunRound(ResourceKey<Level> key, VirtualProjectile.VirtualRailgunRound railgun) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS);

        HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = data.getOrDefault(key, new HashMap<>());

        virtual.put(railgun.id, railgun);

        data.put(key, virtual);

        overworld.setData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS, data);
    }

    @Nullable
    public static VirtualProjectile.VirtualRailgunRound getRailgunRound(ResourceKey<Level> level, UUID id) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS);

        HashMap<UUID, VirtualProjectile.VirtualRailgunRound> virtual = data.getOrDefault(level, new HashMap<>());

        return virtual.get(id);
    }

    public static void wipeAllRailgunRounds() {
        getOverworld().removeData(BallistixAttachmentTypes.ACTIVE_RAILGUNROUNDS);
    }

    public static void addSAM(ResourceKey<Level> key, VirtualProjectile.VirtualSAM bullet) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_SAMS);

        HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = data.getOrDefault(key, new HashMap<>());

        virtual.put(bullet.id, bullet);

        data.put(key, virtual);

        overworld.setData(BallistixAttachmentTypes.ACTIVE_SAMS, data);
    }

    @Nullable
    public static VirtualProjectile.VirtualSAM getSAM(ResourceKey<Level> level, UUID id) {
        ServerLevel overworld = getOverworld();

        HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>> data = overworld.getData(BallistixAttachmentTypes.ACTIVE_SAMS);

        HashMap<UUID, VirtualProjectile.VirtualSAM> virtual = data.getOrDefault(level, new HashMap<>());

        return virtual.get(id);
    }

    public static void wipeAllSAMs() {
        getOverworld().removeData(BallistixAttachmentTypes.ACTIVE_SAMS);
    }

    private static ServerLevel getOverworld() {
        return ServerLifecycleHooks.getCurrentServer().overworld();
    }

}
