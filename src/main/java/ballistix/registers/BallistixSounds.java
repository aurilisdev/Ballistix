package ballistix.registers;

import ballistix.References;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixSounds {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, References.ID);

	public static final RegistryObject<SoundEvent> SOUND_ANTIMATTEREXPLOSION = sound("antimatterexplosion", 100);
	public static final RegistryObject<SoundEvent> SOUND_LARGE_ANTIMATTEREXPLOSION = sound("largeantimatterexplosion", "antimatterexplosion", 160);
	public static final RegistryObject<SoundEvent> SOUND_DARKMATTER = sound("darkmatter", 100);
	public static final RegistryObject<SoundEvent> SOUND_NUCLEAREXPLOSION = sound("nuclearexplosion", 90);
	public static final RegistryObject<SoundEvent> SOUND_EMPEXPLOSION = sound("empexplosion", 100);
	public static final RegistryObject<SoundEvent> SOUND_MISSILE_ROCKETLAUNCHER = sound("missile_launch_rocketlauncher", 32);
	public static final RegistryObject<SoundEvent> SOUND_MISSILE_SILO = sound("missile_launch_silo", 64);
	public static final RegistryObject<SoundEvent> SOUND_RADAR = sound("radar", 16);
	public static final RegistryObject<SoundEvent> SOUND_FIRECONTROLRADAR = sound("firecontrolradar", 32);
	public static final RegistryObject<SoundEvent> SOUND_CIWS_TURRETFIRING = sound("ciwsturretfiring", 32);
	public static final RegistryObject<SoundEvent> SOUND_LASER_TURRETFIRING = sound("laserturretfiring", 32);

	private static RegistryObject<SoundEvent> sound(String name, float range) {
		return sound(name, name, range);
	}

	private static RegistryObject<SoundEvent> sound(String name, String soundName, float range) {
		return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(References.ID + ":" + name), range));
	}
}
