package ballistix.api.capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilitySiloRegistry implements ICapabilitySerializable<CompoundNBT> {

	private final LazyOptional<CapabilitySiloRegistry> lazyOptional = LazyOptional.of(() -> this);

	private HashMap<Integer, HashSet<BlockPos>> siloMap = new HashMap<>();

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == BallistixCapabilities.SILO_REGISTRY) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {

		CompoundNBT tag = new CompoundNBT();

		tag.putIntArray("frequencies", new ArrayList<>(siloMap.keySet()));

		siloMap.forEach((key, value) -> {

			CompoundNBT posTag = new CompoundNBT();

			List<BlockPos> silos = new ArrayList<>(value);

			posTag.putInt("size", silos.size());

			for (int i = 0; i < silos.size(); i++) {

				posTag.put("blockpos" + i, NBTUtil.writeBlockPos(silos.get(i)));

			}

			tag.put("frequencytag" + (int) key, posTag);

		});

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt == null) {
			return;
		}

		int[] frequencies = nbt.getIntArray("frequencies");

		for (int freq : frequencies) {

			HashSet<BlockPos> poses = siloMap.getOrDefault(freq, new HashSet<>());

			CompoundNBT posTag = nbt.getCompound("frequencytag" + freq);

			int size = posTag.getInt("size");

			for (int i = 0; i < size; i++) {

				poses.add(NBTUtil.readBlockPos(posTag.getCompound("blockpos" + i)));

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