package ballistix.datagen.client;

import ballistix.References;
import ballistix.registers.BallistixSounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinition.Sound;
import net.minecraftforge.common.data.SoundDefinition.SoundType;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.fml.RegistryObject;

public class BallistixSoundProvider extends SoundDefinitionsProvider {

	public BallistixSoundProvider(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, References.ID, helper);
	}

	@Override
	public void registerSounds() {
		add(BallistixSounds.SOUND_ANTIMATTEREXPLOSION);
		add(BallistixSounds.SOUND_DARKMATTER);
		add(BallistixSounds.SOUND_NUCLEAREXPLOSION);
	}

	private void add(RegistryObject<SoundEvent> sound) {
		add(sound.get(), SoundDefinition.definition().subtitle("subtitles." + References.ID + "." + sound.getId().getPath()).with(Sound.sound(sound.getId(), SoundType.SOUND)));
	}

}
