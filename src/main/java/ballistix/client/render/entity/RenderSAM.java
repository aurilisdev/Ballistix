package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.ClientRegister;
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
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;

public class RenderSAM extends EntityRenderer<EntitySAM> {

    public RenderSAM(EntityRendererManager context) {
        super(context);
    }

    @Override
    public void render(EntitySAM entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        World world = entity.level;

        if(entity.rotation.x() == 0 && entity.rotation.y() == 0 && entity.rotation.z() == 0) {
            return;
        }

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(new Quaternion(0, (-entity.yRot) - 180, 90 - entity.xRot, true));

        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_AAMISSILE);

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
