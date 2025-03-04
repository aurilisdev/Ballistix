package ballistix.common.tile;

import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileLauncherPlatformT3 extends TileLauncherPlatformT1 {
	public TileLauncherPlatformT3(BlockPos pos, BlockState state) {
		super(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER3.get(), pos, state);
	}

	@Override
	public int getRange() {
		return 10000;
	}

	@Override
	public int getTier() {
		return 3;
	}

}
