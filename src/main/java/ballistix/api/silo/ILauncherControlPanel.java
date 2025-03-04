package ballistix.api.silo;

import net.minecraft.core.BlockPos;

public interface ILauncherControlPanel {
	
	public int getTier();
	
	public BlockPos getTarget();

	public int getFrequency();
}
