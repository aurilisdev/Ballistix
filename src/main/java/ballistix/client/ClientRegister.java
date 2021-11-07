package ballistix.client;

import ballistix.DeferredRegisters;
import ballistix.References;
import ballistix.client.render.entity.RenderBlast;
import ballistix.client.render.entity.RenderExplosive;
import ballistix.client.render.entity.RenderGrenade;
import ballistix.client.render.entity.RenderMissile;
import ballistix.client.render.entity.RenderShrapnel;
import ballistix.client.render.tile.RenderMissileSilo;
import ballistix.client.screen.ScreenMissileSilo;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = { Dist.CLIENT })
public class ClientRegister {
    @SubscribeEvent
    public static void onModelEvent(ModelRegistryEvent event) {
	ModelLoader.addSpecialModel(MODEL_MISSILECLOSERANGE);
	ModelLoader.addSpecialModel(MODEL_MISSILEMEDIUMRANGE);
	ModelLoader.addSpecialModel(MODEL_MISSILELONGRANGE);
    }

    public static final ResourceLocation TEXTURE_SHRAPNEL = new ResourceLocation(References.ID + ":textures/model/shrapnel.png");
    public static final ResourceLocation MODEL_MISSILECLOSERANGE = new ResourceLocation(References.ID + ":entity/missilecloserange");
    public static final ResourceLocation MODEL_MISSILEMEDIUMRANGE = new ResourceLocation(References.ID + ":entity/missilemediumrange");
    public static final ResourceLocation MODEL_MISSILELONGRANGE = new ResourceLocation(References.ID + ":entity/missilelongrange");
    public static final ResourceLocation TEXTURE_MISSILECLOSERANGE = new ResourceLocation(References.ID + ":textures/model/missilecloserange.png");
    public static final ResourceLocation TEXTURE_MISSILEMEDIUMRANGE = new ResourceLocation(References.ID + ":textures/model/missilemediumrange.png");
    public static final ResourceLocation TEXTURE_MISSILELONGRANGE = new ResourceLocation(References.ID + ":textures/model/missilelongrange.png");
    public static final ResourceLocation TEXTURE_FIREBALL = new ResourceLocation(References.ID + ":textures/model/fireball.png");

    public static void setup() {

	ClientRegistry.bindTileEntityRenderer(DeferredRegisters.TILE_MISSILESILO.get(), RenderMissileSilo::new);

	MenuScreens.register(DeferredRegisters.CONTAINER_MISSILESILO.get(), ScreenMissileSilo::new);

	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_EXPLOSIVE.get(), RenderExplosive::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_GRENADE.get(), RenderGrenade::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_BLAST.get(), RenderBlast::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_SHRAPNEL.get(), RenderShrapnel::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_MISSILE.get(), RenderMissile::new);
	ItemBlockRenderTypes.setRenderLayer(DeferredRegisters.blockMissileSilo, RenderType.cutout());
    }

    public static boolean shouldMultilayerRender(RenderType type) {
	return type == RenderType.translucent() || type == RenderType.solid();
    }

}
