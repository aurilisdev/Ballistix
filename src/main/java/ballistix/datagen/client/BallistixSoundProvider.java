package ballistix.datagen.client;

import ballistix.Ballistix;
import ballistix.registers.BallistixSounds;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.datagen.utils.client.BaseSoundProvider;

public class BallistixSoundProvider extends BaseSoundProvider {

	public BallistixSoundProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, helper, Ballistix.ID);
	}

	@Override
	public void registerSounds() {
		add(BallistixSounds.SOUND_ANTIMATTEREXPLOSION);
		add(BallistixSounds.SOUND_SONICEXPLOSION);
		add(BallistixSounds.SOUND_HYPERSONICSONICEXPLOSION);
		add(BallistixSounds.SOUND_DARKMATTER);
		add(BallistixSounds.SOUND_NUCLEAREXPLOSION);
		add(BallistixSounds.SOUND_EMPEXPLOSION);
		add(BallistixSounds.SOUND_MISSILE_ROCKETLAUNCHER);
		add(BallistixSounds.SOUND_MISSILE_SILO);
		add(BallistixSounds.SOUND_RADAR);
		add(BallistixSounds.SOUND_FIRECONTROLRADAR);
		add(BallistixSounds.SOUND_CIWS_TURRETFIRING);
		add(BallistixSounds.SOUND_LASER_TURRETFIRING);
		add(BallistixSounds.SOUND_RAILGUNKINETIC);
		add(BallistixSounds.SOUND_RODHITTINGGROUND);
		add(BallistixSounds.SOUND_VLSLAUNCH);
	}

}
