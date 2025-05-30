package ballistix.client;

import ballistix.Ballistix;
import ballistix.client.event.RegisterBlastRenderersEvent;
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
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.item.ItemTracker;
import ballistix.common.settings.BallistixConstants;
import ballistix.registers.BallistixTiles;
import ballistix.registers.BallistixEntities;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixMenuTypes;
import ballistix.registers.BallistixParticles;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.Voltaic;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.prefab.utilities.RenderingUtils;

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
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T1.get(), ScreenLauncherControlPanelT1::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T2.get(), ScreenLauncherControlPanelT2::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LAUNCHER_CONTROL_PANEL_T3.get(), ScreenLauncherControlPanelT3::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T1.get(), ScreenLauncherPlatformT1::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T2.get(), ScreenLauncherPlatformT2::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LAUNCHER_PLATFORM_T3.get(), ScreenLauncherPlatformT3::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_SAMTURRET.get(), ScreenSAMTurret::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_FIRECONTROLRADAR.get(), ScreenFireControlRadar::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_SEARCHRADAR.get(), ScreenSearchRadar::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_ESMTOWER.get(), ScreenESMTower::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_CIWSTURRET.get(), ScreenCIWSTurret::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_LASERTURRET.get(), ScreenLaserTurret::new);
		ScreenManager.register(BallistixMenuTypes.CONTAINER_RAILGUNTURRET.get(), ScreenRailgunTurret::new);

		ScreenGuidebook.addGuidebookModule(new ModuleBallistix());

		ItemModelsProperties.register(BallistixItems.ITEM_TRACKER.get(), ANGLE_PREDICATE, (stack, level, entity) -> {
			//
			Entity sourceEntity = entity != null ? entity : stack.getEntityRepresentation();
			if (sourceEntity == null || !ItemTracker.hasTarget(stack)) {
				return 0F;
			}

			double targetX = ItemTracker.getX(stack);
			double targetZ = ItemTracker.getZ(stack);

			double angleOfSource = 0.0D;
			if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isLocalPlayer()) {
				angleOfSource = entity.yRot;
			} else if (sourceEntity instanceof ItemFrameEntity) {
				ItemFrameEntity itemFrameEntity = (ItemFrameEntity) sourceEntity;
				Direction direction = itemFrameEntity.getDirection();
				int j = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
				angleOfSource = MathHelper.wrapDegrees(180 + direction.get2DDataValue() * 90L + itemFrameEntity.getRotation() * 45L + j);
			} else if (sourceEntity instanceof ItemEntity) {
				ItemEntity item = (ItemEntity) sourceEntity;
				angleOfSource = 180.0F - item.getSpin(0.5F) / ((float) Math.PI * 2F) * 360.0F;
			} else if (entity != null) {
				angleOfSource = entity.yBodyRot;
			}

			double rawAngleToTarget = Math.atan2(targetZ - sourceEntity.getZ(), targetX - sourceEntity.getX()) / ((float) Math.PI * 2F);
			double adjustedAngleToTarget = 0.5D - (MathHelper.positiveModulo(angleOfSource / 360.0D, 1.0D) - 0.25D - rawAngleToTarget);

			return MathHelper.positiveModulo((float) adjustedAngleToTarget, 1.0F);
			//
		});

		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER1.get(), RenderLauncherPlatform::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER2.get(), RenderLauncherPlatform::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER3.get(), RenderLauncherPlatform::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_RADAR.get(), RenderRadar::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_FIRECONTROLRADAR.get(), RenderFireControlRadar::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_SAMTURRET.get(), RenderSAMTurret::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_ESMTOWER.get(), RenderESMTower::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_CIWSTURRET.get(), RenderCIWSTurret::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_LASERTURRET.get(), RenderLaserTurret::new);
		ClientRegistry.bindTileEntityRenderer(BallistixTiles.TILE_RAILGUNTURRET.get(), RenderRailgunTurret::new);

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
		
		RegisterBlastRenderersEvent registerBlastRenderers = new RegisterBlastRenderersEvent();
		ModLoader.get().postEvent(registerBlastRenderers);
		registerBlastRenderers.process();

	}

	@SubscribeEvent
	public static void onModelEvent(ModelRegistryEvent event) {
		ModelLoader.addSpecialModel(MODEL_RADARDISH);
		ModelLoader.addSpecialModel(MODEL_FIRECONTROLRADARDISH);
		ModelLoader.addSpecialModel(MODEL_MISSILETIER1);
		ModelLoader.addSpecialModel(MODEL_MISSILETIER2);
		ModelLoader.addSpecialModel(MODEL_MISSILETIER3);
		ModelLoader.addSpecialModel(MODEL_DARKMATTERSPHERE);
		ModelLoader.addSpecialModel(MODEL_DARKMATTERDISK);
		ModelLoader.addSpecialModel(MODEL_FIREBALL);
		ModelLoader.addSpecialModel(MODEL_EMP);
		ModelLoader.addSpecialModel(MODEL_BLACKHOLECUBE);
		ModelLoader.addSpecialModel(MODEL_AAMISSILE);
		ModelLoader.addSpecialModel(MODEL_AAMISSILE_MK2);
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

	@SubscribeEvent
	public static void registerParticles(ParticleFactoryRegisterEvent event) {
		ParticleManager engine = Minecraft.getInstance().particleEngine;
		engine.register(BallistixParticles.PARTICLE_BLAST_SMOKE.get(), ParticleBlastSmoke.Factory::new);
		engine.register(BallistixParticles.PARTICLE_MISSILE_SMOKE.get(), ParticleMissileSmoke.Factory::new);
		engine.register(BallistixParticles.PARTICLE_SHOCKWAVE.get(), ParticleShockwave.Factory::new);
	}
	
	@SubscribeEvent
	public static void registerBlastRenderers(RegisterBlastRenderersEvent event) {

		event.register(SubtypeBlast.darkmatter, (entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn) -> {
			double x = entityIn.tickCount;
			double time = 4.0 / 3.0 * Math.PI * Math.pow(BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, 3) / BallistixConstants.EXPLOSIVE_DARKMATTER_DURATION;
			float scale = (float) (0.1 * Math.log(x * x) + x / (time * 2));
			IBakedModel modelDisk = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_DARKMATTERDISK);
			IBakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_BLACKHOLECUBE);

			float animationRadians = (entityIn.tickCount + partialTicks) * 0.05f;

			matrixStack.pushPose();
			matrixStack.scale(scale * 6, scale * 6, scale * 6);
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -animationRadians, false));
			matrixStack.mulPose(new Quaternion(new Vector3f(1, 0, 0), -animationRadians, false));
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 0, 1), -animationRadians, false));
			RenderingUtils.renderModel(modelSphere, null, RenderType.solid(), matrixStack, bufferIn, packedLightIn, packedLightIn);
			matrixStack.popPose();

			matrixStack.pushPose();
			matrixStack.translate(0, 0.5, 0);
			matrixStack.scale(scale, scale, scale);
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -animationRadians, false));
			matrixStack.scale(1.25f, 1.25f, 1.25f);
			RenderingUtils.renderModel(modelDisk, null, RenderType.translucent(), matrixStack, bufferIn, packedLightIn, packedLightIn);
			matrixStack.popPose();

			matrixStack.pushPose();
			matrixStack.scale(scale, scale, scale);
			RenderingUtils.renderStar(entityIn.tickCount + partialTicks, 60, 1, 1, 1, 0.3f, true);
			matrixStack.popPose();
		});

		event.register(SubtypeBlast.nuclear, (entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn) -> {

			if(!entityIn.shouldRenderCustom) {
				return;
			}

			float scale = (entityIn.tickCount - entityIn.ticksWhenCustomRender) / 20.0f;
			matrixStack.scale(scale, scale, scale);

			if (entityIn.tickCount - entityIn.ticksWhenCustomRender < 10) {
				matrixStack.scale(5, 5, 5);
				RenderingUtils.renderStar(entityIn.tickCount + partialTicks, 500, 1, 1, 1, 0.7f, false);
			}

		});

		event.register(SubtypeBlast.emp, (entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn) -> {

			if(!entityIn.shouldRenderCustom) {
				return;
			}

			float scale = (float) ((entityIn.tickCount + partialTicks - entityIn.ticksWhenCustomRender) / BallistixConstants.EXPLOSIVE_ANTIMATTER_DURATION * BallistixConstants.EXPLOSIVE_EMP_RADIUS * 1.2) / 8.0f;
			matrixStack.scale(scale, scale, scale);
			IBakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_EMP);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Atlases.translucentCullBlockSheet()), Blocks.BLACK_STAINED_GLASS.defaultBlockState(), modelSphere, 1, 1, 1, 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);

		});

		event.register(SubtypeBlast.antimatter, (entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn) -> {

			if(!entityIn.shouldRenderCustom) {
				return;
			}

			//TODO implement?

		});

		event.register(SubtypeBlast.largeantimatter, (entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn) -> {

			if(!entityIn.shouldRenderCustom) {
				return;
			}

			//TODO implement?

		});

	}

}
