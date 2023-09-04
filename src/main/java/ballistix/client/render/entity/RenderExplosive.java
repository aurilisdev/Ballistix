package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityExplosive;
import ballistix.registers.BallistixBlocks;
import electrodynamics.prefab.utilities.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderExplosive extends EntityRenderer<EntityExplosive> {
	public RenderExplosive(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(EntityExplosive entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		SubtypeBlast subtype = entityIn.getBlastType();
		if (subtype != null) {
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.0D, 0.5D, 0.0D);
			if (entityIn.fuse - partialTicks + 1.0F < 10.0F) {
				float f = 1.0F - (entityIn.fuse - partialTicks + 1.0F) / 10.0F;
				f = Mth.clamp(f, 0.0F, 1.0F);
				f = f * f;
				f = f * f;
				float f1 = 1.0F + f * 0.3F;
				matrixStackIn.scale(f1, f1, f1);
			}

			matrixStackIn.mulPose(MathUtils.rotVectorQuaternionDeg(-90.0F, MathUtils.YP));
			//matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
			matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
			matrixStackIn.mulPose(MathUtils.rotVectorQuaternionDeg(90.0F, MathUtils.YP));
			//matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
			renderTntFlash(BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(subtype).get().defaultBlockState(), matrixStackIn, bufferIn, packedLightIn, entityIn.fuse / 5 % 2 == 0);
			matrixStackIn.popPose();
		}
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	public static void renderTntFlash(BlockState blockStateIn, PoseStack matrixStackIn, MultiBufferSource renderTypeBuffer, int combinedLight, boolean doFullBright) {
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
		return InventoryMenu.BLOCK_ATLAS;
	}
}
