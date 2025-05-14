package ballistix.datagen.server;

import java.util.List;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixTiles;
import net.minecraft.world.level.block.Block;
import voltaic.datagen.utils.server.loottable.BaseLootTablesProvider;

public class BallistixLootTablesProvider extends BaseLootTablesProvider {

	public BallistixLootTablesProvider() {
		super(Ballistix.ID);
	}

	@Override
	protected void generate() {

		for (SubtypeBlast blast : SubtypeBlast.values()) {
			addSimpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(blast));
		}

		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER1, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER2, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER3, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER1, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER2, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), BallistixTiles.TILE_LAUNCHER_SUPPORT_FRAME_TIER3, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1), BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER1, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER2, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER3, true, false, false, false, false);
		addSimpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar));
		addSimpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar));
		addSimpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret));
		addSimpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower));
		addSimpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret));
		addSimpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret));
		addSimpleBlock(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret));

	}

	@Override
	public List<Block> getExcludedBlocks() {
		return List.of();
	}

}
