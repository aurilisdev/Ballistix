package ballistix.registers;

import ballistix.References;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixSounds {
	
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, References.ID);
	
	public static final RegistryObject<SoundEvent> SOUND_ANTIMATTEREXPLOSION = sound("antimatterexplosion");
	public static final RegistryObject<SoundEvent> SOUND_DARKMATTER = sound("darkmatter");
	public static final RegistryObject<SoundEvent> SOUND_NUCLEAREXPLOSION = sound("nuclearexplosion");

	private static RegistryObject<SoundEvent> sound(String name) {
		return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(References.ID + ":" + name)));
	}
}
