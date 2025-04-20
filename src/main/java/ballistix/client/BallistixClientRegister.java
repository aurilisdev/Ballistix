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
import ballistix.common.item.ItemTracker;
import ballistix.registers.BallistixDataComponentTypes;
import ballistix.registers.BallistixEntities;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixMenuTypes;
import ballistix.registers.BallistixParticles;
import ballistix.registers.BallistixTiles;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
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

	public static final ModelResourceLocation MODEL_RADARDISH = ModelResourceLocation.standalone(Ballistix.rl("block/radardish"));
	public static final ModelResourceLocation MODEL_FIRECONTROLRADARDISH = ModelResourceLocation.standalone(Ballistix.rl("block/firecontrolradardish"));
	public static final ModelResourceLocation MODEL_MISSILETIER1 = ModelResourceLocation.standalone(Ballistix.rl("entity/missiles/missiletier1"));
	public static final ModelResourceLocation MODEL_MISSILETIER2 = ModelResourceLocation.standalone(Ballistix.rl("entity/missiles/missiletier2"));
	public static final ModelResourceLocation MODEL_MISSILETIER3 = ModelResourceLocation.standalone(Ballistix.rl("entity/missiles/missiletier3"));
	public static final ModelResourceLocation MODEL_DARKMATTERSPHERE = ModelResourceLocation.standalone(Ballistix.rl("entity/darkmattersphere"));
	public static final ModelResourceLocation MODEL_DARKMATTERDISK = ModelResourceLocation.standalone(Ballistix.rl("entity/darkmatterdisk"));
	public static final ModelResourceLocation MODEL_FIREBALL = ModelResourceLocation.standalone(Ballistix.rl("entity/explosionsphere"));
	public static final ModelResourceLocation MODEL_EMP = ModelResourceLocation.standalone(Ballistix.rl("entity/emp"));
	public static final ModelResourceLocation MODEL_BLACKHOLECUBE = ModelResourceLocation.standalone(Ballistix.rl("entity/blackhole"));

	public static final ModelResourceLocation MODEL_AAMISSILE = ModelResourceLocation.standalone(Ballistix.rl("entity/aamissile"));
	public static final ModelResourceLocation MODEL_AAMISSILE_MK2 = ModelResourceLocation.standalone(Ballistix.rl("entity/missileantiballistic"));
	public static final ModelResourceLocation MODEL_SAMTURRET_BALLJOINT = ModelResourceLocation.standalone(Ballistix.rl("block/samturretballjoint"));
	public static final ModelResourceLocation MODEL_SAMTURRET_RAIL = ModelResourceLocation.standalone(Ballistix.rl("block/samturretrail"));
	public static final ModelResourceLocation MODEL_ESMTOWER = ModelResourceLocation.standalone(Ballistix.rl("block/esmtower"));
	public static final ModelResourceLocation MODEL_CIWSTURRET_BALLJOINT = ModelResourceLocation.standalone(Ballistix.rl("block/ciwsturretballjoint"));
	public static final ModelResourceLocation MODEL_CIWSTURRET_HEAD = ModelResourceLocation.standalone(Ballistix.rl("block/ciwsturrethead"));
	public static final ModelResourceLocation MODEL_CIWSTURRET_BARREL = ModelResourceLocation.standalone(Ballistix.rl("block/ciwsturretbarrel"));
	public static final ModelResourceLocation MODEL_LASERTURRET_BALLJOINT = ModelResourceLocation.standalone(Ballistix.rl("block/laserturretballjoint"));
	public static final ModelResourceLocation MODEL_LASERTURRET_HEAD = ModelResourceLocation.standalone(Ballistix.rl("block/laserturrethead"));
	public static final ModelResourceLocation MODEL_RAILGUNTURRET_BALLJOINT = ModelResourceLocation.standalone(Ballistix.rl("block/railgunturretballjoint"));
	public static final ModelResourceLocation MODEL_RAILGUNTURRET_HEAD = ModelResourceLocation.standalone(Ballistix.rl("block/railgunturretgun"));

	public static void setup() {
		ItemProperties.register(BallistixItems.ITEM_TRACKER.get(), ANGLE_PREDICATE, (stack, level, entity, seed) -> {
			//
			Entity sourceEntity = entity != null ? entity : stack.getEntityRepresentation();
			if (sourceEntity == null || !stack.has(BallistixDataComponentTypes.TRACKER_TARGET)) {
				return 0F;
			}

			ItemTracker.Target target = stack.get(BallistixDataComponentTypes.TRACKER_TARGET);

			double angleOfSource = 0.0D;
			if (entity instanceof Player player && player.isLocalPlayer()) {
				angleOfSource = entity.getYRot();
			} else if (sourceEntity instanceof ItemFrame itemFrameEntity) {
				Direction direction = itemFrameEntity.getDirection();
				int j = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
				angleOfSource = Mth.wrapDegrees(180 + direction.get2DDataValue() * 90L + itemFrameEntity.getRotation() * 45L + j);
			} else if (sourceEntity instanceof ItemEntity item) {
				angleOfSource = 180.0F - item.getSpin(0.5F) / ((float) Math.PI * 2F) * 360.0F;
			} else if (entity != null) {
				angleOfSource = entity.yBodyRot;
			}

			double rawAngleToTarget = Math.atan2(target.z() - sourceEntity.getZ(), target.x() - sourceEntity.getX()) / ((float) Math.PI * 2F);
			double adjustedAngleToTarget = 0.5D - (Mth.positiveModulo(angleOfSource / 360.0D, 1.0D) - 0.25D - rawAngleToTarget);

			return Mth.positiveModulo((float) adjustedAngleToTarget, 1.0F);
			//
		});

		ScreenGuidebook.addGuidebookModule(new ModuleBallistix());
	}

	@SubscribeEvent
	public static void registerMenus(RegisterMenuScreensEvent event) {
		event.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T1.get(), ScreenLauncherControlPanelT1::new);
		event.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T2.get(), ScreenLauncherControlPanelT2::new);
		event.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T3.get(), ScreenLauncherControlPanelT3::new);
		event.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T1.get(), ScreenLauncherPlatformT1::new);
		event.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T2.get(), ScreenLauncherPlatformT2::new);
		event.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T3.get(), ScreenLauncherPlatformT3::new);
		event.register(BallistixMenuTypes.CONTAINER_SAMTURRET.get(), ScreenSAMTurret::new);
		event.register(BallistixMenuTypes.CONTAINER_FIRECONTROLRADAR.get(), ScreenFireControlRadar::new);
		event.register(BallistixMenuTypes.CONTAINER_SEARCHRADAR.get(), ScreenSearchRadar::new);
		event.register(BallistixMenuTypes.CONTAINER_ESMTOWER.get(), ScreenESMTower::new);
		event.register(BallistixMenuTypes.CONTAINER_CIWSTURRET.get(), ScreenCIWSTurret::new);
		event.register(BallistixMenuTypes.CONTAINER_LASERTURRET.get(), ScreenLaserTurret::new);
		event.register(BallistixMenuTypes.CONTAINER_RAILGUNTURRET.get(), ScreenRailgunTurret::new);
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
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(BallistixParticles.PARTICLE_BLAST_SMOKE.get(), ParticleBlastSmoke.Factory::new);
		event.registerSpriteSet(BallistixParticles.PARTICLE_MISSILE_SMOKE.get(), ParticleMissileSmoke.Factory::new);
		event.registerSpriteSet(BallistixParticles.PARTICLE_SHOCKWAVE.get(), ParticleShockwave.Factory::new);
	}
}
