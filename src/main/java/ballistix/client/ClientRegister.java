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
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class ClientRegister {
    public static final ResourceLocation TEXTURE_SHRAPNEL = new ResourceLocation(
	    References.ID + ":textures/model/shrapnel.png");
    public static final ResourceLocation MODEL_MISSILECLOSERANGE = new ResourceLocation(
	    References.ID + ":entity/missilecloserange");
    public static final ResourceLocation MODEL_MISSILEMEDIUMRANGE = new ResourceLocation(
	    References.ID + ":entity/missilemediumrange");
    public static final ResourceLocation MODEL_MISSILELONGRANGE = new ResourceLocation(
	    References.ID + ":entity/missilelongrange");
    public static final ResourceLocation TEXTURE_MISSILECLOSERANGE = new ResourceLocation(
	    References.ID + ":textures/model/missilecloserange.png");
    public static final ResourceLocation TEXTURE_MISSILEMEDIUMRANGE = new ResourceLocation(
	    References.ID + ":textures/model/missilemediumrange.png");
    public static final ResourceLocation TEXTURE_MISSILELONGRANGE = new ResourceLocation(
	    References.ID + ":textures/model/missilelongrange.png");
    public static final ResourceLocation TEXTURE_FIREBALL = new ResourceLocation(
	    References.ID + ":textures/model/fireball.png");

    public static void setup() {
	ModelLoader.addSpecialModel(MODEL_MISSILECLOSERANGE);
	ModelLoader.addSpecialModel(MODEL_MISSILEMEDIUMRANGE);
	ModelLoader.addSpecialModel(MODEL_MISSILELONGRANGE);

	ClientRegistry.bindTileEntityRenderer(DeferredRegisters.TILE_MISSILESILO.get(), RenderMissileSilo::new);

	ScreenManager.registerFactory(DeferredRegisters.CONTAINER_MISSILESILO.get(), ScreenMissileSilo::new);

	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_EXPLOSIVE.get(),
		RenderExplosive::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_GRENADE.get(), RenderGrenade::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_BLAST.get(), RenderBlast::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_SHRAPNEL.get(), RenderShrapnel::new);
	RenderingRegistry.registerEntityRenderingHandler(DeferredRegisters.ENTITY_MISSILE.get(), RenderMissile::new);
	RenderTypeLookup.setRenderLayer(DeferredRegisters.blockMissileSilo, RenderType.getCutout());
    }

    public static boolean shouldMultilayerRender(RenderType type) {
	return type == RenderType.getTranslucent() || type == RenderType.getSolid();
    }

}
