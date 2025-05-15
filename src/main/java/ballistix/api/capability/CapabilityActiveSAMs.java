package ballistix.api.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.virtual.VirtualProjectile;
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

public class CapabilityActiveSAMs implements ICapabilitySerializable<CompoundTag> {

	public final HashMap<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>> activeSAMs = new HashMap<>();

	private final LazyOptional<CapabilityActiveSAMs> lazyOptional = LazyOptional.of(() -> this);

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == BallistixCapabilities.ACTIVE_SAMS) {
			return lazyOptional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag data = new CompoundTag();

        data.putInt("size", activeSAMs.size());

        int i = 0;

        for (Map.Entry<ResourceKey<Level>, HashMap<UUID, VirtualProjectile.VirtualSAM>> entry : activeSAMs.entrySet()) {

            if(entry.getValue().size() <= 0) {
                continue;
            }

            CompoundTag stored = new CompoundTag();

            stored.putString("key", entry.getKey().location().toString());

            int activeSize = entry.getValue().size();

            stored.putInt("size", activeSize);

            int j = 0;

            for (VirtualProjectile.VirtualSAM missile : entry.getValue().values()) {

                final int index = j;

                VirtualProjectile.VirtualSAM.CODEC.encode(missile, NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> stored.put("" + index, tag));

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

            HashMap<UUID, VirtualProjectile.VirtualSAM> active = new HashMap<>();

            int activeSize = stored.getInt("size");

            for (int j = 0; j < activeSize; j++) {

                VirtualProjectile.VirtualSAM virtual = VirtualProjectile.VirtualSAM.CODEC.decode(NbtOps.INSTANCE, stored.getCompound("" + j)).result().get().getFirst();

                active.put(virtual.id, virtual);
            }

            activeSAMs.put(key, active);

        }
		
	}

}
