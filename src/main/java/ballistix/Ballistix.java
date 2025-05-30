package ballistix;

import ballistix.api.blast.RegisterBlastEvent;
import ballistix.client.BallistixClientRegister;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.BallistixVoxelShapes;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemGrenade;
import ballistix.common.item.ItemMinecart;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixItems;
import ballistix.registers.UnifiedBallistixRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import voltaic.prefab.configuration.ConfigurationHandler;

@Mod(Ballistix.ID)
@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
public class Ballistix {

	public static final String ID = "ballistix";
	public static final String NAME = "Ballistix";

	public static final String NUCLEAR_SCIENCE_ID = "nuclearscience";
	public static final String GRIEF_DEFENDER_ID = "griefdefender";

	public Ballistix(IEventBus bus) {
		ConfigurationHandler.registerConfig(BallistixConstants.class);
		BallistixVoxelShapes.init();
		UnifiedBallistixRegister.register(bus);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			BallistixClientRegister.setup();
		});
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		BallistixTags.init();
		// Moved here due to config file not being loaded until now
		event.enqueueWork(() -> {
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_ANTIMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.antimatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.darkmatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.largeantimatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) ((int) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 2.5), Integer.MAX_VALUE, null, SubtypeBlast.nuclear.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_EMP_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.emp.ordinal());
			RegisterBlastEvent registerBlastEvent = new RegisterBlastEvent();
			ModLoader.postEvent(registerBlastEvent);
			registerBlastEvent.stashBlasts();
		});
	}

	@SubscribeEvent
	public static void registerBlasts(RegisterBlastEvent event) {
		for(SubtypeBlast blast : SubtypeBlast.values()) {
			event.registerBlast(blast);
		}
		for(ItemGrenade.SubtypeGrenade grenade : ItemGrenade.SubtypeGrenade.values()) {
			event.registerGrenade(grenade.explosiveType, BallistixItems.ITEMS_GRENADE.getValue(grenade));
		}
		for(ItemMinecart.SubtypeMinecart minecart : ItemMinecart.SubtypeMinecart.values()) {
			event.registerMinecart(minecart.explosiveType, BallistixItems.ITEMS_MINECART.getValue(minecart));
		}
	}

	public static final ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(Ballistix.ID, path);
	}

}
