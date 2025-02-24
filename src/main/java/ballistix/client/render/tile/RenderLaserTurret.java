package ballistix.client.render.tile;

import java.util.Random;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.client.ClientRegister;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.utilities.RenderingUtils;
import electrodynamics.prefab.utilities.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderLaserTurret extends AbstractTileRenderer<TileTurretLaser> {

    public RenderLaserTurret(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull TileTurretLaser tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        BakedModel model = getModel(ClientRegister.MODEL_LASERTURRET_BALLJOINT);

        Vec3 rotVec = tileEntityIn.turretRotation.get();

        double yRot = TileTurretSAM.getXZAngleRadians(rotVec) / Math.PI * 180.0;

        double yAng = Math.asin(rotVec.y);

        float elevRot = (float) (yAng / Math.PI * 180.0F);

        matrixStackIn.pushPose();
        //matrixStackIn.translate(7.75 / 16.0, 11.0 / 16.0, 7.75 / 16.0);
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(MathUtils.rotQuaternionDeg(0, (float) -yRot, 0));
        matrixStackIn.translate(-0.5, -0.5, -0.5);

        // matrixStackIn.mulPose(new Quaternion(0,(float) ((tileEntityIn.savedTickRotation + partial)), 0, true));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5, 0.8125, 0.5);
        matrixStackIn.mulPose(MathUtils.rotQuaternionDeg(0, (float) -yRot, elevRot));
        matrixStackIn.translate(-0.5, -0.8125, -0.5);

        model = getModel(ClientRegister.MODEL_LASERTURRET_HEAD);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

        matrixStackIn.popPose();

        if(!tileEntityIn.firing.get() || tileEntityIn.hasNoPower.get()) {
            return;
        }

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5, 0.8125, 0.5);
        matrixStackIn.mulPose(MathUtils.rotQuaternionDeg(0, (float) -yRot, elevRot));
        matrixStackIn.translate(-0.5, -0.8125, -0.5);

        //matrixStackIn.translate(0.5, 0.5, 0.5);

        Vec3 start = tileEntityIn.getProjectileLaunchPosition();
        Vec3 end = tileEntityIn.targetPos.get();

        double deltaX = end.x - start.x;
        double deltaY = end.y - start.y;
        double deltaZ = end.z - start.z;

        double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        AABB box = new AABB(0.5, 1, 0.46875, mag, 1.0625, 0.53125);

        TextureAtlasSprite sprite = electrodynamics.client.ClientRegister.CACHED_TEXTUREATLASSPRITES.get(electrodynamics.client.ClientRegister.TEXTURE_WHITE);

        RenderingUtils.renderFilledBoxNoOverlay(matrixStackIn, bufferIn.getBuffer(RenderType.solid()), box, 1.0F, 0, 0, 1.0F, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), combinedLightIn);

        matrixStackIn.popPose();

    }

    @Override
    public boolean shouldRenderOffScreen(TileTurretLaser blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(TileTurretLaser blockEntity, Vec3 cameraPos) {
        return true;
    }

}
