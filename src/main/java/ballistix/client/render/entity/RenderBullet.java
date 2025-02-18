package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.entity.EntityBullet;
import electrodynamics.client.ClientRegister;
import electrodynamics.prefab.utilities.RenderingUtils;
import electrodynamics.prefab.utilities.math.Color;
import electrodynamics.prefab.utilities.math.MathUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;

public class RenderBullet extends EntityRenderer<EntityBullet> {

    private static final Color COLOR = new Color(181, 166, 66, 255);

    public RenderBullet(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityBullet entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        if (entity.rotation.length() <= 0) {
            return;
        }

        TextureAtlasSprite sprite = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_WHITE);

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(MathUtils.rotQuaternionDeg(0, (-entity.getYRot()) - 180, 90 - entity.getXRot()));

        RenderingUtils.renderFilledBoxNoOverlay(matrixStackIn, bufferIn.getBuffer(RenderType.solid()), new AABB(0, 0, 0, 0.0625, 0.0625, 0.0625), COLOR.rFloat(), COLOR.gFloat(), COLOR.bFloat(), COLOR.aFloat(), sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLightIn);

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
