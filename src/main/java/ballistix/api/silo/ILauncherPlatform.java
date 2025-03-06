package ballistix.api.silo;

public interface ILauncherPlatform {
	public int getRange();

	public int getTier();

	public boolean hasExplosive();

	public boolean launch(ILauncherControlPanel panel);

}
