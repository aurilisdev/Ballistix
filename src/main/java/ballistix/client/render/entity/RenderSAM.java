package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.BallistixClientRegister;
import ballistix.common.entity.EntitySAM;
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

public class RenderSAM extends EntityRenderer<EntitySAM> {

    public RenderSAM(EntityRendererManager context) {
        super(context);
    }

    @Override
    public void render(EntitySAM entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        World world = entity.level;

        if(entity.getDeltaMovement().length() <= 0) {
            return;
        }

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entity.yRot + 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90 - entity.xRot));

        IBakedModel model;
        if(entity.variant == 0) {
            model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_AAMISSILE);

        } else {
            model = Minecraft.getInstance().getModelManager().getModel(BallistixClientRegister.MODEL_AAMISSILE_MK2);

            matrixStackIn.translate(0, 1.05f, 0);
            matrixStackIn.scale(1f, 1f, 1f);

        }
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, model, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);

        matrixStackIn.popPose();


    }

    @Override
    public boolean shouldRender(EntitySAM livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySAM entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}
