package ballistix.client.render.tile;

import java.util.Random;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.BallistixClientRegister;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import voltaic.client.VoltaicClientRegister;
import voltaic.client.render.AbstractTileRenderer;
import voltaic.prefab.utilities.RenderingUtils;

public class RenderLaserTurret extends AbstractTileRenderer<TileTurretLaser> {

    public RenderLaserTurret(TileEntityRendererDispatcher context) {
        super(context);
    }

    @Override
    public void render(@Nonnull TileTurretLaser tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        IBakedModel model = getModel(BallistixClientRegister.MODEL_LASERTURRET_BALLJOINT);

        Vector3d rotVec = tileEntityIn.turretRotation.getValue();

        double yRot = TileTurretSAM.getXZAngleRadians(rotVec) / Math.PI * 180.0;

        double yAng = Math.asin(rotVec.y);

        float elevRot = (float) (yAng / Math.PI * 180.0F);

        matrixStackIn.pushPose();
        //matrixStackIn.translate(7.75 / 16.0, 11.0 / 16.0, 7.75 / 16.0);
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.mulPose(new Quaternion(0, (float) -yRot, 0, true));
        matrixStackIn.translate(-0.5, -0.5, -0.5);

        // matrixStackIn.mulPose(new Quaternion(0,(float) ((tileEntityIn.savedTickRotation + partial)), 0, true));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5, 0.8125, 0.5);
        matrixStackIn.mulPose(new Quaternion(0, (float) -yRot, elevRot, true));
        matrixStackIn.translate(-0.5, -0.8125, -0.5);

        model = getModel(BallistixClientRegister.MODEL_LASERTURRET_HEAD);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

        matrixStackIn.popPose();

        if(tileEntityIn.hasNoPower.getValue() || !tileEntityIn.firing.getValue()) {
            return;
        }

        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5, 0.8125, 0.5);
        matrixStackIn.mulPose(new Quaternion(0, (float) -yRot, elevRot, true));
        matrixStackIn.translate(-0.5, -0.8125, -0.5);

        //matrixStackIn.translate(0.5, 0.5, 0.5);

        Vector3d start = tileEntityIn.getProjectileLaunchPosition();
        Vector3d end = tileEntityIn.targetPos.getValue();

        double deltaX = end.x - start.x;
        double deltaY = end.y - start.y;
        double deltaZ = end.z - start.z;

        double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        AxisAlignedBB box = new AxisAlignedBB(0.5, 1, 0.46875, mag, 1.0625, 0.53125);

        TextureAtlasSprite sprite = VoltaicClientRegister.whiteSprite();

        RenderingUtils.renderFilledBoxNoOverlay(matrixStackIn, bufferIn.getBuffer(RenderType.solid()), box, 1.0F, 0, 0, 1.0F, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), combinedLightIn, RenderingUtils.ALL_FACES);

        matrixStackIn.popPose();

    }

    @Override
    public boolean shouldRenderOffScreen(TileTurretLaser blockEntity) {
        return true;
    }

}
