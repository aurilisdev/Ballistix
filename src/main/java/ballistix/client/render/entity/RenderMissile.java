package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

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
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMissile extends EntityRenderer<EntityMissile> {

	public RenderMissile(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.15F;
		shadowStrength = 0.75F;
	}

	@Override
	public void render(EntityMissile entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		int type = entity.range;
		World world = entity.level;
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 1, 0), entity.yRot + 90.0F, true));
		matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 0, 1), 90 - entity.xRot, true));
		switch (type) {
		case 1: {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILEMEDIUMRANGE);
			matrixStackIn.translate(0.5f, 2.6f, 0.5f);
			matrixStackIn.scale(1.5f, 2.5f, 1.5f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, closerange, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);
			break;
		}
		case 0: {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILECLOSERANGE);
			matrixStackIn.translate(0.5f, 1.5f, 0.5f);
			matrixStackIn.scale(1.25f, 1.5f, 1.25f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, closerange, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);
			break;
		}
		case 2: {
			IBakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILELONGRANGE);
			matrixStackIn.translate(0.5f, 4, 0.5f);
			matrixStackIn.scale(2f, 4f, 2f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, closerange, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);
			break;
		}
		default:
			break;
		}
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