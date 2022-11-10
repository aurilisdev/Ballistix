package ballistix.client;

import ballistix.References;
import ballistix.client.guidebook.ModuleBallistix;
import ballistix.client.render.entity.RenderBlast;
import ballistix.client.render.entity.RenderExplosive;
import ballistix.client.render.entity.RenderGrenade;
import ballistix.client.render.entity.RenderMinecart;
import ballistix.client.render.entity.RenderMissile;
import ballistix.client.render.entity.RenderShrapnel;
import ballistix.client.render.tile.RenderMissileSilo;
import ballistix.client.screen.ScreenMissileSilo;
import ballistix.common.item.ItemTracker;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixEntities;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.client.guidebook.ScreenGuidebook;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = { Dist.CLIENT })
public class ClientRegister {
	@SubscribeEvent
	public static void onModelEvent(RegisterAdditional event) {
		event.register(MODEL_MISSILECLOSERANGE);
		event.register(MODEL_MISSILEMEDIUMRANGE);
		event.register(MODEL_MISSILELONGRANGE);
		event.register(MODEL_DARKMATTERSPHERE);
		event.register(MODEL_DARKMATTERDISK);
		event.register(MODEL_FIREBALL);
		event.register(MODEL_EMP);
		event.register(MODEL_BLACKHOLECUBE);
	}

	public static final ResourceLocation TEXTURE_SHRAPNEL = new ResourceLocation(References.ID + ":textures/model/shrapnel.png");
	public static final ResourceLocation MODEL_MISSILECLOSERANGE = new ResourceLocation(References.ID + ":entity/missilecloserange");
	public static final ResourceLocation MODEL_MISSILEMEDIUMRANGE = new ResourceLocation(References.ID + ":entity/missilemediumrange");
	public static final ResourceLocation MODEL_MISSILELONGRANGE = new ResourceLocation(References.ID + ":entity/missilelongrange");
	public static final ResourceLocation MODEL_DARKMATTERSPHERE = new ResourceLocation(References.ID + ":entity/darkmattersphere");
	public static final ResourceLocation MODEL_DARKMATTERDISK = new ResourceLocation(References.ID + ":entity/darkmatterdisk");
	public static final ResourceLocation MODEL_FIREBALL = new ResourceLocation(References.ID + ":entity/explosionsphere");
	public static final ResourceLocation MODEL_EMP = new ResourceLocation(References.ID + ":entity/emp");
	public static final ResourceLocation MODEL_BLACKHOLECUBE = new ResourceLocation(References.ID + ":entity/blackhole");
	public static final ResourceLocation TEXTURE_MISSILECLOSERANGE = new ResourceLocation(References.ID + ":textures/model/missilecloserange.png");
	public static final ResourceLocation TEXTURE_MISSILEMEDIUMRANGE = new ResourceLocation(References.ID + ":textures/model/missilemediumrange.png");
	public static final ResourceLocation TEXTURE_MISSILELONGRANGE = new ResourceLocation(References.ID + ":textures/model/missilelongrange.png");

	public static void setup() {
		MenuScreens.register(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), ScreenMissileSilo::new);
		ItemProperties.register(BallistixItems.ITEM_TRACKER.get(), new ResourceLocation("angle"), ItemTracker::getAngle);

		ScreenGuidebook.addGuidebookModule(new ModuleBallistix());
	}

	@SubscribeEvent
	public static void registerEntities(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(BallistixEntities.ENTITY_EXPLOSIVE.get(), RenderExplosive::new);
		event.registerEntityRenderer(BallistixEntities.ENTITY_GRENADE.get(), RenderGrenade::new);
		event.registerEntityRenderer(BallistixEntities.ENTITY_BLAST.get(), RenderBlast::new);
		event.registerEntityRenderer(BallistixEntities.ENTITY_SHRAPNEL.get(), RenderShrapnel::new);
		event.registerEntityRenderer(BallistixEntities.ENTITY_MISSILE.get(), RenderMissile::new);
		event.registerEntityRenderer(BallistixEntities.ENTITY_MINECART.get(), RenderMinecart::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_MISSILESILO.get(), RenderMissileSilo::new);

	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.translucent() || type == RenderType.solid();
	}

}
