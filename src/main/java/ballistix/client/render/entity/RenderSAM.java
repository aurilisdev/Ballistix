package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.client.BallistixClientRegister;
import ballistix.common.entity.EntitySAM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import voltaic.prefab.utilities.math.MathUtils;

public class RenderSAM extends EntityRenderer<EntitySAM> {

    public RenderSAM(EntityRendererProvider.Context context) {
	super(context);
    }

    @Override
    public void render(EntitySAM entity, float entityYaw, float partialTicks, PoseStack matrixStackIn,
	    MultiBufferSource bufferIn, int packedLightIn) {

	Level world = entity.level();

	if (entity.getDeltaMovement().length() <= 0) {
	    return;
	}

	matrixStackIn.pushPose();

	float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

	matrixStackIn.mulPose(MathUtils.rotVectorQuaternionDeg(entityYaw + 90.0F, MathUtils.YP));

	matrixStackIn.mulPose(MathUtils.rotVectorQuaternionDeg(90.0F - pitch, MathUtils.ZP));

	BakedModel model;
	if (entity.variant == 0) {
	    model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_AAMISSILE);

	} else {
	    model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_AAMISSILE_MK2);

	    matrixStackIn.translate(0, 1.05f, 0);
	    matrixStackIn.scale(1f, 1f, 1f);

	}
	Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, model,
		Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn,
		bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);

	matrixStackIn.popPose();

    }

    @Override
    public boolean shouldRender(EntitySAM livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
	return true;
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySAM entity) {
	return InventoryMenu.BLOCK_ATLAS;
    }
}
