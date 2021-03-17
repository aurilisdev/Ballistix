package ballistix.client.render.entity;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import ballistix.client.ClientRegister;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.settings.Constants;
import electrodynamics.api.utilities.UtilitiesRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBlast extends EntityRenderer<EntityBlast> {
    public RenderBlast(EntityRendererManager renderManagerIn) {
	super(renderManagerIn);
	shadowSize = 0.5F;
    }

    @Override
    @Deprecated
    public void render(EntityBlast entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
	    int packedLightIn) {
	SubtypeBlast subtype = entityIn.getBlastType();
	if (subtype == SubtypeBlast.darkmatter) {

	    GlStateManager.pushMatrix();
	    RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
	    matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
	    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
	    float scale = entityIn.ticksExisted / 1200.0f;
	    GlStateManager.scalef(scale, scale, scale);

	    UtilitiesRendering.renderStar(entityIn.ticksExisted, 100, 1, 1, 1, 0.3f, true);

	    GlStateManager.popMatrix();
	} else if (subtype == SubtypeBlast.nuclear && entityIn.shouldRenderCustom) {
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
	    GlStateManager.disableLighting();
	    GlStateManager.enableDepthTest();
	    GlStateManager.color4f(0.0F, 0.0F, 0.2F, 0.8f);
	    RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
	    matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
	    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
	    double pi = 3.141592;
	    double di = 0.02;
	    double dj = 0.04;
	    double du = di * 2 * pi;
	    double dv = dj * pi;
	    float scale = (entityIn.ticksExisted - entityIn.ticksWhenCustomRender) / 2.0f;
	    GL11.glScalef(scale, scale, scale);
	    for (double i = 0; i < 1.0; i += di) {// horizonal
		for (double j = 0; j < 1.0; j += dj) // vertical
		{
		    double u = i * 2 * pi; // 0 to 2pi
		    double v = (j - 0.5) * pi; // -pi/2 to pi/2

		    double[][] p = new double[][] { { Math.cos(v) * Math.cos(u), Math.cos(v) * Math.sin(u), Math.sin(v) },
			    { Math.cos(v) * Math.cos(u + du), Math.cos(v) * Math.sin(u + du), Math.sin(v) },
			    { Math.cos(v + dv) * Math.cos(u + du), Math.cos(v + dv) * Math.sin(u + du), Math.sin(v + dv) },
			    { Math.cos(v + dv) * Math.cos(u), Math.cos(v + dv) * Math.sin(u), Math.sin(v + dv) } };
		    GL11.glNormal3d(Math.cos(v + dv / 2) * Math.cos(u + du / 2), Math.cos(v + dv / 2) * Math.sin(u + du / 2), Math.sin(v + dv / 2));
		    Minecraft.getInstance().textureManager.bindTexture(ClientRegister.TEXTURE_FIREBALL);
		    GL11.glBegin(GL11.GL_POLYGON);
		    GL11.glColor4f(1, 1, 1,
			    (float) (1.25f - (entityIn.ticksExisted - entityIn.ticksWhenCustomRender) / Constants.EXPLOSIVE_NUCLEAR_DURATION));
		    GL11.glTexCoord2d(i, j);
		    GL11.glVertex3dv(p[0]);
		    GL11.glTexCoord2d(i + di, j);
		    GL11.glVertex3dv(p[1]);
		    GL11.glTexCoord2d(i + di, j + dj);
		    GL11.glVertex3dv(p[2]);
		    GL11.glTexCoord2d(i, j + dj);
		    GL11.glVertex3dv(p[3]);
		    GL11.glEnd();
		}
	    }
	    GlStateManager.disableDepthTest();
	    GlStateManager.enableLighting();
	    GlStateManager.disableBlend();
	    if (entityIn.ticksExisted - entityIn.ticksWhenCustomRender < 10) {
		scale = (entityIn.ticksExisted - entityIn.ticksWhenCustomRender) * 5000;
		matrixStackIn.scale(scale, scale, scale);
		UtilitiesRendering.renderStar(entityIn.ticksExisted, 500, 1, 1, 1, 0.7f, true);
	    }
	    GlStateManager.popMatrix();
	    RenderHelper.enableStandardItemLighting();
	} else if (subtype == SubtypeBlast.antimatter && entityIn.shouldRenderCustom) {
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
	    GlStateManager.disableLighting();
	    GlStateManager.enableDepthTest();
	    GlStateManager.color4f(0.0F, 0.0F, 0.2F, 0.8f);
	    RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
	    matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
	    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
	    double pi = 3.141592;
	    double di = 0.02;
	    double dj = 0.04;
	    double du = di * 2 * pi;
	    double dv = dj * pi;
	    float scale = (float) ((entityIn.ticksExisted - entityIn.ticksWhenCustomRender) / Constants.EXPLOSIVE_ANTIMATTER_DURATION
		    * Constants.EXPLOSIVE_ANTIMATTER_RADIUS);
	    GL11.glScalef(scale, scale, scale);
	    for (double i = 0; i < 1.0; i += di) {// horizonal
		for (double j = 0; j < 1.0; j += dj) // vertical
		{
		    double u = i * 2 * pi; // 0 to 2pi
		    double v = (j - 0.5) * pi; // -pi/2 to pi/2

		    double[][] p = new double[][] { { Math.cos(v) * Math.cos(u), Math.cos(v) * Math.sin(u), Math.sin(v) },
			    { Math.cos(v) * Math.cos(u + du), Math.cos(v) * Math.sin(u + du), Math.sin(v) },
			    { Math.cos(v + dv) * Math.cos(u + du), Math.cos(v + dv) * Math.sin(u + du), Math.sin(v + dv) },
			    { Math.cos(v + dv) * Math.cos(u), Math.cos(v + dv) * Math.sin(u), Math.sin(v + dv) } };
		    GL11.glNormal3d(Math.cos(v + dv / 2) * Math.cos(u + du / 2), Math.cos(v + dv / 2) * Math.sin(u + du / 2), Math.sin(v + dv / 2));
		    Minecraft.getInstance().textureManager.bindTexture(ClientRegister.TEXTURE_FIREBALL);
		    GL11.glBegin(GL11.GL_POLYGON);
		    GL11.glColor4f(1, 1, 1,
			    (float) (1.25f - (entityIn.ticksExisted - entityIn.ticksWhenCustomRender) / Constants.EXPLOSIVE_ANTIMATTER_DURATION));
		    GL11.glTexCoord2d(i, j);
		    GL11.glVertex3dv(p[0]);
		    GL11.glTexCoord2d(i + di + entityIn.world.rand.nextFloat() * 2, j + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[1]);
		    GL11.glTexCoord2d(i + di + entityIn.world.rand.nextFloat() * 2, j + dj + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[2]);
		    GL11.glTexCoord2d(i + entityIn.world.rand.nextFloat() * 2, j + dj + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[3]);
		    GL11.glEnd();
		}
	    }
	    GlStateManager.disableDepthTest();
	    GlStateManager.enableLighting();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	    RenderHelper.enableStandardItemLighting();
	}
	super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    @Deprecated
    public ResourceLocation getEntityTexture(EntityBlast entity) {
	return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
