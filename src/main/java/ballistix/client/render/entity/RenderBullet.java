package ballistix.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.entity.EntityBullet;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3f;
import voltaic.client.VoltaicClientRegister;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;

public class RenderBullet extends EntityRenderer<EntityBullet> {

    private static final Color COLOR = new Color(181, 166, 66, 255);
    private static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 0.0625, 0.0625, 0.0625);

    public RenderBullet(EntityRendererManager context) {
        super(context);
    }

    @Override
    public void render(EntityBullet entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {


        if (entity.getDeltaMovement().length() <= 0) {
            return;
        }

        TextureAtlasSprite sprite = VoltaicClientRegister.whiteSprite();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entity.yRot + 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90 - entity.xRot));

        RenderingUtils.renderFilledBoxNoOverlay(matrixStackIn, bufferIn.getBuffer(RenderType.solid()), BOX, COLOR.rFloat(), COLOR.gFloat(), COLOR.bFloat(), COLOR.aFloat(), sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLightIn, RenderingUtils.ALL_FACES);

        matrixStackIn.popPose();
    }

    @Override
    public boolean shouldRender(EntityBullet livingEntity, ClippingHelper camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityBullet entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}
