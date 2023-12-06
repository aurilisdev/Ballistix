package ballistix.registers;

import electrodynamics.api.ISubtype;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;

public class UnifiedBallistixRegister {

	public static void register(IEventBus bus) {
		BallistixBlocks.BLOCKS.register(bus);
		BallistixItems.ITEMS.register(bus);
		BallistixBlockTypes.BLOCK_ENTITY_TYPES.register(bus);
		BallistixMenuTypes.MENU_TYPES.register(bus);
		BallistixEntities.ENTITIES.register(bus);
		BallistixSounds.SOUNDS.register(bus);
	}

	public static Block getSafeBlock(ISubtype type) {
		return BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(type).get();
	}
}
