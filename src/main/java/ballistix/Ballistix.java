package ballistix;

import ballistix.client.ClientRegister;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.settings.Constants;
import electrodynamics.api.configuration.ConfigurationHandler;
import electrodynamics.api.tile.processing.O2OProcessingRecipe;
import electrodynamics.common.recipe.MachineRecipes;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(References.ID)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD)
public class Ballistix {

	public Ballistix() {
		ConfigurationHandler.registerConfig(Constants.class);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		DeferredRegisters.BLOCKS.register(bus);
		DeferredRegisters.ITEMS.register(bus);
		DeferredRegisters.TILES.register(bus);
		DeferredRegisters.CONTAINERS.register(bus);
		DeferredRegisters.ENTITIES.register(bus);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onClientSetup(FMLClientSetupEvent event) {
		ClientRegister.setup();
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		NetworkHandler.init();
	}

	@SubscribeEvent
	public static void onLoadEvent(FMLLoadCompleteEvent event) {
		MachineRecipes.registerRecipe(electrodynamics.DeferredRegisters.TILE_MINERALGRINDER.get(), new O2OProcessingRecipe(Items.ROTTEN_FLESH, DeferredRegisters.ITEM_DUSTPOISON.get(), 2));
	}

}
