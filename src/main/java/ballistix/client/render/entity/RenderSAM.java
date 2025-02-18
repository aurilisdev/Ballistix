package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.client.ClientRegister;
import ballistix.common.entity.EntitySAM;
import electrodynamics.prefab.utilities.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class RenderSAM extends EntityRenderer<EntitySAM> {

    public RenderSAM(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntitySAM entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        Level world = entity.level();

        if(entity.rotation.length() <= 0) {
            return;
        }

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(MathUtils.rotQuaternionDeg(0, (-entity.getYRot()) - 180, 90 - entity.getXRot()));

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_AAMISSILE);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, model, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);

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
