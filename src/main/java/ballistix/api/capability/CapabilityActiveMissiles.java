package ballistix.api.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.registers.BallistixCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityActiveMissiles implements ICapabilitySerializable<CompoundTag> {
	
	public final HashMap<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> activeMissiles = new HashMap<>();
	
	private final LazyOptional<CapabilityActiveMissiles> lazyOptional = LazyOptional.of(() -> this);
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap == BallistixCapabilities.ACTIVE_MISSILES) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag data = new CompoundTag();

        data.putInt("size", activeMissiles.size());

        int i = 0;

        for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualMissile>> entry : activeMissiles.entrySet()) {

            if(entry.getValue().size() <= 0) {
                continue;
            }

            CompoundTag stored = new CompoundTag();

            stored.putString("key", entry.getKey().location().toString());
            
            //ResourceLocation.CODEC.encode(entry.getKey().location(), NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> stored.put("key", tag));

            int activeSize = entry.getValue().size();

            stored.putInt("size", activeSize);

            int j = 0;

            for (VirtualMissile missile : entry.getValue().values()) {

                final int index = j;

                VirtualMissile.CODEC.encode(missile, NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> stored.put("" + index, tag));

                j++;

            }

            data.put("" + i, stored);

            i++;

        }

        return data;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt == null) {
			return;
		}
		
		int size = nbt.getInt("size");

        for (int i = 0; i < size; i++) {

            CompoundTag stored = nbt.getCompound("" + i);

            if(!stored.contains("key")) {
                continue;
            }

            ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(stored.getString("key")));

            HashMap<UUID, VirtualMissile> active = new HashMap<>();

            int activeSize = stored.getInt("size");

            for (int j = 0; j < activeSize; j++) {

                VirtualMissile virtual = VirtualMissile.CODEC.decode(NbtOps.INSTANCE, stored.getCompound("" + j)).result().get().getFirst();

                active.put(virtual.getId(), virtual);
            }

           activeMissiles.put(key, active);

        }
	}
	
}
