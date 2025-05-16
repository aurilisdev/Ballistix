package ballistix;

import ballistix.client.BallistixClientRegister;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.BallistixVoxelShapes;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixCapabilities;
import ballistix.registers.UnifiedBallistixRegister;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import voltaic.prefab.configuration.ConfigurationHandler;

@Mod(Ballistix.ID)
@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
public class Ballistix {

	public static final String ID = "ballistix";
	public static final String NAME = "Ballistix";

	public static final String NUCLEAR_SCIENCE_ID = "nuclearscience";
	public static final String GRIEF_DEFENDER_ID = "griefdefender";

	public Ballistix() {
		ConfigurationHandler.registerConfig(BallistixConstants.class);
		BallistixVoxelShapes.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
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
		NetworkHandler.init();
		BallistixTags.init();
		BallistixVoxelShapes.init();
		event.enqueueWork(() -> {
			BallistixCapabilities.register();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_ANTIMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.antimatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.darkmatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.largeantimatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE * 2, Integer.MAX_VALUE, null, SubtypeBlast.nuclear.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConstants.EXPLOSIVE_EMP_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.emp.ordinal());
		});
	}

	public static final ResourceLocation rl(String path) {
		return new ResourceLocation(Ballistix.ID, path);
	}

}
