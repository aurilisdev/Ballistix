package ballistix.registers;

import ballistix.References;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixSounds {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, References.ID);

	public static final RegistryObject<SoundEvent> SOUND_ANTIMATTEREXPLOSION = sound("antimatterexplosion");
	public static final RegistryObject<SoundEvent> SOUND_LARGE_ANTIMATTEREXPLOSION = sound("largeantimatterexplosion", "antimatterexplosion");
	public static final RegistryObject<SoundEvent> SOUND_DARKMATTER = sound("darkmatter");
	public static final RegistryObject<SoundEvent> SOUND_NUCLEAREXPLOSION = sound("nuclearexplosion");
	public static final RegistryObject<SoundEvent> SOUND_EMPEXPLOSION = sound("empexplosion");
	public static final RegistryObject<SoundEvent> SOUND_MISSILE_ROCKETLAUNCHER = sound("missile_launch_rocketlauncher");
	public static final RegistryObject<SoundEvent> SOUND_MISSILE_SILO = sound("missile_launch_silo");
	public static final RegistryObject<SoundEvent> SOUND_RADAR = sound("radar");
	public static final RegistryObject<SoundEvent> SOUND_FIRECONTROLRADAR = sound("firecontrolradar");
	public static final RegistryObject<SoundEvent> SOUND_CIWS_TURRETFIRING = sound("ciwsturretfiring");
	public static final RegistryObject<SoundEvent> SOUND_LASER_TURRETFIRING = sound("laserturretfiring");
	
	private static RegistryObject<SoundEvent> sound(String name) {
		return sound(name, name);
	}

	private static RegistryObject<SoundEvent> sound(String name, String soundName) {
		return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(References.ID + ":" + name)));
	}
}
