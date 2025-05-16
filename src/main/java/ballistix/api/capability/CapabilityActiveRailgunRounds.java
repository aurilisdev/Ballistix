package ballistix.api.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import ballistix.api.missile.virtual.VirtualProjectile;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityActiveRailgunRounds implements ICapabilitySerializable<CompoundNBT> {

	public final HashMap<RegistryKey<World>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> activeRailgunRounds = new HashMap<>();

	private final LazyOptional<CapabilityActiveRailgunRounds> lazyOptional = LazyOptional.of(() -> this);

	@Override
	public <T> LazyOptional<T> getCapability(@Nullable Capability<T> cap, @Nullable Direction side) {
		if (cap == BallistixCapabilities.ACTIVE_RAILGUN_ROUNDS) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT data = new CompoundNBT();

		data.putInt("size", activeRailgunRounds.size());

		int i = 0;

		for (Map.Entry<RegistryKey<World>, HashMap<UUID, VirtualProjectile.VirtualRailgunRound>> entry : activeRailgunRounds.entrySet()) {

			if (entry.getValue().size() <= 0) {
				continue;
			}

			CompoundNBT stored = new CompoundNBT();

			stored.putString("key", entry.getKey().location().toString());

			int activeSize = entry.getValue().size();

			stored.putInt("size", activeSize);

			int j = 0;

			for (VirtualProjectile.VirtualRailgunRound missile : entry.getValue().values()) {

				final int index = j;

				VirtualProjectile.VirtualRailgunRound.CODEC.encode(missile, NBTDynamicOps.INSTANCE, new CompoundNBT()).result().ifPresent(tag -> stored.put("" + index, tag));

				j++;

			}

			data.put("" + i, stored);

			i++;

		}

		return data;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt == null) {
			return;
		}

		int size = nbt.getInt("size");

		for (int i = 0; i < size; i++) {

			CompoundNBT stored = nbt.getCompound("" + i);

			if (!stored.contains("key")) {
				continue;
			}

			RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(stored.getString("key")));

			HashMap<UUID, VirtualProjectile.VirtualRailgunRound> active = new HashMap<>();

			int activeSize = stored.getInt("size");

			for (int j = 0; j < activeSize; j++) {

				VirtualProjectile.VirtualRailgunRound virtual = VirtualProjectile.VirtualRailgunRound.CODEC.decode(NBTDynamicOps.INSTANCE, stored.getCompound("" + j)).result().get().getFirst();

				active.put(virtual.id, virtual);
			}

			activeRailgunRounds.put(key, active);

		}
	}

}
