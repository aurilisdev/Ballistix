package ballistix.registers;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.IEventBus;
import voltaic.common.blockitem.BlockItemDescriptable;
import voltaic.prefab.utilities.VoltaicTextUtils;

public class UnifiedBallistixRegister {

	public static void register(IEventBus bus) {
		BallistixBlocks.BLOCKS.register(bus);
		BallistixItems.ITEMS.register(bus);
		BallistixTiles.BLOCK_ENTITY_TYPES.register(bus);
		BallistixMenuTypes.MENU_TYPES.register(bus);
		BallistixEntities.ENTITIES.register(bus);
		BallistixSounds.SOUNDS.register(bus);
		BallistixParticles.PARTICLES.register(bus);
		BallistixEffects.EFFECTS.register(bus);
	}

	static {

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1), VoltaicTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2), VoltaicTextUtils.voltageTooltip(240));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3), VoltaicTextUtils.voltageTooltip(480));
		
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1), BallistixTextUtils.tooltip("missilesilo.maxtier", Component.literal("1").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2), BallistixTextUtils.tooltip("missilesilo.maxtier", Component.literal("2").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3), BallistixTextUtils.tooltip("missilesilo.maxtier", Component.literal("2").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1), BallistixTextUtils.tooltip("missilesilo.accuracy", Component.literal("30").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2), BallistixTextUtils.tooltip("missilesilo.accuracy", Component.literal("15").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3), BallistixTextUtils.tooltip("missilesilo.accuracy", Component.literal("0").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls), VoltaicTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls), BallistixTextUtils.tooltip("missilesilo.maxtier", Component.literal("1").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar), VoltaicTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar), VoltaicTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower), VoltaicTextUtils.voltageTooltip(480));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret), VoltaicTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret), VoltaicTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret), VoltaicTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.BLOCKS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret), VoltaicTextUtils.voltageTooltip(120));

	}

}
