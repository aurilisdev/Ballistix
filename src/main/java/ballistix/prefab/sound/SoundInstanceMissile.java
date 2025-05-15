package ballistix.prefab.sound;

import ballistix.common.entity.EntityMissile;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundInstanceMissile extends AbstractTickableSoundInstance {

    private final EntityMissile missile;

    public SoundInstanceMissile(SoundEvent soundEvent, EntityMissile missile) {
        super(soundEvent, SoundSource.HOSTILE);
        this.missile = missile;
        this.x = missile.getX();
        this.y = missile.getY();
        this.z = missile.getZ();
        this.looping = false;
    }

    @Override
    public void tick() {
        this.x = missile.getX();
        this.y = missile.getY();
        this.z = missile.getZ();
    }


}
