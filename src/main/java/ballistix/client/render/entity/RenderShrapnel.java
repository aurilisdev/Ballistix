package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import ballistix.client.ClientRegister;
import ballistix.common.entity.EntityShrapnel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShrapnel extends EntityRenderer<EntityShrapnel> {
    public RenderShrapnel(EntityRendererManager renderManagerIn) {
	super(renderManagerIn);
	shadowSize = 0.1F;
    }

    @Override
    @Deprecated
    public void render(EntityShrapnel entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
	    int packedLightIn) {
	matrixStackIn.translate(entity.getMotion().x * partialTicks, entity.getMotion().y * partialTicks, entity.getMotion().z * partialTicks);
	matrixStackIn.push();
	GlStateManager.pushMatrix();
	Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(entity));
	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	GlStateManager.enableDepthTest();
	GlStateManager.disableLighting();
	RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
	matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
	matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
	GlStateManager.rotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
	GlStateManager.rotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
	Tessellator tessellator = Tessellator.getInstance();
	BufferBuilder bufferbuilder = tessellator.getBuffer();

	GlStateManager.enableRescaleNormal();

	GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
	GlStateManager.scalef(0.015625F, 0.015625F, 0.015625F);
	GlStateManager.translatef(-4.0F, 0.0F, 0.0F);

	GlStateManager.normal3f(0.05625F, 0.0F, 0.0F);
	bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
	bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0f, 0.15625f).endVertex();
	bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625f, 0.15625f).endVertex();
	bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625f, 0.3125f).endVertex();
	bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0f, 0.3125f).endVertex();
	tessellator.draw();

	GlStateManager.normal3f(-0.05625F, 0.0F, 0.0F);
	bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
	bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0f, 0.15625f).endVertex();
	bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625f, 0.15625f).endVertex();
	bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625f, 0.3125f).endVertex();
	bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0f, 0.3125f).endVertex();
	tessellator.draw();

	for (int j = 0; j < 4; ++j) {
	    GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
	    GlStateManager.normal3f(0.0F, 0.0F, 0.05625F);
	    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
	    bufferbuilder.pos(-8.0D, -2.0D, 0.0D).tex(0.0f, 0.0f).endVertex();
	    bufferbuilder.pos(8.0D, -2.0D, 0.0D).tex(0.5f, 0.0f).endVertex();
	    bufferbuilder.pos(8.0D, 2.0D, 0.0D).tex(0.5f, 0.15625f).endVertex();
	    bufferbuilder.pos(-8.0D, 2.0D, 0.0D).tex(0.0f, 0.15625f).endVertex();
	    tessellator.draw();
	}

	GlStateManager.disableRescaleNormal();
	GlStateManager.enableLighting();
	GlStateManager.disableDepthTest();
	GlStateManager.popMatrix();
	matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityShrapnel entity) {
	return ClientRegister.TEXTURE_SHRAPNEL;
    }
}
