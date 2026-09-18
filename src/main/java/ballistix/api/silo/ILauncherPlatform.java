package ballistix.api.silo;

import net.minecraft.world.level.Level;

public interface ILauncherPlatform {
    public int getRange();

    public int getTier();

    public boolean hasMissile();

    public boolean hasExplosive();

    public boolean hasSAM();

    // returns cooldown
    public int launch(Level level, ILauncherControlPanel panel, boolean redstoneTriggered, int inaccuracy);

}
