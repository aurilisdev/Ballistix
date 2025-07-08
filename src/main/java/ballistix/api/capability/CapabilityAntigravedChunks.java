package ballistix.api.capability;

import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ballistix.api.blast.AntigravedChunk;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityAntigravedChunks implements ICapabilitySerializable<CompoundTag> {
	
	public final HashSet<AntigravedChunk> activeChunks = new HashSet<>();
	
	private final LazyOptional<CapabilityAntigravedChunks> lazyOptional = LazyOptional.of(() -> this);

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap == BallistixCapabilities.ANTIGRAVED_CHUNKS) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag data = new CompoundTag();

        data.putInt("size", activeChunks.size());

        int i = 0;

        for (AntigravedChunk chunk : activeChunks) {

            final int index = i;

            AntigravedChunk.CODEC.encodeStart(NbtOps.INSTANCE, chunk).result().ifPresent(tag -> data.put("" + index, tag));

            i++;

        }

        return data;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		activeChunks.clear();

        int size = tag.getInt("size");

        for (int i = 0; i < size; i++) {

            if(!tag.contains("" + i)) {
                continue;
            }

            AntigravedChunk.CODEC.decode(NbtOps.INSTANCE, tag.get("" + i)).result().ifPresent(pair -> activeChunks.add(pair.getFirst()));

        }
	}

}
