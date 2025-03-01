package ballistix.registers;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.recipe.BallistixRecipeInit;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import net.neoforged.bus.api.IEventBus;

public class UnifiedBallistixRegister {

	public static void register(IEventBus bus) {
		BallistixAttachmentTypes.ATTACHMENT_TYPES.register(bus);
		BallistixDataComponentTypes.DATA_COMPONENT_TYPES.register(bus);
		BallistixBlocks.BLOCKS.register(bus);
		BallistixItems.ITEMS.register(bus);
		BallistixTiles.BLOCK_ENTITY_TYPES.register(bus);
		BallistixRecipeInit.INGREDIENT_TYPES.register(bus);
		BallistixMenuTypes.MENU_TYPES.register(bus);
		BallistixEntities.ENTITIES.register(bus);
		BallistixSounds.SOUNDS.register(bus);
		BallistixCreativeTabs.CREATIVE_TABS.register(bus);
		BallistixParticles.PARTICLES.register(bus);
	}

	static {

		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.launchercontrolpaneltier1), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.launchercontrolpaneltier2), ElectroTextUtils.voltageTooltip(240));
		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.launchercontrolpaneltier3), ElectroTextUtils.voltageTooltip(480));

		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.radar), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.firecontrolradar), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.esmtower), ElectroTextUtils.voltageTooltip(480));

		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.samturret), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.ciwsturret), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.laserturret), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getHolder(SubtypeBallistixMachine.railgunturret), ElectroTextUtils.voltageTooltip(120));

	}

}
