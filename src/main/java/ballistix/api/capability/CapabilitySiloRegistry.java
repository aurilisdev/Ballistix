package ballistix.api.capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilitySiloRegistry implements ICapabilitySerializable<CompoundTag> {

	private final LazyOptional<CapabilitySiloRegistry> lazyOptional = LazyOptional.of(() -> this);

	private HashMap<Integer, HashSet<BlockPos>> siloMap = new HashMap<>();

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == BallistixCapabilities.SILO_REGISTRY) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {

		CompoundTag tag = new CompoundTag();

		tag.putIntArray("frequencies", new ArrayList<>(siloMap.keySet()));

		siloMap.forEach((key, value) -> {

			CompoundTag posTag = new CompoundTag();

			List<BlockPos> silos = new ArrayList<>(value);

			posTag.putInt("size", silos.size());

			for (int i = 0; i < silos.size(); i++) {

				posTag.put("blockpos" + i, NbtUtils.writeBlockPos(silos.get(i)));

			}

			tag.put("frequencytag" + (int) key, posTag);

		});

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt == null) {
			return;
		}

		int[] frequencies = nbt.getIntArray("frequencies");

		for (int freq : frequencies) {

			HashSet<BlockPos> poses = siloMap.getOrDefault(freq, new HashSet<>());

			CompoundTag posTag = nbt.getCompound("frequencytag" + freq);

			int size = posTag.getInt("size");

			for (int i = 0; i < size; i++) {

				poses.add(NbtUtils.readBlockPos(posTag.getCompound("blockpos" + i)));

			}

			siloMap.put(freq, poses);

		}

	}

	public void addSilo(int freq, BlockPos silo) {
		HashSet<BlockPos> set = siloMap.getOrDefault(freq, new HashSet<>());

		set.add(silo);

		siloMap.put(freq, set);

	}

	public void removeSilo(int freq, BlockPos silo) {

		HashSet<BlockPos> set = siloMap.getOrDefault(freq, new HashSet<>());

		set.remove(silo);

		siloMap.put(freq, set);

	}

	public HashSet<BlockPos> getSilosForFrequency(int freq) {
		return siloMap.getOrDefault(freq, new HashSet<>());
	}

}
