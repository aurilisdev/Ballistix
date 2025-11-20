package ballistix;

import ballistix.api.blast.RegisterBlastEvent;
import ballistix.client.BallistixClientRegister;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.BallistixVoxelShapes;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemGrenade;
import ballistix.common.item.ItemMinecart;
import ballistix.common.settings.BallistixConfig;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixItems;
import ballistix.registers.UnifiedBallistixRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Ballistix.ID)
@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
public final class Ballistix {

    public static final String ID = "ballistix";
    public static final String NAME = "Ballistix";

    public static final String NUCLEAR_SCIENCE_ID = "nuclearscience";
    public static final String GRIEF_DEFENDER_ID = "griefdefender";

    public Ballistix(IEventBus bus, ModContainer container) {
	BallistixConfig.INSTANCE = new BallistixConfig();
	container.registerConfig(ModConfig.Type.COMMON, BallistixConfig.INSTANCE.SPEC);
	container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
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
	    RegisterBlastEvent registerBlastEvent = new RegisterBlastEvent();
	    ModLoader.postEvent(registerBlastEvent);
	    registerBlastEvent.stashBlasts();
	});
    }

    @SubscribeEvent
    public static void registerBlasts(RegisterBlastEvent event) {
	for (SubtypeBlast blast : SubtypeBlast.values()) {
	    event.registerBlast(blast);
	}
	for (ItemGrenade.SubtypeGrenade grenade : ItemGrenade.SubtypeGrenade.values()) {
	    event.registerGrenade(grenade.explosiveType, BallistixItems.ITEMS_GRENADE.getValue(grenade));
	}
	for (ItemMinecart.SubtypeMinecart minecart : ItemMinecart.SubtypeMinecart.values()) {
	    event.registerMinecart(minecart.explosiveType, BallistixItems.ITEMS_MINECART.getValue(minecart));
	}
	event.submitCachedThreads(() -> {
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.antimatter.id()).start();
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_DARKMATTER_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.darkmatter.id()).start();
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_LARGEANTIMATTER_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.largeantimatter.id()).start();
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_NUCLEAR_SIZE.getAsDouble() * 2,
		    Integer.MAX_VALUE, null, SubtypeBlast.nuclear.id()).start();
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_EMP_RADIUS.getAsDouble(), Integer.MAX_VALUE,
		    null, SubtypeBlast.emp.id());
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_SONIC_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.sonic.id());
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_HYPERSONIC_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.hypersonic.id());
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_ENDOTHERMIC_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.endothermic.id());
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_INFESTIVE_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.infestive.id());
	    new ThreadSimpleBlast(null, BlockPos.ZERO, (int) BallistixConfig.INSTANCE.EXPLOSIVE_EXOTHERMIC_RADIUS.getAsDouble(),
		    Integer.MAX_VALUE, null, SubtypeBlast.exothermic.id());
	});
    }

    public static final ResourceLocation rl(String path) {
	return ResourceLocation.fromNamespaceAndPath(Ballistix.ID, path);
    }

}
