package ballistix.datagen.server;

import java.util.List;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixTiles;
import electrodynamics.datagen.server.ElectrodynamicsLootTablesProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

public class BallistixLootTablesProvider extends ElectrodynamicsLootTablesProvider {

	public BallistixLootTablesProvider(HolderLookup.Provider provider) {
		super(References.ID, provider);
	}

	@Override
	protected void generate() {

		for (SubtypeBlast blast : SubtypeBlast.values()) {
			addSimpleBlock(BallistixBlocks.BLOCKS_EXPLOSIVE.getValue(blast));
		}

		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER1, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER2, true, false, false, false, false);
		addMachineTable(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), BallistixTiles.TILE_LAUNCHER_CONTROL_PANEL_TIER3, true, false, false, false, false);
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
