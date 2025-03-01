package ballistix.client;

import ballistix.References;
import ballistix.client.guidebook.ModuleBallistix;
import ballistix.client.render.entity.RenderBlast;
import ballistix.client.render.entity.RenderBullet;
import ballistix.client.render.entity.RenderExplosive;
import ballistix.client.render.entity.RenderGrenade;
import ballistix.client.render.entity.RenderMinecart;
import ballistix.client.render.entity.RenderMissile;
import ballistix.client.render.entity.RenderRailgunRound;
import ballistix.client.render.entity.RenderSAM;
import ballistix.client.render.entity.RenderShrapnel;
import ballistix.client.render.tile.RenderCIWSTurret;
import ballistix.client.render.tile.RenderESMTower;
import ballistix.client.render.tile.RenderFireControlRadar;
import ballistix.client.render.tile.RenderLaserTurret;
import ballistix.client.render.tile.RenderMissileSilo;
import ballistix.client.render.tile.RenderRadar;
import ballistix.client.render.tile.RenderRailgunTurret;
import ballistix.client.render.tile.RenderSAMTurret;
import ballistix.client.screen.ScreenCIWSTurret;
import ballistix.client.screen.ScreenESMTower;
import ballistix.client.screen.ScreenFireControlRadar;
import ballistix.client.screen.ScreenLaserTurret;
import ballistix.client.screen.ScreenMissileSilo;
import ballistix.client.screen.ScreenRailgunTurret;
import ballistix.client.screen.ScreenSAMTurret;
import ballistix.client.screen.ScreenSearchRadar;
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
		ModelLoader.addSpecialModel(MODEL_RADARDISH);
		ModelLoader.addSpecialModel(MODEL_FIRECONTROLRADARDISH);
		ModelLoader.addSpecialModel(MODEL_MISSILECLOSERANGE);
		ModelLoader.addSpecialModel(MODEL_MISSILEMEDIUMRANGE);
		ModelLoader.addSpecialModel(MODEL_MISSILELONGRANGE);
		ModelLoader.addSpecialModel(MODEL_DARKMATTERSPHERE);
		ModelLoader.addSpecialModel(MODEL_DARKMATTERDISK);
		ModelLoader.addSpecialModel(MODEL_FIREBALL);
		ModelLoader.addSpecialModel(MODEL_EMP);
		ModelLoader.addSpecialModel(MODEL_BLACKHOLECUBE);
		ModelLoader.addSpecialModel(MODEL_AAMISSILE);
		ModelLoader.addSpecialModel(MODEL_SAMTURRET_BALLJOINT);
		ModelLoader.addSpecialModel(MODEL_SAMTURRET_RAIL);
		ModelLoader.addSpecialModel(MODEL_ESMTOWER);
		ModelLoader.addSpecialModel(MODEL_CIWSTURRET_BALLJOINT);
		ModelLoader.addSpecialModel(MODEL_CIWSTURRET_HEAD);
		ModelLoader.addSpecialModel(MODEL_CIWSTURRET_BARREL);
		ModelLoader.addSpecialModel(MODEL_LASERTURRET_BALLJOINT);
		ModelLoader.addSpecialModel(MODEL_LASERTURRET_HEAD);
		ModelLoader.addSpecialModel(MODEL_RAILGUNTURRET_BALLJOINT);
		ModelLoader.addSpecialModel(MODEL_RAILGUNTURRET_HEAD);
	}

	public static final ResourceLocation TEXTURE_SHRAPNEL = new ResourceLocation(References.ID, "textures/model/shrapnel.png");
	public static final ResourceLocation TEXTURE_MISSILECLOSERANGE = new ResourceLocation(References.ID, "textures/model/missilecloserange.png");
	public static final ResourceLocation TEXTURE_MISSILEMEDIUMRANGE = new ResourceLocation(References.ID, "textures/model/missilemediumrange.png");
	public static final ResourceLocation TEXTURE_MISSILELONGRANGE = new ResourceLocation(References.ID, "textures/model/missilelongrange.png");
	
	public static final ResourceLocation MODEL_RADARDISH = new ResourceLocation(References.ID, "block/radardish");
	public static final ResourceLocation MODEL_FIRECONTROLRADARDISH = new ResourceLocation(References.ID, "block/firecontrolradardish");
	public static final ResourceLocation MODEL_MISSILECLOSERANGE = new ResourceLocation(References.ID, "entity/missilecloserange");
	public static final ResourceLocation MODEL_MISSILEMEDIUMRANGE = new ResourceLocation(References.ID, "entity/missilemediumrange");
	public static final ResourceLocation MODEL_MISSILELONGRANGE = new ResourceLocation(References.ID, "entity/missilelongrange");
	public static final ResourceLocation MODEL_DARKMATTERSPHERE = new ResourceLocation(References.ID, "entity/darkmattersphere");
	public static final ResourceLocation MODEL_DARKMATTERDISK = new ResourceLocation(References.ID, "entity/darkmatterdisk");
	public static final ResourceLocation MODEL_FIREBALL = new ResourceLocation(References.ID, "entity/explosionsphere");
	public static final ResourceLocation MODEL_EMP = new ResourceLocation(References.ID, "entity/emp");
	public static final ResourceLocation MODEL_BLACKHOLECUBE = new ResourceLocation(References.ID, "entity/blackhole");

	public static final ResourceLocation MODEL_AAMISSILE = new ResourceLocation(References.ID, "entity/aamissile");
	public static final ResourceLocation MODEL_SAMTURRET_BALLJOINT = new ResourceLocation(References.ID, "block/samturretballjoint");
	public static final ResourceLocation MODEL_SAMTURRET_RAIL = new ResourceLocation(References.ID, "block/samturretrail");
	public static final ResourceLocation MODEL_ESMTOWER = new ResourceLocation(References.ID, "block/esmtower");
	public static final ResourceLocation MODEL_CIWSTURRET_BALLJOINT = new ResourceLocation(References.ID, "block/ciwsturretballjoint");
	public static final ResourceLocation MODEL_CIWSTURRET_HEAD = new ResourceLocation(References.ID, "block/ciwsturrethead");
	public static final ResourceLocation MODEL_CIWSTURRET_BARREL = new ResourceLocation(References.ID, "block/ciwsturretbarrel");
	public static final ResourceLocation MODEL_LASERTURRET_BALLJOINT = new ResourceLocation(References.ID, "block/laserturretballjoint");
	public static final ResourceLocation MODEL_LASERTURRET_HEAD = new ResourceLocation(References.ID, "block/laserturrethead");
	public static final ResourceLocation MODEL_RAILGUNTURRET_BALLJOINT = new ResourceLocation(References.ID, "block/railgunturretballjoint");
	public static final ResourceLocation MODEL_RAILGUNTURRET_HEAD = new ResourceLocation(References.ID, "block/railgunturretgun");

	public static void setup() {

		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_MISSILESILO.get(), RenderMissileSilo::new);
		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_RADAR.get(), RenderRadar::new);
		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_FIRECONTROLRADAR.get(), RenderFireControlRadar::new);
		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_SAMTURRET.get(), RenderSAMTurret::new);
		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_ESMTOWER.get(), RenderESMTower::new);
		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_CIWSTURRET.get(), RenderCIWSTurret::new);
		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_LASERTURRET.get(), RenderLaserTurret::new);
		ClientRegistry.bindTileEntityRenderer(BallistixBlockTypes.TILE_RAILGUNTURRET.get(), RenderRailgunTurret::new);

		ScreenManager.register(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), ScreenMissileSilo::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_SAMTURRET.get(), ScreenSAMTurret::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_FIRECONTROLRADAR.get(), ScreenFireControlRadar::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_SEARCHRADAR.get(), ScreenSearchRadar::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_ESMTOWER.get(), ScreenESMTower::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_CIWSTURRET.get(), ScreenCIWSTurret::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LASERTURRET.get(), ScreenLaserTurret::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_RAILGUNTURRET.get(), ScreenRailgunTurret::new);

		EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
		
		manager.register(BallistixEntities.ENTITY_EXPLOSIVE.get(), new RenderExplosive(manager));
		manager.register(BallistixEntities.ENTITY_GRENADE.get(), new RenderGrenade(manager));
		manager.register(BallistixEntities.ENTITY_BLAST.get(), new RenderBlast(manager));
		manager.register(BallistixEntities.ENTITY_SHRAPNEL.get(), new RenderShrapnel(manager));
		manager.register(BallistixEntities.ENTITY_MISSILE.get(), new RenderMissile(manager));
		manager.register(BallistixEntities.ENTITY_MINECART.get(), new RenderMinecart(manager));
		manager.register(BallistixEntities.ENTITY_SAM.get(), new RenderSAM(manager));
		manager.register(BallistixEntities.ENTITY_BULLET.get(), new RenderBullet(manager));
		manager.register(BallistixEntities.ENTITY_RAILGUNROUND.get(), new RenderRailgunRound(manager));

		ItemModelsProperties.register(BallistixItems.ITEM_TRACKER.get(), ANGLE_PREDICATE, ItemTracker::getAngle);

		RenderTypeLookup.setRenderLayer(BallistixBlocks.blockMissileSilo, RenderType.cutout());

		ScreenGuidebook.addGuidebookModule(new ModuleBallistix());
	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.translucent() || type == RenderType.solid();
	}

}
