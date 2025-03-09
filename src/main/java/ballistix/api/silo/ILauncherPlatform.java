package ballistix.api.silo;

public interface ILauncherPlatform {
	public int getRange();

	public int getTier();

	public boolean hasMissile();

	public boolean hasExplosive();

	public boolean hasSAM();

	// returns cooldown
	public int launch(ILauncherControlPanel panel, boolean redstoneTriggered, int inaccuracy);

}
