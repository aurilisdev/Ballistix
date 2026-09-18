package ballistix.registers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;

import ballistix.Ballistix;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.missile.virtual.VirtualProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BallistixAttachmentTypes {

    private static final String SIZE = "size";
    private static final String FREQUENCY = "freq";
    private static final String SET_SIZE = "setsize";
    private static final String POSITION = "pos";
    private static final String DIMENSION = "key";
    private static final Codec<ResourceKey<Level>> LEVEL_CODEC = ResourceKey.codec(Registries.DIMENSION);

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
	    .create(NeoForgeRegistries.ATTACHMENT_TYPES, Ballistix.ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<Integer, HashSet<BlockPos>>>> SILO_FREQUENCIES = ATTACHMENT_TYPES
	    .register("silofrequencies",
		    () -> AttachmentType
			    .<HashMap<Integer, HashSet<BlockPos>>>builder(BallistixAttachmentTypes::newHashMap)
			    .serialize(new IAttachmentSerializer<CompoundTag, HashMap<Integer, HashSet<BlockPos>>>() {
				@Override
				public HashMap<Integer, HashSet<BlockPos>> read(IAttachmentHolder holder,
					CompoundTag tag, HolderLookup.Provider provider) {
				    HashMap<Integer, HashSet<BlockPos>> data = newHashMap();
				    int size = tag.getInt(SIZE);

				    for (int i = 0; i < size; i++) {
					CompoundTag stored = tag.getCompound(Integer.toString(i));
					int frequency = stored.getInt(FREQUENCY);
					HashSet<BlockPos> tiles = new HashSet<>();
					int setSize = stored.getInt(SET_SIZE);

					for (int j = 0; j < setSize; j++) {
					    tiles.add(decode(BlockPos.CODEC, getRequired(stored, POSITION + j)));
					}

					data.put(frequency, tiles);
				    }

				    return data;
				}

				@Override
				public @Nullable CompoundTag write(HashMap<Integer, HashSet<BlockPos>> attachment,
					HolderLookup.Provider provider) {
				    CompoundTag data = new CompoundTag();
				    data.putInt(SIZE, attachment.size());

				    int i = 0;
				    for (Map.Entry<Integer, HashSet<BlockPos>> entry : attachment.entrySet()) {
					CompoundTag stored = new CompoundTag();
					stored.putInt(FREQUENCY, entry.getKey());
					stored.putInt(SET_SIZE, entry.getValue().size());

					int j = 0;
					for (BlockPos pos : entry.getValue()) {
					    stored.put(POSITION + j++, encode(BlockPos.CODEC, pos));
					}

					data.put(Integer.toString(i++), stored);
				    }

				    return data;
				}
			    }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>>>> ACTIVE_MISSILES = ATTACHMENT_TYPES
	    .register("activemissiles",
		    () -> AttachmentType
			    .<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>>>builder(
				    BallistixAttachmentTypes::newHashMap)
			    .serialize(virtualMapSerializer(VirtualMissile.CODEC, VirtualMissile::getId)).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualBullet>>>> ACTIVE_BULLETS = ATTACHMENT_TYPES
	    .register("activebullets",
		    () -> AttachmentType.<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualBullet>>>builder(
			    BallistixAttachmentTypes::newHashMap)
			    .serialize(virtualMapSerializer(VirtualProjectile.VirtualBullet.CODEC, bullet -> bullet.id))
			    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>>>> ACTIVE_RAILGUNROUNDS = ATTACHMENT_TYPES
	    .register("activerailgunrounds",
		    () -> AttachmentType.<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>>>builder(
			    BallistixAttachmentTypes::newHashMap)
			    .serialize(virtualMapSerializer(VirtualProjectile.VirtualRailgunRound.CODEC,
				    round -> round.id))
			    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>>>> ACTIVE_SAMS = ATTACHMENT_TYPES
	    .register("activesams",
		    () -> AttachmentType.<HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>>>builder(
			    BallistixAttachmentTypes::newHashMap)
			    .serialize(virtualMapSerializer(VirtualProjectile.VirtualSAM.CODEC, sam -> sam.id))
			    .build());

    private static <T> IAttachmentSerializer<CompoundTag, HashMap<ResourceKey<Level>, HashMap<UUID, T>>> virtualMapSerializer(
	    Codec<T> codec, Function<T, UUID> idGetter) {
	return new IAttachmentSerializer<CompoundTag, HashMap<ResourceKey<Level>, HashMap<UUID, T>>>() {
	    @Override
	    public HashMap<ResourceKey<Level>, HashMap<UUID, T>> read(IAttachmentHolder holder, CompoundTag tag,
		    HolderLookup.Provider provider) {
		HashMap<ResourceKey<Level>, HashMap<UUID, T>> data = newHashMap();
		int size = tag.getInt(SIZE);

		for (int i = 0; i < size; i++) {
		    CompoundTag stored = tag.getCompound(Integer.toString(i));
		    ResourceKey<Level> dimension = decode(LEVEL_CODEC, getRequired(stored, DIMENSION));
		    HashMap<UUID, T> active = newHashMap();
		    int activeSize = stored.getInt(SIZE);

		    for (int j = 0; j < activeSize; j++) {
			T virtual = decode(codec, getRequired(stored, Integer.toString(j)));
			active.put(idGetter.apply(virtual), virtual);
		    }

		    data.put(dimension, active);
		}

		return data;
	    }

	    @Override
	    public @Nullable CompoundTag write(HashMap<ResourceKey<Level>, HashMap<UUID, T>> attachment,
		    HolderLookup.Provider provider) {
		CompoundTag data = new CompoundTag();
		data.putInt(SIZE, attachment.size());

		int i = 0;
		for (Map.Entry<ResourceKey<Level>, HashMap<UUID, T>> entry : attachment.entrySet()) {
		    CompoundTag stored = new CompoundTag();
		    stored.put(DIMENSION, encode(LEVEL_CODEC, entry.getKey()));
		    stored.putInt(SIZE, entry.getValue().size());

		    int j = 0;
		    for (T virtual : entry.getValue().values()) {
			stored.put(Integer.toString(j++), encode(codec, virtual));
		    }

		    data.put(Integer.toString(i++), stored);
		}

		return data;
	    }
	};
    }

    private static <K, V> HashMap<K, V> newHashMap() {
	return new HashMap<>();
    }

    private static Tag getRequired(CompoundTag tag, String key) {
	Tag value = tag.get(key);

	if (value == null)
	    throw new IllegalStateException("Missing attachment field: " + key);

	return value;
    }

    private static <T> T decode(Codec<T> codec, Tag tag) {
	return codec.parse(new Dynamic<>(NbtOps.INSTANCE, tag)).getOrThrow();
    }

    private static <T> Tag encode(Codec<T> codec, T value) {
	return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
    }

}
