package ballistix.common.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class TrackerSecurityData extends SavedData {

    private static final String DATA_NAME = "ballistix_tracker_security";

    private static final String ENTRIES = "entries";
    private static final String UUID_KEY = "uuid";
    private static final String REVISION_KEY = "revision";

    private final HashMap<UUID, Long> revisions = new HashMap<>();

    public static TrackerSecurityData load(CompoundTag tag) {

	TrackerSecurityData data = new TrackerSecurityData();

	ListTag entries = tag.getList(ENTRIES, Tag.TAG_COMPOUND);

	for (Tag entryTag : entries) {

	    CompoundTag entry = (CompoundTag) entryTag;

	    if (!entry.hasUUID(UUID_KEY)) {
		continue;
	    }

	    data.revisions.put(entry.getUUID(UUID_KEY), entry.getLong(REVISION_KEY));
	}

	return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {

	ListTag entries = new ListTag();

	for (Map.Entry<UUID, Long> entry : revisions.entrySet()) {

	    CompoundTag stored = new CompoundTag();

	    stored.putUUID(UUID_KEY, entry.getKey());
	    stored.putLong(REVISION_KEY, entry.getValue());

	    entries.add(stored);
	}

	tag.put(ENTRIES, entries);

	return tag;
    }

    public long getRevision(UUID uuid) {
	return revisions.getOrDefault(uuid, 0L);
    }

    public long incrementRevision(UUID uuid) {

	long revision = getRevision(uuid) + 1L;

	revisions.put(uuid, revision);

	/*
	 * Required so Minecraft saves the modified data.
	 */
	setDirty();

	return revision;
    }

    public static TrackerSecurityData get(MinecraftServer server) {

	/*
	 * Store the revisions in the overworld so every dimension shares the same
	 * scanner state.
	 */
	ServerLevel overworld = server.overworld();

	DimensionDataStorage storage = overworld.getDataStorage();

	return storage.computeIfAbsent(TrackerSecurityData::load, TrackerSecurityData::new, DATA_NAME);
    }
}