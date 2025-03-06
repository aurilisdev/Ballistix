package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.entity.EntityMissile;
import electrodynamics.prefab.utilities.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class RenderMissile extends EntityRenderer<EntityMissile> {

	public RenderMissile(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.15F;
		shadowStrength = 0.75F;
	}

	@Override
	public void render(EntityMissile entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

		int type = entity.missileType;
		Level world = entity.level();

		// matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 1, 0), entity.getYRot() + 90.0F, true));
		// matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 0, 1), 90 - entity.getXRot(), true));


		if(type == -1) {
			return;
		}

		matrixStackIn.pushPose();

		matrixStackIn.mulPose(MathUtils.rotVectorQuaternionDeg(entity.getYRot() + 90.0F, MathUtils.YP));
		matrixStackIn.mulPose(MathUtils.rotVectorQuaternionDeg(90 - entity.getXRot(), MathUtils.ZP));

		BakedModel model;
		if (type == 0) {

			model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILETIER1);
			matrixStackIn.translate(0, 0.82, 0);
			matrixStackIn.scale(1f, 0.75f, 1f);

		} else if (type == 1) {

			model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILETIER2);
			matrixStackIn.translate(0, 1.05f, 0);
			matrixStackIn.scale(1f, 1f, 1f);

		} else {

			model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILETIER3);
			matrixStackIn.translate(0, 1.05f, 0);
			matrixStackIn.scale(1f, 1.25f, 1f);

		}

		Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, model, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);

		matrixStackIn.popPose();
	}

	@Override
	public boolean shouldRender(EntityMissile livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityMissile entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
