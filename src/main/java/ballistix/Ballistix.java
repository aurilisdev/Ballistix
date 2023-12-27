package ballistix;

import ballistix.api.capability.BallistixCapabilities;
import ballistix.client.ClientRegister;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.BallistixVoxelShapesRegistry;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.settings.Constants;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.UnifiedBallistixRegister;
import electrodynamics.prefab.configuration.ConfigurationHandler;
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

@Mod(References.ID)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD)
public class Ballistix {

	public Ballistix() {
		ConfigurationHandler.registerConfig(Constants.class);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		UnifiedBallistixRegister.register(bus);
		new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_ANTIMATTER_RADIUS, Integer.MAX_VALUE, null, true).start();
		new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null, true).start();
		new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_LARGEANTIMATTER_RADIUS, Integer.MAX_VALUE, null, true).start();
		new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_NUCLEAR_SIZE * 2, Integer.MAX_VALUE, null, true).start();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ClientRegister.setup();
		});
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		NetworkHandler.init();
		BallistixTags.init();
		BallistixVoxelShapesRegistry.init();
		event.enqueueWork(() -> {
			BallistixCapabilities.register();
		});
	}

}
