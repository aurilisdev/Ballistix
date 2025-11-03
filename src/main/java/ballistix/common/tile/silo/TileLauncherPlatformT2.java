package ballistix.common.tile.silo;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.settings.BallistixConfig;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;

public class TileLauncherPlatformT2 extends TileLauncherPlatformT1 {
	public TileLauncherPlatformT2(BlockPos pos, BlockState state) {
		super(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER2.get(), pos, state);
	}

	@Override
	public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
		return SubtypeBallistixMachine.Subnodes.LAUNCHER_PLATFORM_TIER2;
	}

	@Override
	public int getRange() {
		return BallistixConfig.INSTANCE.LAUNCHER_PLATFORM_RANGE_T2.get();
	}

	@Override
	public int getTier() {
		return 2;
	}

}
