package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityExplosive;
import ballistix.registers.BallistixBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderExplosive extends EntityRenderer<EntityExplosive> {
	public RenderExplosive(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(EntityExplosive entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		SubtypeBlast subtype = entityIn.getBlastType();
		if (subtype != null) {
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.0D, 0.5D, 0.0D);
			if (entityIn.fuse - partialTicks + 1.0F < 10.0F) {
				float f = 1.0F - (entityIn.fuse - partialTicks + 1.0F) / 10.0F;
				f = MathHelper.clamp(f, 0.0F, 1.0F);
				f = f * f;
				f = f * f;
				float f1 = 1.0F + f * 0.3F;
				matrixStackIn.scale(f1, f1, f1);
			}

			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
			matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
			renderTntFlash(BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(subtype).get().defaultBlockState(), matrixStackIn, bufferIn, packedLightIn, entityIn.fuse / 5 % 2 == 0);
			matrixStackIn.popPose();
		}
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	public static void renderTntFlash(BlockState blockStateIn, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, int combinedLight, boolean doFullBright) {
		int i;
		if (doFullBright) {
			i = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
		} else {
			i = OverlayTexture.NO_OVERLAY;
		}

		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockStateIn, matrixStackIn, renderTypeBuffer, combinedLight, i);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityExplosive entity) {
		return AtlasTexture.LOCATION_BLOCKS;
	}
}