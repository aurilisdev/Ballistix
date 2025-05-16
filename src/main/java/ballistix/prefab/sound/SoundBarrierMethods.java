package ballistix.prefab.sound;

import ballistix.common.entity.EntityMissile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;

public class SoundBarrierMethods {

    public static void playMissileSound(SoundEvent sound, EntityMissile missile) {
        Minecraft.getInstance().getSoundManager().play(new SoundInstanceMissile(sound, missile));
    }

}
