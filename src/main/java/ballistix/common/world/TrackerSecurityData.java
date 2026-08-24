package ballistix.common.world;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class TrackerSecurityData extends SavedData {

    private static final String DATA_NAME = "ballistix_tracker_security";

	private final ConcurrentHashMap<UUID, Long> revisions = new ConcurrentHashMap<>();

    public static TrackerSecurityData load(CompoundTag tag, HolderLookup.Provider registries) {

	TrackerSecurityData data = new TrackerSecurityData();

	ListTag entries = tag.getList("entries", Tag.TAG_COMPOUND);

	for (Tag entryTag : entries) {

	    CompoundTag entry = (CompoundTag) entryTag;

	    if (!entry.hasUUID("uuid")) {
		continue;
	    }

	    data.revisions.put(entry.getUUID("uuid"), entry.getLong("revision"));
	}

	return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

	ListTag entries = new ListTag();

	for (Map.Entry<UUID, Long> entry : revisions.entrySet()) {

	    CompoundTag stored = new CompoundTag();

	    stored.putUUID("uuid", entry.getKey());
	    stored.putLong("revision", entry.getValue());

	    entries.add(stored);
	}

	tag.put("entries", entries);

	return tag;
    }

    public long getRevision(UUID uuid) {
	return revisions.getOrDefault(uuid, 0L);
    }

    public long incrementRevision(UUID uuid) {

	long revision = revisions.merge(uuid, 1L, Long::sum);
	setDirty();

	return revision;
    }

    public static TrackerSecurityData get(MinecraftServer server) {

	/*
	 * Always store this in the overworld data storage so tracking revisions are
	 * shared between every dimension.
	 */
	ServerLevel overworld = server.overworld();
	DimensionDataStorage storage = overworld.getDataStorage();

	return storage.computeIfAbsent(new Factory<>(TrackerSecurityData::new, TrackerSecurityData::load), DATA_NAME);
    }
}