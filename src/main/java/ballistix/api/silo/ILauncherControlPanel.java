package ballistix.api.silo;

import electrodynamics.prefab.utilities.object.CachedTileOutput;
import net.minecraft.core.BlockPos;

public interface ILauncherControlPanel {

	public int getTier();

	public BlockPos getTarget();

	public int getFrequency();

	public BlockPos getPos();

	public CachedTileOutput getPlatform();

	public void launch();

	public void setTarget(BlockPos blockPos);

}
