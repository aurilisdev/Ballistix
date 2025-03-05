package ballistix.api.silo;

public interface ILauncherPlatform {
	public int getRange();

	public int getTier();

	public boolean hasExplosive();

	public void launch(ILauncherControlPanel panel);

}
