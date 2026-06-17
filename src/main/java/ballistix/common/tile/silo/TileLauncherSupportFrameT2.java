package ballistix.common.tile.silo;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.registers.BallistixTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;

public class TileLauncherSupportFrameT2 extends TileLauncherSupportFrameT1 {
    public TileLauncherSupportFrameT2(BlockPos pos, BlockState state) {
	super(BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER2.get(), pos, state, 2);
    }

    @Override
    public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
	return SubtypeBallistixMachine.Subnodes.LAUNCHER_SUPPORT_FRAME_TIER2;
    }

    @Override
    public int getInaccuracy() {
	return 15;
    }
}
