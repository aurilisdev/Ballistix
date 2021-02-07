package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.block.SubtypeExplosive;
import ballistix.common.entity.EntityBlast;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBlast extends EntityRenderer<EntityBlast> {
	public RenderBlast(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowSize = 0.5F;
	}

	@Override
	public void render(EntityBlast entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		SubtypeExplosive subtype = entityIn.getExplosiveType();
		if (subtype != null) {
			matrixStackIn.push();
			matrixStackIn.pop();
		}
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	@SuppressWarnings("deprecation")
	public ResourceLocation getEntityTexture(EntityBlast entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
