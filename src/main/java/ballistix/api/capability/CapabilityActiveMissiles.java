package ballistix.api.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ballistix.api.missile.virtual.VirtualMissile;
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

public class CapabilityActiveMissiles implements ICapabilitySerializable<CompoundNBT> {
	
	public final HashMap<RegistryKey<World>, HashMap<UUID, VirtualMissile>> activeMissiles = new HashMap<>();
	
	private final LazyOptional<CapabilityActiveMissiles> lazyOptional = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if(cap == BallistixCapabilities.ACTIVE_MISSILES) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT data = new CompoundNBT();

        data.putInt("size", activeMissiles.size());

        int i = 0;

        for (Map.Entry<RegistryKey<World>, HashMap<UUID, VirtualMissile>> entry : activeMissiles.entrySet()) {

            if(entry.getValue().size() <= 0) {
                continue;
            }

            CompoundNBT stored = new CompoundNBT();

            stored.putString("key", entry.getKey().location().toString());
            
            //ResourceLocation.CODEC.encode(entry.getKey().location(), NbtOps.INSTANCE, new CompoundNBT()).result().ifPresent(tag -> stored.put("key", tag));

            int activeSize = entry.getValue().size();

            stored.putInt("size", activeSize);

            int j = 0;

            for (VirtualMissile missile : entry.getValue().values()) {

                final int index = j;

                VirtualMissile.CODEC.encode(missile, NBTDynamicOps.INSTANCE, new CompoundNBT()).result().ifPresent(tag -> stored.put("" + index, tag));

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

            if(!stored.contains("key")) {
                continue;
            }

            RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(stored.getString("key")));

            HashMap<UUID, VirtualMissile> active = new HashMap<>();

            int activeSize = stored.getInt("size");

            for (int j = 0; j < activeSize; j++) {

                VirtualMissile virtual = VirtualMissile.CODEC.decode(NBTDynamicOps.INSTANCE, stored.getCompound("" + j)).result().get().getFirst();

                active.put(virtual.getId(), virtual);
            }

           activeMissiles.put(key, active);

        }
	}
	
}
