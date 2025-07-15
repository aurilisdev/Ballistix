package ballistix.api.silo;

import net.minecraft.core.BlockPos;
import voltaic.prefab.utilities.object.CachedTileOutput;

public interface ILauncherControlPanel {

	public int getTier();

	public BlockPos getTarget();

	public int getFrequency();

	public BlockPos getPos();

	public CachedTileOutput getPlatform();

	public CachedTileOutput getSupportFrame();

	public void launch();

	public void setTarget(BlockPos target);

	/**
	 * All designators i.e. the Laser Designator and the Radar gun will call this method variant instead of the
	 * standard launch() method. This is to allow you to tweak the coordinates before doing something
	 * @param target
	 */
	public void setTargetFromDesignator(BlockPos target);

}
