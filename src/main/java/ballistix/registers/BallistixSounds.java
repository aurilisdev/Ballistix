package ballistix.registers;

import com.google.common.base.Supplier;

import ballistix.References;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixSounds {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, References.ID);
	public static final RegistryObject<SoundEvent> SOUND_ANTIMATTEREXPLOSION = SOUNDS.register("antimatterexplosion", supplier(new SoundEvent(new ResourceLocation(References.ID + ":antimatterexplosion"))));
	public static final RegistryObject<SoundEvent> SOUND_DARKMATTER = SOUNDS.register("darkmatter", supplier(new SoundEvent(new ResourceLocation(References.ID + ":darkmatter"))));
	public static final RegistryObject<SoundEvent> SOUND_NUCLEAREXPLOSION = SOUNDS.register("nuclearexplosion", supplier(new SoundEvent(new ResourceLocation(References.ID + ":nuclearexplosion"))));

	private static <T> Supplier<? extends T> supplier(T entry) {
		return () -> entry;
	}
}
