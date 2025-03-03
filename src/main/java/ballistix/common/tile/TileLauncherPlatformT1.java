package ballistix.common.tile;

import ballistix.registers.BallistixTiles;
import electrodynamics.prefab.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileLauncherPlatformT1 extends GenericTile {
	public TileLauncherPlatformT1(BlockPos pos, BlockState state) {
		this(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER1.get(), pos, state, 1);
	}

	public TileLauncherPlatformT1(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
		super(type, pos, state);
	}

}
