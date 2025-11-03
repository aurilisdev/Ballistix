package ballistix.registers;

import java.util.UUID;

import com.mojang.serialization.Codec;

import ballistix.Ballistix;
import ballistix.common.item.ItemTracker;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixDataComponentTypes {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Ballistix.ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUND_FREQUENCY = DATA_COMPONENT_TYPES.register("frequency", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemTracker.Target>> TRACKER_TARGET = DATA_COMPONENT_TYPES.register("trackertarget", () -> DataComponentType.<ItemTracker.Target>builder().persistent(ItemTracker.Target.CODEC).networkSynchronized(ItemTracker.Target.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> TRACKER_UUID = DATA_COMPONENT_TYPES.register("trackeruuid", () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TRACKER_ID = DATA_COMPONENT_TYPES.register("trackerid", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).cacheEncoding().build());

}
