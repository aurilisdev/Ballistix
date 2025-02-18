package ballistix.registers;

import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import net.minecraftforge.eventbus.api.IEventBus;

public class UnifiedBallistixRegister {

	public static void register(IEventBus bus) {
		BallistixBlocks.BLOCKS.register(bus);
		BallistixItems.ITEMS.register(bus);
		BallistixBlockTypes.BLOCK_ENTITY_TYPES.register(bus);
		BallistixMenuTypes.MENU_TYPES.register(bus);
		BallistixEntities.ENTITIES.register(bus);
		BallistixSounds.SOUNDS.register(bus);
		BallistixCreativeTabs.CREATIVE_TABS.register(bus);
	}
	
	static {
		
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockMissileSilo, ElectroTextUtils.voltageTooltip(120));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockRadar, ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockFireControlRadar, ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockEsmTower, ElectroTextUtils.voltageTooltip(480));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockSamTurret, ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockCiwsTurret, ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockLaserTurret, ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockRailgunTurret, ElectroTextUtils.voltageTooltip(120));
		
	}

}
