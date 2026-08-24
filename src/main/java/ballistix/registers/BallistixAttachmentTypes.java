package ballistix.registers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import ballistix.Ballistix;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.missile.virtual.VirtualProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BallistixAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
	    .create(NeoForgeRegistries.ATTACHMENT_TYPES, Ballistix.ID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConcurrentHashMap<Integer, Set<BlockPos>>>> SILO_FREQUENCIES = ATTACHMENT_TYPES
	    .register("silofrequencies", () -> AttachmentType.builder(() -> new ConcurrentHashMap<Integer, Set<BlockPos>>())
		    .serialize(new IAttachmentSerializer<CompoundTag, ConcurrentHashMap<Integer, Set<BlockPos>>>() {
			@Override
			public ConcurrentHashMap<Integer, Set<BlockPos>> read(IAttachmentHolder holder, CompoundTag tag,
				HolderLookup.Provider provider) {
			    ConcurrentHashMap<Integer, Set<BlockPos>> data = new ConcurrentHashMap<>();

			    int size = tag.getInt("size");
			    for (int i = 0; i < size; i++) {

				CompoundTag stored = tag.getCompound("" + i);

				int freq = stored.getInt("freq");

				int setSize = stored.getInt("setsize");

				Set<BlockPos> tiles = ConcurrentHashMap.newKeySet();

				for (int j = 0; j < setSize; j++) {
				    BlockPos.CODEC.decode(NbtOps.INSTANCE, stored.get("pos" + j))
					    .ifSuccess(pair -> tiles.add(pair.getFirst()));
				}

				data.put(freq, tiles);
			    }

			    return data;
			}

			@Override
			public @Nullable CompoundTag write(ConcurrentHashMap<Integer, Set<BlockPos>> attachment,
				HolderLookup.Provider provider) {
			    CompoundTag data = new CompoundTag();
			    int size = attachment.size();
			    data.putInt("size", size);
			    int i = 0;
			    for (Map.Entry<Integer, Set<BlockPos>> entry : attachment.entrySet()) {

				CompoundTag store = new CompoundTag();

				store.putInt("freq", entry.getKey());

				Set<BlockPos> tiles = entry.getValue();

				store.putInt("setsize", tiles.size());

				int j = 0;

				for (BlockPos pos : tiles) {
				    final int index = j;

				    BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos)
					    .ifSuccess(tag -> store.put("pos" + index, tag));
				    j++;

				}

				data.put(i + "", store);

				i++;
			    }
			    return data;
			}
		    }).build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualMissile>>>> ACTIVE_MISSILES = ATTACHMENT_TYPES
	    .register("activemissiles", () -> AttachmentType
		    .builder(() -> new ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualMissile>>()).serialize(
			    new IAttachmentSerializer<CompoundTag, ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualMissile>>>() {

				private static final Codec<ResourceKey<Level>> CODEC = ResourceKey
					.codec(Registries.DIMENSION);

				@Override
				public ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualMissile>> read(
					IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
				    ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualMissile>> data = new ConcurrentHashMap<>();

				    int size = tag.getInt("size");

				    for (int i = 0; i < size; i++) {

					CompoundTag stored = tag.getCompound("" + i);

					if (!stored.contains("key")) {
					    continue;
					}

					ResourceKey<Level> key = CODEC.decode(NbtOps.INSTANCE, stored.get("key"))
						.getOrThrow().getFirst();

					ConcurrentHashMap<UUID, VirtualMissile> active = new ConcurrentHashMap<>();

					int activeSize = stored.getInt("size");

					for (int j = 0; j < activeSize; j++) {

					    VirtualMissile virtual = VirtualMissile.CODEC
						    .decode(NbtOps.INSTANCE, stored.getCompound("" + j)).getOrThrow()
						    .getFirst();

					    active.put(virtual.getId(), virtual);
					}

					data.put(key, active);

				    }

				    return data;
				}

				@Override
				public @Nullable CompoundTag write(
					ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualMissile>> attachment,
					HolderLookup.Provider provider) {

				    CompoundTag data = new CompoundTag();

				    data.putInt("size", attachment.size());

				    int i = 0;

				    for (Map.Entry<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualMissile>> entry : attachment
					    .entrySet()) {

					if (entry.getValue().size() <= 0) {
					    continue;
					}

					CompoundTag stored = new CompoundTag();

					CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey())
						.ifSuccess(tag -> stored.put("key", tag));

					int activeSize = entry.getValue().size();

					stored.putInt("size", activeSize);

					int j = 0;

					for (VirtualMissile missile : entry.getValue().values()) {

					    final int index = j;

					    VirtualMissile.CODEC.encodeStart(NbtOps.INSTANCE, missile)
						    .ifSuccess(tag -> stored.put("" + index, tag));

					    j++;

					}

					data.put("" + i, stored);

					i++;

				    }

				    return data;
				}

			    })
		    .build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet>>>> ACTIVE_BULLETS = ATTACHMENT_TYPES
	    .register("activebullets", () -> AttachmentType
		    .builder(() -> new ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet>>())
		    .serialize(
			    new IAttachmentSerializer<CompoundTag, ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet>>>() {

				private static final Codec<ResourceKey<Level>> CODEC = ResourceKey
					.codec(Registries.DIMENSION);

				@Override
				public ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet>> read(
					IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
				    ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet>> data = new ConcurrentHashMap<>();

				    int size = tag.getInt("size");

				    for (int i = 0; i < size; i++) {

					CompoundTag stored = tag.getCompound("" + i);

					if (!stored.contains("key")) {
					    continue;
					}

					ResourceKey<Level> key = CODEC.decode(NbtOps.INSTANCE, stored.get("key"))
						.getOrThrow().getFirst();

					ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet> active = new ConcurrentHashMap<>();

					int activeSize = stored.getInt("size");

					for (int j = 0; j < activeSize; j++) {

					    VirtualProjectile.VirtualBullet virtual = VirtualProjectile.VirtualBullet.CODEC
						    .decode(NbtOps.INSTANCE, stored.getCompound("" + j)).getOrThrow()
						    .getFirst();

					    active.put(virtual.id, virtual);
					}

					data.put(key, active);

				    }

				    return data;
				}

				@Override
				public @Nullable CompoundTag write(
					ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet>> attachment,
					HolderLookup.Provider provider) {

				    CompoundTag data = new CompoundTag();

				    data.putInt("size", attachment.size());

				    int i = 0;

				    for (Map.Entry<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualBullet>> entry : attachment
					    .entrySet()) {

					if (entry.getValue().size() <= 0) {
					    continue;
					}

					CompoundTag stored = new CompoundTag();

					CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey())
						.ifSuccess(tag -> stored.put("key", tag));

					int activeSize = entry.getValue().size();

					stored.putInt("size", activeSize);

					int j = 0;

					for (VirtualProjectile.VirtualBullet missile : entry.getValue().values()) {

					    final int index = j;

					    VirtualProjectile.VirtualBullet.CODEC.encodeStart(NbtOps.INSTANCE, missile)
						    .ifSuccess(tag -> stored.put("" + index, tag));

					    j++;

					}

					data.put("" + i, stored);

					i++;

				    }

				    return data;
				}

			    })
		    .build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound>>>> ACTIVE_RAILGUNROUNDS = ATTACHMENT_TYPES
	    .register("activerailgunrounds", () -> AttachmentType.builder(
		    () -> new ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound>>())
		    .serialize(
			    new IAttachmentSerializer<CompoundTag, ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound>>>() {

				private static final Codec<ResourceKey<Level>> CODEC = ResourceKey
					.codec(Registries.DIMENSION);

				@Override
				public ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound>> read(
					IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
				    ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound>> data = new ConcurrentHashMap<>();

				    int size = tag.getInt("size");

				    for (int i = 0; i < size; i++) {

					CompoundTag stored = tag.getCompound("" + i);

					if (!stored.contains("key")) {
					    continue;
					}

					ResourceKey<Level> key = CODEC.decode(NbtOps.INSTANCE, stored.get("key"))
						.getOrThrow().getFirst();

					ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound> active = new ConcurrentHashMap<>();

					int activeSize = stored.getInt("size");

					for (int j = 0; j < activeSize; j++) {

					    VirtualProjectile.VirtualRailgunRound virtual = VirtualProjectile.VirtualRailgunRound.CODEC
						    .decode(NbtOps.INSTANCE, stored.getCompound("" + j)).getOrThrow()
						    .getFirst();

					    active.put(virtual.id, virtual);
					}

					data.put(key, active);

				    }

				    return data;
				}

				@Override
				public @Nullable CompoundTag write(
					ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound>> attachment,
					HolderLookup.Provider provider) {

				    CompoundTag data = new CompoundTag();

				    data.putInt("size", attachment.size());

				    int i = 0;

				    for (Map.Entry<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualRailgunRound>> entry : attachment
					    .entrySet()) {

					if (entry.getValue().size() <= 0) {
					    continue;
					}

					CompoundTag stored = new CompoundTag();

					CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey())
						.ifSuccess(tag -> stored.put("key", tag));

					int activeSize = entry.getValue().size();

					stored.putInt("size", activeSize);

					int j = 0;

					for (VirtualProjectile.VirtualRailgunRound missile : entry.getValue()
						.values()) {

					    final int index = j;

					    VirtualProjectile.VirtualRailgunRound.CODEC
						    .encodeStart(NbtOps.INSTANCE, missile)
						    .ifSuccess(tag -> stored.put("" + index, tag));

					    j++;

					}

					data.put("" + i, stored);

					i++;

				    }

				    return data;
				}

			    })
		    .build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM>>>> ACTIVE_SAMS = ATTACHMENT_TYPES
	    .register("activesams", () -> AttachmentType
		    .builder(() -> new ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM>>())
		    .serialize(
			    new IAttachmentSerializer<CompoundTag, ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM>>>() {

				private static final Codec<ResourceKey<Level>> CODEC = ResourceKey
					.codec(Registries.DIMENSION);

				@Override
				public ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM>> read(
					IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
				    ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM>> data = new ConcurrentHashMap<>();

				    int size = tag.getInt("size");

				    for (int i = 0; i < size; i++) {

					CompoundTag stored = tag.getCompound("" + i);

					if (!stored.contains("key")) {
					    continue;
					}

					ResourceKey<Level> key = CODEC.decode(NbtOps.INSTANCE, stored.get("key"))
						.getOrThrow().getFirst();

					ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM> active = new ConcurrentHashMap<>();

					int activeSize = stored.getInt("size");

					for (int j = 0; j < activeSize; j++) {

					    VirtualProjectile.VirtualSAM virtual = VirtualProjectile.VirtualSAM.CODEC
						    .decode(NbtOps.INSTANCE, stored.getCompound("" + j)).getOrThrow()
						    .getFirst();

					    active.put(virtual.id, virtual);
					}

					data.put(key, active);

				    }

				    return data;
				}

				@Override
				public @Nullable CompoundTag write(
					ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM>> attachment,
					HolderLookup.Provider provider) {

				    CompoundTag data = new CompoundTag();

				    data.putInt("size", attachment.size());

				    int i = 0;

				    for (Map.Entry<ResourceKey<Level>, ConcurrentHashMap<UUID, VirtualProjectile.VirtualSAM>> entry : attachment
					    .entrySet()) {

					if (entry.getValue().size() <= 0) {
					    continue;
					}

					CompoundTag stored = new CompoundTag();

					CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey())
						.ifSuccess(tag -> stored.put("key", tag));

					int activeSize = entry.getValue().size();

					stored.putInt("size", activeSize);

					int j = 0;

					for (VirtualProjectile.VirtualSAM missile : entry.getValue().values()) {

					    final int index = j;

					    VirtualProjectile.VirtualSAM.CODEC.encodeStart(NbtOps.INSTANCE, missile)
						    .ifSuccess(tag -> stored.put("" + index, tag));

					    j++;

					}

					data.put("" + i, stored);

					i++;

				    }

				    return data;
				}

			    })
		    .build());

}
