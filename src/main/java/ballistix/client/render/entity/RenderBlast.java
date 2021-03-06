package ballistix.client.render.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import ballistix.client.ClientRegister;
import ballistix.common.block.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.settings.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

    @Deprecated
    public static void renderStar(float time, int starFrags) {
	Tessellator tessellator = Tessellator.getInstance();
	BufferBuilder bufferBuilder = tessellator.getBuffer();
	GlStateManager.disableTexture();
	GlStateManager.shadeModel(GL11.GL_SMOOTH);
	GlStateManager.enableBlend();
	GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	GlStateManager.disableAlphaTest();
	GlStateManager.enableCull();
	GlStateManager.enableDepthTest();

	GlStateManager.pushMatrix();
	try {
	    float par2 = time * 3 % 180;
	    float var41 = (5.0F + par2) / 200.0F;
	    float var51 = 0.0F;
	    if (var41 > 0.8F) {
		var51 = (var41 - 0.8F) / 0.2F;
	    }
	    Random rand = new Random(432L);
	    for (int i1 = 0; i1 < starFrags; i1++) {
		GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rand.nextFloat() * 360.0F + var41 * 90.0F, 0.0F, 0.0F, 1.0F);
		final float f2 = rand.nextFloat() * 20 + 5 + var51 * 10;
		final float f3 = rand.nextFloat() * 2 + 1 + var51 * 2;
		bufferBuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
		bufferBuilder.pos(0, 0, 0).color(255, 255, 255, 200).endVertex();
		bufferBuilder.pos(-0.866 * f3, f2, -0.5 * f3).color(255, 255, 255, 0).endVertex();
		bufferBuilder.pos(0.866 * f3, f2, -0.5 * f3).color(255, 255, 255, 0).endVertex();
		bufferBuilder.pos(0, f2, 1 * f3).color(255, 255, 255, 0).endVertex();
		bufferBuilder.pos(-0.866 * f3, f2, -0.5 * f3).color(255, 255, 255, 0).endVertex();
		tessellator.draw();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	GlStateManager.popMatrix();

	GlStateManager.disableDepthTest();
	GlStateManager.disableBlend();
	GlStateManager.shadeModel(GL11.GL_FLAT);
	GlStateManager.color4f(1, 1, 1, 1);
	GlStateManager.enableTexture();
	GlStateManager.enableAlphaTest();
    }

    @Override
    @Deprecated
    public void render(EntityBlast entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
	    IRenderTypeBuffer bufferIn, int packedLightIn) {
	SubtypeBlast subtype = entityIn.getBlastType();
	if (subtype == SubtypeBlast.darkmatter) {

	    GlStateManager.pushMatrix();
	    RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
	    matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
	    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
	    float scale = entityIn.ticksExisted / 1200.0f;
	    GlStateManager.scalef(scale, scale, scale);

	    renderStar(entityIn.ticksExisted, 100);

	    GlStateManager.popMatrix();
	} else if (subtype == SubtypeBlast.nuclear && entityIn.shouldRenderCustom) {
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param,
		    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
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

		    double[][] p = new double[][] {
			    { Math.cos(v) * Math.cos(u), Math.cos(v) * Math.sin(u), Math.sin(v) },
			    { Math.cos(v) * Math.cos(u + du), Math.cos(v) * Math.sin(u + du), Math.sin(v) },
			    { Math.cos(v + dv) * Math.cos(u + du), Math.cos(v + dv) * Math.sin(u + du),
				    Math.sin(v + dv) },
			    { Math.cos(v + dv) * Math.cos(u), Math.cos(v + dv) * Math.sin(u), Math.sin(v + dv) } };
		    GL11.glNormal3d(Math.cos(v + dv / 2) * Math.cos(u + du / 2),
			    Math.cos(v + dv / 2) * Math.sin(u + du / 2), Math.sin(v + dv / 2));
		    Minecraft.getInstance().textureManager.bindTexture(ClientRegister.TEXTURE_FIREBALL);
		    GL11.glBegin(GL11.GL_POLYGON);
		    GL11.glColor4f(1, 1, 1, (float) (1.25f - (entityIn.ticksExisted - entityIn.ticksWhenCustomRender)
			    / Constants.EXPLOSIVE_NUCLEAR_DURATION));
		    GL11.glTexCoord2d(i, j);
		    GL11.glVertex3dv(p[0]);
		    GL11.glTexCoord2d(i + di + entityIn.world.rand.nextFloat() * 2,
			    j + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[1]);
		    GL11.glTexCoord2d(i + di + entityIn.world.rand.nextFloat() * 2,
			    j + dj + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[2]);
		    GL11.glTexCoord2d(i + entityIn.world.rand.nextFloat() * 2,
			    j + dj + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[3]);
		    GL11.glEnd();
		}
	    }
	    GlStateManager.disableDepthTest();
	    GlStateManager.enableLighting();
	    GlStateManager.disableBlend();
	    if (entityIn.ticksExisted - entityIn.ticksWhenCustomRender < 10) {
		scale = entityIn.ticksExisted - entityIn.ticksWhenCustomRender;
		matrixStackIn.scale(scale, scale, scale);
		renderStar(entityIn.ticksExisted, 500);
	    }
	    GlStateManager.popMatrix();
	    RenderHelper.enableStandardItemLighting();
	} else if (subtype == SubtypeBlast.antimatter && entityIn.shouldRenderCustom) {
	    GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param,
		    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);
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
	    float scale = (float) (((entityIn.ticksExisted - entityIn.ticksWhenCustomRender)
		    / Constants.EXPLOSIVE_ANTIMATTER_DURATION) * Constants.EXPLOSIVE_ANTIMATTER_RADIUS);
	    GL11.glScalef(scale, scale, scale);
	    for (double i = 0; i < 1.0; i += di) {// horizonal
		for (double j = 0; j < 1.0; j += dj) // vertical
		{
		    double u = i * 2 * pi; // 0 to 2pi
		    double v = (j - 0.5) * pi; // -pi/2 to pi/2

		    double[][] p = new double[][] {
			    { Math.cos(v) * Math.cos(u), Math.cos(v) * Math.sin(u), Math.sin(v) },
			    { Math.cos(v) * Math.cos(u + du), Math.cos(v) * Math.sin(u + du), Math.sin(v) },
			    { Math.cos(v + dv) * Math.cos(u + du), Math.cos(v + dv) * Math.sin(u + du),
				    Math.sin(v + dv) },
			    { Math.cos(v + dv) * Math.cos(u), Math.cos(v + dv) * Math.sin(u), Math.sin(v + dv) } };
		    GL11.glNormal3d(Math.cos(v + dv / 2) * Math.cos(u + du / 2),
			    Math.cos(v + dv / 2) * Math.sin(u + du / 2), Math.sin(v + dv / 2));
		    Minecraft.getInstance().textureManager.bindTexture(ClientRegister.TEXTURE_FIREBALL);
		    GL11.glBegin(GL11.GL_POLYGON);
		    GL11.glColor4f(1, 1, 1, (float) (1.25f - (entityIn.ticksExisted - entityIn.ticksWhenCustomRender)
			    / Constants.EXPLOSIVE_ANTIMATTER_DURATION));
		    GL11.glTexCoord2d(i, j);
		    GL11.glVertex3dv(p[0]);
		    GL11.glTexCoord2d(i + di + entityIn.world.rand.nextFloat() * 2,
			    j + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[1]);
		    GL11.glTexCoord2d(i + di + entityIn.world.rand.nextFloat() * 2,
			    j + dj + entityIn.world.rand.nextFloat() * 2);
		    GL11.glVertex3dv(p[2]);
		    GL11.glTexCoord2d(i + entityIn.world.rand.nextFloat() * 2,
			    j + dj + entityIn.world.rand.nextFloat() * 2);
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
