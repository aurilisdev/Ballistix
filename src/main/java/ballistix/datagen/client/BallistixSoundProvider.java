package ballistix.datagen.client;

import ballistix.References;
import ballistix.registers.BallistixSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinition.Sound;
import net.minecraftforge.common.data.SoundDefinition.SoundType;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

public class BallistixSoundProvider extends SoundDefinitionsProvider {

	public BallistixSoundProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, References.ID, helper);
	}

	@Override
	public void registerSounds() {
		add(BallistixSounds.SOUND_ANTIMATTEREXPLOSION);
		add(BallistixSounds.SOUND_DARKMATTER);
		add(BallistixSounds.SOUND_NUCLEAREXPLOSION);
		add(BallistixSounds.SOUND_EMPEXPLOSION);
		add(BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER);
		add(BallistixSounds.SOUND_MISSILE_SILO);
		add(BallistixSounds.SOUND_RADAR);
		add(BallistixSounds.SOUND_FIRECONTROLRADAR);
		add(BallistixSounds.SOUND_CIWS_TURRETFIRING);
		add(BallistixSounds.SOUND_LASER_TURRETFIRING);
	}	

	private void add(RegistryObject<SoundEvent> sound) {
		add(sound.get(), SoundDefinition.definition().subtitle("subtitles." + References.ID + "." + sound.getId().getPath()).with(Sound.sound(sound.getId(), SoundType.SOUND)));
	}

}
