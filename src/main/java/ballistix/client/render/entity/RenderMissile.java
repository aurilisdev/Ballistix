package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import ballistix.common.entity.EntityMissile;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMissile extends EntityRenderer<EntityMissile> {

	public RenderMissile(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.15F;
		shadowStrength = 0.75F;
	}

	@Override
	public void render(EntityMissile entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		int type = entity.range;
		Level world = entity.level;
		matrixStackIn.pushPose();
		matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 1, 0), entity.getYRot() + 90.0F, true));
		matrixStackIn.mulPose(new Quaternion(new Vector3f(0, 0, 1), 90 - entity.getXRot(), true));
		switch (type) {
		case 1: {
			BakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILEMEDIUMRANGE);
			matrixStackIn.translate(0.5f, 1.5f, -0.5f);
			matrixStackIn.scale(1.5f, 2.5f, 1.5f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, closerange, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);
			break;
		}
		case 0: {
			BakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILECLOSERANGE);
			matrixStackIn.translate(0.5f, 1.5f, -0.5f);
			matrixStackIn.scale(1.25f, 1.5f, 1.25f);
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, closerange, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);
			break;
		}
		case 2: {
			BakedModel closerange = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILELONGRANGE);
			matrixStackIn.translate(0.5f, 0f, -0.5f);
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
	public boolean shouldRender(EntityMissile livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityMissile entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
