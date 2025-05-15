package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import ballistix.client.BallistixClientRegister;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.settings.BallistixConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import voltaic.prefab.utilities.RenderingUtils;

public class RenderBlast extends EntityRenderer<EntityBlast> {

	public RenderBlast(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(EntityBlast entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
		matrixStack.pushPose();
		SubtypeBlast subtype = entityIn.getBlastType();

		if (subtype == SubtypeBlast.darkmatter) {
			double x = entityIn.tickCount;
			double time = 4.0 / 3.0 * Math.PI * Math.pow(BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, 3) / BallistixConstants.EXPLOSIVE_DARKMATTER_DURATION;
			float scale = (float) (0.1 * Math.log(x * x) + x / (time * 2));
			BakedModel modelDisk = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_DARKMATTERDISK);
			BakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_BLACKHOLECUBE);

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
			RenderingUtils.renderStar(matrixStack, bufferIn, entityIn.tickCount + partialTicks, 60, 1, 1, 1, 0.3f, true);
			matrixStack.popPose();
		} else if (subtype == SubtypeBlast.nuclear && entityIn.shouldRenderCustom) {
			float scale = (entityIn.tickCount - entityIn.ticksWhenCustomRender) / 20.0f;
			matrixStack.scale(scale, scale, scale);
//            BakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_FIREBALL);
//            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Sheets.translucentItemSheet()), Blocks.BLACK_STAINED_GLASS.defaultBlockState(), modelSphere, 1, 1, 1, 0, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
			if (entityIn.tickCount - entityIn.ticksWhenCustomRender < 10) {
				matrixStack.scale(5, 5, 5);
				RenderingUtils.renderStar(matrixStack, bufferIn, entityIn.tickCount + partialTicks, 500, 1, 1, 1, 0.7f, false);
			}
		} else if (subtype == SubtypeBlast.emp && entityIn.shouldRenderCustom) {
			float scale = (float) ((entityIn.tickCount + partialTicks - entityIn.ticksWhenCustomRender) / BallistixConstants.EXPLOSIVE_ANTIMATTER_DURATION * BallistixConstants.EXPLOSIVE_EMP_RADIUS * 1.2) / 8.0f;
			matrixStack.scale(scale, scale, scale);
			BakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_EMP);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Sheets.translucentCullBlockSheet()), Blocks.BLACK_STAINED_GLASS.defaultBlockState(), modelSphere, 1, 1, 1, 0, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucent());
		} else if (subtype == SubtypeBlast.antimatter && entityIn.shouldRenderCustom) {
//	    float scale = (float) ((entityIn.tickCount + partialTicks - entityIn.ticksWhenCustomRender)
//		    / Constants.EXPLOSIVE_ANTIMATTER_DURATION * Constants.EXPLOSIVE_ANTIMATTER_RADIUS);
//	    matrixStack.scale(0.1f, 0.1f, 0.1f);
//	    matrixStack.scale(scale, scale, scale);
//	    BakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_FIREBALL);
//	    RenderingUtils.renderModel(modelSphere, null, RenderType.translucent(), matrixStack, bufferIn,
//		    packedLightIn, packedLightIn);
		} else if (subtype == SubtypeBlast.largeantimatter && entityIn.shouldRenderCustom) {
//	    float scale = (float) ((entityIn.tickCount + partialTicks - entityIn.ticksWhenCustomRender)
//		    / Constants.EXPLOSIVE_ANTIMATTER_DURATION * Constants.EXPLOSIVE_ANTIMATTER_RADIUS);
//	    matrixStack.scale(0.1f, 0.1f, 0.1f);
//	    matrixStack.scale(scale, scale, scale);
//	    BakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_FIREBALL);
//	    RenderingUtils.renderModel(modelSphere, null, RenderType.translucent(), matrixStack, bufferIn,
//		    packedLightIn, packedLightIn);
		}
		matrixStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
	}

	@Override
	public boolean shouldRender(EntityBlast b, Frustum f, double x, double y, double z) {
		return true;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityBlast entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
