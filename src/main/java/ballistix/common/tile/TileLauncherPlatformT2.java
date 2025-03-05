package ballistix.common.tile;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.registers.BallistixTiles;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

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
		return 3000;
	}

	@Override
	public int getTier() {
		return 2;
	}

}
