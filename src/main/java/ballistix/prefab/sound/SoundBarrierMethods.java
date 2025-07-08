package ballistix.prefab.sound;

import ballistix.common.entity.EntityMissile;
import ballistix.common.tile.TileAirRaidSiren;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public class SoundBarrierMethods {

    public static void playMissileSound(SoundEvent sound, EntityMissile missile) {
        Minecraft.getInstance().getSoundManager().play(new SoundInstanceMissile(sound, missile));
    }
    
    public static void playAirRaidSirenSound(SoundEvent sound, TileAirRaidSiren tile, double range) {
        Minecraft.getInstance().getSoundManager().play(new SoundInstanceAirRaidSiren(sound, tile, range, true));
    }

}
