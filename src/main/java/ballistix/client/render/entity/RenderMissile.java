package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.BallistixClientRegister;

import ballistix.common.entity.EntityMissile;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class RenderMissile extends EntityRenderer<EntityMissile> {

	public RenderMissile(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.15F;
		shadowStrength = 0.75F;
	}

	@Override
	public void render(EntityMissile entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

		int type = entity.missileType;
		World world = entity.level;

		// matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 1, 0), entity.getYRot() + 90.0F, true));
		// matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 0, 1), 90 - entity.getXRot(), true));


		if(type == -1) {
			return;
		}

		matrixStackIn.pushPose();

		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entity.yRot + 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90 - entity.xRot));

		IBakedModel model;
		if (type == 0) {

			model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_MISSILETIER1);
			matrixStackIn.translate(0, 0.82, 0);
			matrixStackIn.scale(1f, 0.75f, 1f);

		} else if (type == 1) {

			model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_MISSILETIER2);
			matrixStackIn.translate(0, 1.05f, 0);
			matrixStackIn.scale(1f, 1f, 1f);

		} else {

			model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_MISSILETIER3);
			matrixStackIn.translate(0, 1.05f, 0);
			matrixStackIn.scale(1f, 1.25f, 1f);

		}

		Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, model, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);

		matrixStackIn.popPose();
	}

	@Override
	public boolean shouldRender(EntityMissile livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityMissile entity) {
		return AtlasTexture.LOCATION_BLOCKS;
	}
}
