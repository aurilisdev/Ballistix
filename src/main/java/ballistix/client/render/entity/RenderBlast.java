package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.ClientRegister;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.settings.Constants;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class RenderBlast extends EntityRenderer<EntityBlast> {
	public RenderBlast(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(EntityBlast entityIn, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStack.pushPose();
		SubtypeBlast subtype = entityIn.getBlastType();
		if (subtype == SubtypeBlast.darkmatter) {
			double x = entityIn.tickCount;
			double time = 4.0 / 3.0 * Math.PI * Math.pow(Constants.EXPLOSIVE_DARKMATTER_RADIUS, 3) / Constants.EXPLOSIVE_DARKMATTER_DURATION;
			float scale = (float) (0.1 * Math.log(x * x) + x / (time * 2));
			IBakedModel modelDisk = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_DARKMATTERDISK);
			IBakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_BLACKHOLECUBE);

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
		} else if (subtype == SubtypeBlast.nuclear && entityIn.shouldRenderCustom) {
			float scale = (entityIn.tickCount - entityIn.ticksWhenCustomRender) / 20.0f;
			matrixStack.scale(scale, scale, scale);
			IBakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_FIREBALL);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Atlases.translucentItemSheet()), Blocks.BLACK_STAINED_GLASS.defaultBlockState(), modelSphere, 1, 1, 1, 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
			if (entityIn.tickCount - entityIn.ticksWhenCustomRender < 10) {
				matrixStack.scale(5, 5, 5);
				RenderingUtils.renderStar(entityIn.tickCount + partialTicks, 500, 1, 1, 1, 0.7f, false);
			}
		} else if (subtype == SubtypeBlast.emp && entityIn.shouldRenderCustom) {
			float scale = (float) ((entityIn.tickCount + partialTicks - entityIn.ticksWhenCustomRender) / Constants.EXPLOSIVE_ANTIMATTER_DURATION * Constants.EXPLOSIVE_EMP_RADIUS * 1.2) / 8.0f;
			matrixStack.scale(scale, scale, scale);
			IBakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_EMP);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Atlases.translucentCullBlockSheet()), Blocks.BLACK_STAINED_GLASS.defaultBlockState(), modelSphere, 1, 1, 1, 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
		}
		matrixStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
	}
	
	@Override
	public boolean shouldRender(EntityBlast entity, ClippingHelper frustum, double x, double y, double z) {
		return true;
	}
	
	@Override
	public ResourceLocation getTextureLocation(EntityBlast entity) {
		return AtlasTexture.LOCATION_BLOCKS;
	}
}