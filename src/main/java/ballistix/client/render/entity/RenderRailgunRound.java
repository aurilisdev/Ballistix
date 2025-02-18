package ballistix.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.entity.EntityRailgunRound;
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

public class RenderRailgunRound extends EntityRenderer<EntityRailgunRound> {

    private static final Color COLOR = new Color(85, 85, 85, 255);

    public RenderRailgunRound(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityRailgunRound entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {


        if (entity.rotation.length() <= 0) {
            return;
        }

        TextureAtlasSprite sprite = ClientRegister.CACHED_TEXTUREATLASSPRITES.get(ClientRegister.TEXTURE_WHITE);

        matrixStackIn.pushPose();

        matrixStackIn.mulPose(MathUtils.rotQuaternionDeg(0, (-entity.getYRot()) - 180, 180 - entity.getXRot()));

        RenderingUtils.renderFilledBoxNoOverlay(matrixStackIn, bufferIn.getBuffer(RenderType.solid()), new AABB(0, 0, 0, 1, 0.0625, 0.0625), COLOR.rFloat(), COLOR.gFloat(), COLOR.bFloat(), COLOR.aFloat(), sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLightIn);

        matrixStackIn.popPose();
    }

    @Override
    public boolean shouldRender(EntityRailgunRound livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityRailgunRound entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
