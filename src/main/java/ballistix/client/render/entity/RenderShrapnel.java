package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.client.ClientRegister;
import ballistix.common.entity.EntityShrapnel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShrapnel extends EntityRenderer<EntityShrapnel> {
	public RenderShrapnel(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.1F;
	}

	@Override
	public void render(EntityShrapnel entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int packedLightIn) {
		matrixStackIn.translate(entity.getDeltaMovement().x * partialTicks, entity.getDeltaMovement().y * partialTicks,
				entity.getDeltaMovement().z * partialTicks);
		matrixStackIn.pushPose();
		// GlStateManager._pushMatrix();
		// Minecraft.getInstance().textureManager.bind(getTextureLocation(entity));
		// GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
		// GlStateManager._enableDepthTest();
		// GlStateManager._disableLighting();
		// RenderSystem.multMatrix(matrixStackIn.last().pose());
		// matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
		// matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		// GlStateManager._rotatef(entity.yRotO + (entity.yRot - entity.yRotO) *
		// partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		// GlStateManager._rotatef(entity.xRotO + (entity.xRot - entity.xRotO) *
		// partialTicks, 0.0F, 0.0F, 1.0F);
		// Tesselator tessellator = Tesselator.getInstance();
		// BufferBuilder bufferbuilder = tessellator.getBuilder();
		//
		// GlStateManager._enableRescaleNormal();
		//
		// GlStateManager._rotatef(45.0F, 1.0F, 0.0F, 0.0F);
		// GlStateManager._scalef(0.015625F, 0.015625F, 0.015625F);
		// GlStateManager._translatef(-4.0F, 0.0F, 0.0F);
		//
		// GlStateManager._normal3f(0.05625F, 0.0F, 0.0F);
		// bufferbuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
		// bufferbuilder.vertex(-7.0D, -2.0D, -2.0D).uv(0.0f, 0.15625f).endVertex();
		// bufferbuilder.vertex(-7.0D, -2.0D, 2.0D).uv(0.15625f, 0.15625f).endVertex();
		// bufferbuilder.vertex(-7.0D, 2.0D, 2.0D).uv(0.15625f, 0.3125f).endVertex();
		// bufferbuilder.vertex(-7.0D, 2.0D, -2.0D).uv(0.0f, 0.3125f).endVertex();
		// tessellator.end();
		//
		// GlStateManager._normal3f(-0.05625F, 0.0F, 0.0F);
		// bufferbuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
		// bufferbuilder.vertex(-7.0D, 2.0D, -2.0D).uv(0.0f, 0.15625f).endVertex();
		// bufferbuilder.vertex(-7.0D, 2.0D, 2.0D).uv(0.15625f, 0.15625f).endVertex();
		// bufferbuilder.vertex(-7.0D, -2.0D, 2.0D).uv(0.15625f, 0.3125f).endVertex();
		// bufferbuilder.vertex(-7.0D, -2.0D, -2.0D).uv(0.0f, 0.3125f).endVertex();
		// tessellator.end();
		//
		// for (int j = 0; j < 4; ++j) {
		// GlStateManager._rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		// GlStateManager._normal3f(0.0F, 0.0F, 0.05625F);
		// bufferbuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
		// bufferbuilder.vertex(-8.0D, -2.0D, 0.0D).uv(0.0f, 0.0f).endVertex();
		// bufferbuilder.vertex(8.0D, -2.0D, 0.0D).uv(0.5f, 0.0f).endVertex();
		// bufferbuilder.vertex(8.0D, 2.0D, 0.0D).uv(0.5f, 0.15625f).endVertex();
		// bufferbuilder.vertex(-8.0D, 2.0D, 0.0D).uv(0.0f, 0.15625f).endVertex();
		// tessellator.end();
		// }
		//
		// GlStateManager._disableRescaleNormal();
		// GlStateManager._enableLighting();
		// GlStateManager._disableDepthTest();
		// GlStateManager._popMatrix();
		// TODO: Fix this rendering
		matrixStackIn.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityShrapnel entity) {
		return ClientRegister.TEXTURE_SHRAPNEL;
	}
}
