package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import ballistix.client.ClientRegister;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.settings.Constants;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBlast extends EntityRenderer<EntityBlast> {
	public RenderBlast(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(EntityBlast entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn,
			int packedLightIn) {
		SubtypeBlast subtype = entityIn.getBlastType();
		if (subtype == SubtypeBlast.darkmatter) {
			double x = entityIn.tickCount;
			double time = (4.0 / 3.0) * Math.PI * Math.pow(Constants.EXPLOSIVE_DARKMATTER_RADIUS, 3) / Constants.EXPLOSIVE_DARKMATTER_DURATION;
			float scale = (float) (0.1 * Math.log(x * x) + x/ (time * 2));
			BakedModel modelDisk = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_DARKMATTERDISK);
			BakedModel modelSphere = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_DARKMATTERSPHERE);

			float animationRadians = (entityIn.tickCount + partialTicks) * 0.05f;

			matrixStack.pushPose();
			matrixStack.translate(0D, 0.5D, 0D);
			matrixStack.scale(scale, scale, scale);
			RenderingUtils.renderModel(modelSphere, null, RenderType.solid(), matrixStack, bufferIn, packedLightIn, packedLightIn);
			matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -animationRadians, false));
			matrixStack.scale(1.25f, 1.25f, 1.25f);
			RenderingUtils.renderModel(modelDisk, null, RenderType.translucent(), matrixStack, bufferIn, packedLightIn, packedLightIn);
			matrixStack.popPose();

			matrixStack.pushPose();
			matrixStack.scale(scale, scale, scale);
			RenderingUtils.renderStar(matrixStack, bufferIn, entityIn.tickCount + partialTicks, 60, 1, 1, 1, 0.3f, true);
			matrixStack.popPose();
		}
//	else if (subtype == SubtypeBlast.nuclear && entityIn.shouldRenderCustom) {
//	    GlStateManager._pushMatrix();
//	    GlStateManager._enableBlend();
//	    GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
//	    GlStateManager._disableLighting();
//	    GlStateManager._enableDepthTest();
//	    GlStateManager._color4f(0.0F, 0.0F, 0.2F, 0.8f);
//	    RenderSystem.multMatrix(matrixStackIn.last().pose());
//	    matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
//	    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
//	    double di = 0.02;
//	    double dj = 0.04;
//	    double du = di * 2 * Math.PI;
//	    double dv = dj * Math.PI;
//	    float scale = (entityIn.tickCount - entityIn.ticksWhenCustomRender) / 2.0f;
//	    GL11.glScalef(scale, scale, scale);
//	    for (double i = 0; i < 1.0; i += di) {// horizonal
//		for (double j = 0; j < 1.0; j += dj) // vertical
//		{
//		    double u = i * 2 * Math.PI; // 0 to 2 Math.PI
//		    double v = (j - 0.5) * Math.PI; // - Math.PI/2 to Math.PI/2
//
//		    double[][] p = new double[][] { { Math.cos(v) * Math.cos(u), Math.cos(v) * Math.sin(u), Math.sin(v) },
//			    { Math.cos(v) * Math.cos(u + du), Math.cos(v) * Math.sin(u + du), Math.sin(v) },
//			    { Math.cos(v + dv) * Math.cos(u + du), Math.cos(v + dv) * Math.sin(u + du), Math.sin(v + dv) },
//			    { Math.cos(v + dv) * Math.cos(u), Math.cos(v + dv) * Math.sin(u), Math.sin(v + dv) } };
//		    GL11.glNormal3d(Math.cos(v + dv / 2) * Math.cos(u + du / 2), Math.cos(v + dv / 2) * Math.sin(u + du / 2), Math.sin(v + dv / 2));
//		    Minecraft.getInstance().textureManager.bind(ClientRegister.TEXTURE_FIREBALL);
//		    GL11.glBegin(GL11.GL_POLYGON);
//		    GL11.glColor4f(1, 1, 1,
//			    (float) (1.25f - (entityIn.tickCount - entityIn.ticksWhenCustomRender) / Constants.EXPLOSIVE_NUCLEAR_DURATION));
//		    GL11.glTexCoord2d(i, j);
//		    GL11.glVertex3dv(p[0]);
//		    GL11.glTexCoord2d(i + di, j);
//		    GL11.glVertex3dv(p[1]);
//		    GL11.glTexCoord2d(i + di, j + dj);
//		    GL11.glVertex3dv(p[2]);
//		    GL11.glTexCoord2d(i, j + dj);
//		    GL11.glVertex3dvzx(p[3]);
//		    GL11.glEnd();
//		}
//	    }
//	    GlStateManager._disableDepthTest();
//	    GlStateManager._enableLighting();
//	    GlStateManager._disableBlend();
//	    if (entityIn.tickCount - entityIn.ticksWhenCustomRender < 10) {
//		scale = (entityIn.tickCount - entityIn.ticksWhenCustomRender) * 5000f;
//		matrixStackIn.scale(scale, scale, scale);
//		UtilitiesRendering.renderStar(entityIn.tickCount, 500, 1, 1, 1, 0.7f, true);
//	    }
//	    GlStateManager._popMatrix();
//	    Lighting.turnBackOn();
//	} else if (subtype == SubtypeBlast.antimatter && entityIn.shouldRenderCustom) {
//	    GlStateManager._pushMatrix();
//	    GlStateManager._enableBlend();
//	    GlStateManager._blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
//	    GlStateManager._disableLighting();
//	    GlStateManager._enableDepthTest();
//	    GlStateManager._color4f(0.0F, 0.0F, 0.2F, 0.8f);
//	    RenderSystem.multMatrix(matrixStackIn.last().pose());
//	    matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
//	    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
//	    double di = 0.02;
//	    double dj = 0.04;
//	    double du = di * 2 * Math.PI;
//	    double dv = dj * Math.PI;
//	    float scale = (float) ((entityIn.tickCount - entityIn.ticksWhenCustomRender) / Constants.EXPLOSIVE_ANTIMATTER_DURATION
//		    * Constants.EXPLOSIVE_ANTIMATTER_RADIUS * 1.5);
//	    GL11.glScalef(scale, scale, scale);
//	    for (double i = 0; i < 1.0; i += di) {// horizonal
//		for (double j = 0; j < 1.0; j += dj) // vertical
//		{
//		    double u = i * 2 * Math.PI; // 0 to 2 Math.PI
//		    double v = (j - 0.5) * Math.PI; // - Math.PI/2 to Math.PI/2
//
//		    double[][] p = new double[][] { { Math.cos(v) * Math.cos(u), Math.cos(v) * Math.sin(u), Math.sin(v) },
//			    { Math.cos(v) * Math.cos(u + du), Math.cos(v) * Math.sin(u + du), Math.sin(v) },
//			    { Math.cos(v + dv) * Math.cos(u + du), Math.cos(v + dv) * Math.sin(u + du), Math.sin(v + dv) },
//			    { Math.cos(v + dv) * Math.cos(u), Math.cos(v + dv) * Math.sin(u), Math.sin(v + dv) } };
//		    GL11.glNormal3d(Math.cos(v + dv / 2) * Math.cos(u + du / 2), Math.cos(v + dv / 2) * Math.sin(u + du / 2), Math.sin(v + dv / 2));
//		    Minecraft.getInstance().textureManager.bind(ClientRegister.TEXTURE_FIREBALL);
//		    GL11.glBegin(GL11.GL_POLYGON);
//		    GL11.glColor4f(1, 1, 1,
//			    (float) (1.25f - (entityIn.tickCount - entityIn.ticksWhenCustomRender) / Constants.EXPLOSIVE_ANTIMATTER_DURATION));
//		    GL11.glTexCoord2d(i, j);
//		    GL11.glVertex3dv(p[0]);
//		    GL11.glTexCoord2d(i + di, j);
//		    GL11.glVertex3dv(p[1]);
//		    GL11.glTexCoord2d(i + di, j + dj);
//		    GL11.glVertex3dv(p[2]);
//		    GL11.glTexCoord2d(i, j + dj);
//		    GL11.glVertex3dv(p[3]);
//		    GL11.glEnd();
//		}
//	    }
//	    GlStateManager._disableDepthTest();
//	    GlStateManager._enableLighting();
//	    GlStateManager._disableBlend();
//	    GlStateManager._popMatrix();
//	    Lighting.turnBackOn();
//	}
		// TODO: Fix this rendering
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
