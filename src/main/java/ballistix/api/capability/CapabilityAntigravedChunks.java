package ballistix.api.capability;

import java.util.HashSet;

import javax.annotation.Nullable;

import ballistix.api.blast.AntigravedChunk;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityAntigravedChunks implements ICapabilitySerializable<CompoundNBT> {
	
	public final HashSet<AntigravedChunk> activeChunks = new HashSet<>();
	
	private final LazyOptional<CapabilityAntigravedChunks> lazyOptional = LazyOptional.of(() -> this);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if(cap == BallistixCapabilities.ANTIGRAVED_CHUNKS) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT data = new CompoundNBT();

        data.putInt("size", activeChunks.size());

        int i = 0;

        for (AntigravedChunk chunk : activeChunks) {

            final int index = i;

            AntigravedChunk.CODEC.encodeStart(NBTDynamicOps.INSTANCE, chunk).result().ifPresent(tag -> data.put("" + index, tag));

            i++;

        }

        return data;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		activeChunks.clear();

        int size = tag.getInt("size");

        for (int i = 0; i < size; i++) {

            if(!tag.contains("" + i)) {
                continue;
            }

            AntigravedChunk.CODEC.decode(NBTDynamicOps.INSTANCE, tag.get("" + i)).result().ifPresent(pair -> activeChunks.add(pair.getFirst()));

        }
	}

}
