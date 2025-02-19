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

	public static final ResourceLocation ANGLE_PREDICATE = new ResourceLocation("angle");

	@SubscribeEvent
	public static void onModelEvent(RegisterAdditional event) {
		event.register(MODEL_RADARDISH);
		event.register(MODEL_FIRECONTROLRADARDISH);
		event.register(MODEL_MISSILECLOSERANGE);
		event.register(MODEL_MISSILEMEDIUMRANGE);
		event.register(MODEL_MISSILELONGRANGE);
		event.register(MODEL_DARKMATTERSPHERE);
		event.register(MODEL_DARKMATTERDISK);
		event.register(MODEL_FIREBALL);
		event.register(MODEL_EMP);
		event.register(MODEL_BLACKHOLECUBE);
		event.register(MODEL_AAMISSILE);
		event.register(MODEL_SAMTURRET_BALLJOINT);
		event.register(MODEL_SAMTURRET_RAIL);
		event.register(MODEL_ESMTOWER);
		event.register(MODEL_CIWSTURRET_BALLJOINT);
		event.register(MODEL_CIWSTURRET_HEAD);
		event.register(MODEL_CIWSTURRET_BARREL);
		event.register(MODEL_LASERTURRET_BALLJOINT);
		event.register(MODEL_LASERTURRET_HEAD);
		event.register(MODEL_RAILGUNTURRET_BALLJOINT);
		event.register(MODEL_RAILGUNTURRET_HEAD);
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
		MenuScreens.register(BallistixMenuTypes.CONTAINER_MISSILESILO.get(), ScreenMissileSilo::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_SAMTURRET.get(), ScreenSAMTurret::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_FIRECONTROLRADAR.get(), ScreenFireControlRadar::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_SEARCHRADAR.get(), ScreenSearchRadar::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_ESMTOWER.get(), ScreenESMTower::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_CIWSTURRET.get(), ScreenCIWSTurret::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LASERTURRET.get(), ScreenLaserTurret::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_RAILGUNTURRET.get(), ScreenRailgunTurret::new);
		ItemProperties.register(BallistixItems.ITEM_TRACKER.get(), ANGLE_PREDICATE, ItemTracker::getAngle);

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
		event.registerEntityRenderer(BallistixEntities.ENTITY_SAM.get(), RenderSAM::new);
		event.registerEntityRenderer(BallistixEntities.ENTITY_BULLET.get(), RenderBullet::new);
		event.registerEntityRenderer(BallistixEntities.ENTITY_RAILGUNROUND.get(), RenderRailgunRound::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_MISSILESILO.get(), RenderMissileSilo::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_RADAR.get(), RenderRadar::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_FIRECONTROLRADAR.get(), RenderFireControlRadar::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_SAMTURRET.get(), RenderSAMTurret::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_ESMTOWER.get(), RenderESMTower::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_CIWSTURRET.get(), RenderCIWSTurret::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_LASERTURRET.get(), RenderLaserTurret::new);
		event.registerBlockEntityRenderer(BallistixBlockTypes.TILE_RAILGUNTURRET.get(), RenderRailgunTurret::new);

	}

	public static boolean shouldMultilayerRender(RenderType type) {
		return type == RenderType.translucent() || type == RenderType.solid();
	}

}
