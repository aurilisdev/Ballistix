package ballistix.prefab.sound;

import ballistix.common.entity.EntityMissile;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundInstanceMissile extends TickableSound {

    private final EntityMissile missile;

    public SoundInstanceMissile(SoundEvent soundEvent, EntityMissile missile) {
        super(soundEvent, SoundCategory.HOSTILE);
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
