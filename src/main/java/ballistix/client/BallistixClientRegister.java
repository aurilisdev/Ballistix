package ballistix.client;

import ballistix.Ballistix;
import ballistix.client.guidebook.ModuleBallistix;
import ballistix.client.particle.ParticleBlastSmoke;
import ballistix.client.particle.ParticleMissileSmoke;
import ballistix.client.particle.ParticleShockwave;
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
import ballistix.client.render.tile.RenderLauncherPlatform;
import ballistix.client.render.tile.RenderRadar;
import ballistix.client.render.tile.RenderRailgunTurret;
import ballistix.client.render.tile.RenderSAMTurret;
import ballistix.client.screen.ScreenCIWSTurret;
import ballistix.client.screen.ScreenESMTower;
import ballistix.client.screen.ScreenFireControlRadar;
import ballistix.client.screen.ScreenLaserTurret;
import ballistix.client.screen.ScreenLauncherControlPanelT1;
import ballistix.client.screen.ScreenLauncherControlPanelT2;
import ballistix.client.screen.ScreenLauncherControlPanelT3;
import ballistix.client.screen.ScreenLauncherPlatformT1;
import ballistix.client.screen.ScreenLauncherPlatformT2;
import ballistix.client.screen.ScreenLauncherPlatformT3;
import ballistix.client.screen.ScreenRailgunTurret;
import ballistix.client.screen.ScreenSAMTurret;
import ballistix.client.screen.ScreenSearchRadar;
import ballistix.registers.BallistixTiles;
import ballistix.registers.BallistixEntities;
import ballistix.registers.BallistixMenuTypes;
import ballistix.registers.BallistixParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.Voltaic;
import voltaic.client.guidebook.ScreenGuidebook;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT })
public class BallistixClientRegister {

	public static final ResourceLocation ANGLE_PREDICATE = Voltaic.vanillarl("angle");

	public static final ResourceLocation TEXTURE_SHRAPNEL = Ballistix.rl("textures/model/shrapnel.png");
	public static final ResourceLocation TEXTURE_MISSILECLOSERANGE = Ballistix.rl("textures/model/missilecloserange.png");
	public static final ResourceLocation TEXTURE_MISSILEMEDIUMRANGE = Ballistix.rl("textures/model/missilemediumrange.png");
	public static final ResourceLocation TEXTURE_MISSILELONGRANGE = Ballistix.rl("textures/model/missilelongrange.png");

	public static final ResourceLocation MODEL_RADARDISH = Ballistix.rl("block/radardish");
	public static final ResourceLocation MODEL_FIRECONTROLRADARDISH = Ballistix.rl("block/firecontrolradardish");
	public static final ResourceLocation MODEL_MISSILETIER1 = Ballistix.rl("entity/missiles/missiletier1");
	public static final ResourceLocation MODEL_MISSILETIER2 = Ballistix.rl("entity/missiles/missiletier2");
	public static final ResourceLocation MODEL_MISSILETIER3 = Ballistix.rl("entity/missiles/missiletier3");
	public static final ResourceLocation MODEL_DARKMATTERSPHERE = Ballistix.rl("entity/darkmattersphere");
	public static final ResourceLocation MODEL_DARKMATTERDISK = Ballistix.rl("entity/darkmatterdisk");
	public static final ResourceLocation MODEL_FIREBALL = Ballistix.rl("entity/explosionsphere");
	public static final ResourceLocation MODEL_EMP = Ballistix.rl("entity/emp");
	public static final ResourceLocation MODEL_BLACKHOLECUBE = Ballistix.rl("entity/blackhole");

	public static final ResourceLocation MODEL_AAMISSILE = Ballistix.rl("entity/aamissile");
	public static final ResourceLocation MODEL_AAMISSILE_MK2 = Ballistix.rl("entity/missileantiballistic");
	public static final ResourceLocation MODEL_SAMTURRET_BALLJOINT = Ballistix.rl("block/samturretballjoint");
	public static final ResourceLocation MODEL_SAMTURRET_RAIL = Ballistix.rl("block/samturretrail");
	public static final ResourceLocation MODEL_ESMTOWER = Ballistix.rl("block/esmtower");
	public static final ResourceLocation MODEL_CIWSTURRET_BALLJOINT = Ballistix.rl("block/ciwsturretballjoint");
	public static final ResourceLocation MODEL_CIWSTURRET_HEAD = Ballistix.rl("block/ciwsturrethead");
	public static final ResourceLocation MODEL_CIWSTURRET_BARREL = Ballistix.rl("block/ciwsturretbarrel");
	public static final ResourceLocation MODEL_LASERTURRET_BALLJOINT = Ballistix.rl("block/laserturretballjoint");
	public static final ResourceLocation MODEL_LASERTURRET_HEAD = Ballistix.rl("block/laserturrethead");
	public static final ResourceLocation MODEL_RAILGUNTURRET_BALLJOINT = Ballistix.rl("block/railgunturretballjoint");
	public static final ResourceLocation MODEL_RAILGUNTURRET_HEAD = Ballistix.rl("block/railgunturretgun");

	public static void setup() {
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T1.get(), ScreenLauncherControlPanelT1::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T2.get(), ScreenLauncherControlPanelT2::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T3.get(), ScreenLauncherControlPanelT3::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T1.get(), ScreenLauncherPlatformT1::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T2.get(), ScreenLauncherPlatformT2::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T3.get(), ScreenLauncherPlatformT3::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_SAMTURRET.get(), ScreenSAMTurret::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_FIRECONTROLRADAR.get(), ScreenFireControlRadar::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_SEARCHRADAR.get(), ScreenSearchRadar::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_ESMTOWER.get(), ScreenESMTower::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_CIWSTURRET.get(), ScreenCIWSTurret::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_LASERTURRET.get(), ScreenLaserTurret::new);
		MenuScreens.register(BallistixMenuTypes.CONTAINER_RAILGUNTURRET.get(), ScreenRailgunTurret::new);

		ScreenGuidebook.addGuidebookModule(new ModuleBallistix());
	}
	
	@SubscribeEvent
	public static void onModelEvent(ModelEvent.RegisterAdditional event) {
		event.register(MODEL_RADARDISH);
		event.register(MODEL_FIRECONTROLRADARDISH);
		event.register(MODEL_MISSILETIER1);
		event.register(MODEL_MISSILETIER2);
		event.register(MODEL_MISSILETIER3);
		event.register(MODEL_DARKMATTERSPHERE);
		event.register(MODEL_DARKMATTERDISK);
		event.register(MODEL_FIREBALL);
		event.register(MODEL_EMP);
		event.register(MODEL_BLACKHOLECUBE);
		event.register(MODEL_AAMISSILE);
		event.register(MODEL_AAMISSILE_MK2);
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
		event.registerBlockEntityRenderer(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER1.get(), RenderLauncherPlatform::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER2.get(), RenderLauncherPlatform::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER3.get(), RenderLauncherPlatform::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_RADAR.get(), RenderRadar::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_FIRECONTROLRADAR.get(), RenderFireControlRadar::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_SAMTURRET.get(), RenderSAMTurret::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_ESMTOWER.get(), RenderESMTower::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_CIWSTURRET.get(), RenderCIWSTurret::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_LASERTURRET.get(), RenderLaserTurret::new);
		event.registerBlockEntityRenderer(BallistixTiles.TILE_RAILGUNTURRET.get(), RenderRailgunTurret::new);

	}
	
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.register(BallistixParticles.PARTICLE_BLAST_SMOKE.get(),  ParticleBlastSmoke.Factory::new);
		event.register(BallistixParticles.PARTICLE_MISSILE_SMOKE.get(),  ParticleMissileSmoke.Factory::new);
		event.register(BallistixParticles.PARTICLE_SHOCKWAVE.get(),  ParticleShockwave.Factory::new);
	}

}
