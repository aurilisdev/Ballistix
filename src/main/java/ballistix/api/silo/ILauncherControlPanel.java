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

	public void setTarget(BlockPos blockPos);

}
