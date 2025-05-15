package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import ballistix.common.entity.EntityBullet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import voltaic.client.VoltaicClientRegister;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;

public class RenderBullet extends EntityRenderer<EntityBullet> {

    private static final Color COLOR = new Color(181, 166, 66, 255);
    private static final AABB BOX = new AABB(0, 0, 0, 0.0625, 0.0625, 0.0625);

    public RenderBullet(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityBullet entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {


        if (entity.getDeltaMovement().length() <= 0) {
            return;
        }

        TextureAtlasSprite sprite = VoltaicClientRegister.whiteSprite();

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entity.getYRot() + 90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90 - entity.getXRot()));

        RenderingUtils.renderFilledBoxNoOverlay(matrixStackIn, bufferIn.getBuffer(RenderType.solid()), BOX, COLOR.rFloat(), COLOR.gFloat(), COLOR.bFloat(), COLOR.aFloat(), sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLightIn, RenderingUtils.ALL_FACES);

        matrixStackIn.popPose();
    }

    @Override
    public boolean shouldRender(EntityBullet livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityBullet entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
