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
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixEntities;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.client.guidebook.ScreenGuidebook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD, value = { Dist.CLIENT })
public class ClientRegister {

	public static final ResourceLocation ANGLE_PREDICATE = new ResourceLocation("angle");

	@SubscribeEvent
	public static void onModelEvent(ModelRegistryEvent event) {
		ModelLoader.addSpecialModel(MODEL_MISSILECLOSERANGE);
		ModelLoader.addSpecialModel(MODEL_MISSILEMEDIUMRANGE);
		ModelLoader.addSpecialModel(MODEL_MISSILELONGRANGE);
		ModelLoader.addSpecialModel(MODEL_DARKMATTERSPHERE);
		ModelLoader.addSpecialModel(MODEL_DARKMATTERDISK);
		ModelLoader.addSpecialModel(MODEL_FIREBALL);
		ModelLoader.addSpecialModel(MODEL_EMP);
		ModelLoader.addSpecialModel(MODEL_BLACKHOLECUBE);
	}

	public static final ResourceLocation TEXTURE_SHRAPNEL = new ResourceLocation(References.ID + ":textures/model/shrapnel.png");
	public static final ResourceLocation MODEL_RADARDISH = new ResourceLocation(References.ID + ":block/dish");
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

		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_MISSILESILO.get(), RenderMissileSilo::new);

		ScreenManager.register(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), ScreenMissileSilo::new);

		EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
		
		manager.register(BallistixEntities.ENTITY_EXPLOSIVE.get(), new RenderExplosive(manager));
		manager.register(BallistixEntities.ENTITY_GRENADE.get(), new RenderGrenade(manager));
		manager.register(BallistixEntities.ENTITY_BLAST.get(), new RenderBlast(manager));
		manager.register(BallistixEntities.ENTITY_SHRAPNEL.get(), new RenderShrapnel(manager));
		manager.register(BallistixEntities.ENTITY_MISSILE.get(), new RenderMissile(manager));
		manager.register(BallistixEntities.ENTITY_MINECART.get(), new RenderMinecart(manager));

		ItemModelsProperties.register(BallistixItems.ITEM_TRACKER.get(), ANGLE_PREDICATE, ItemTracker::getAngle);

		RenderTypeLookup.setRenderLayer(BallistixBlocks.blockMissileSilo, RenderType.cutout());

		ScreenGuidebook.addGuidebookModule(new ModuleBallistix());
	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.translucent() || type == RenderType.solid();
	}

}
