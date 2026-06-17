package ballistix.registers;

import ballistix.Ballistix;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT,
	    Ballistix.ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_ANTIMATTEREXPLOSION = sound("antimatterexplosion",
	    100);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_LARGE_ANTIMATTEREXPLOSION = sound(
	    "largeantimatterexplosion", "antimatterexplosion", 160);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_DARKMATTER = sound("darkmatter", 100);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_NUCLEAREXPLOSION = sound("nuclearexplosion", 90);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_EMPEXPLOSION = sound("empexplosion", 100);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_SONICEXPLOSION = sound("sonicexplosion", 50);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_HYPERSONICSONICEXPLOSION = sound(
	    "hypersonicexplosion", 70);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_ENDOTHERMICBEAM = sound("endothermicbeam", 70);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MISSILE_ROCKETLAUNCHER = sound(
	    "missile_launch_rocketlauncher", 32);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MISSILE_SILO = sound("missile_launch_silo", 64);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RADAR = sound("radar", 16);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_FIRECONTROLRADAR = sound("firecontrolradar", 32);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_CIWS_TURRETFIRING = sound("ciwsturretfiring", 32);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_LASER_TURRETFIRING = sound("laserturretfiring",
	    32);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RODHITTINGGROUND = sound("rodhittingground", 16);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RAILGUNKINETIC = sound("railgunkinetic", 16);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_VLSLAUNCH = sound("vlslaunch", 16);
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_AIRRAIDSIREN = sound("airraidsiren", 32);

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name, float range) {
	return sound(name, name, range);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name, String soundName, float range) {
	return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(Ballistix.rl(soundName), range));
    }
}
