package ballistix.client.render.tile;

import java.util.Random;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.BallistixClientRegister;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import voltaic.client.render.AbstractTileRenderer;

public class RenderCIWSTurret extends AbstractTileRenderer<TileTurretCIWS> {
	
    public RenderCIWSTurret(TileEntityRendererDispatcher context) {
        super(context);
    }

    @Override
    public void render(@Nonnull TileTurretCIWS tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        IBakedModel model = getModel(BallistixClientRegister.MODEL_CIWSTURRET_BALLJOINT);

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

        model = getModel(BallistixClientRegister.MODEL_CIWSTURRET_HEAD);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

        matrixStackIn.popPose();

        matrixStackIn.pushPose();

        float rotation = tileEntityIn.firing.getValue() ? System.currentTimeMillis() % 100L / 100.0F * 360.0F : 0.0F;

        //matrixStackIn.translate(0.5, 0.84375, 0.5);

        //matrixStackIn.mulPose(MathUtils.rotQuaternionDeg(rotation, 0, 0));

        //matrixStackIn.translate(-0.5, -0.84375, -0.5);

        matrixStackIn.translate(0.5, 0.84375, 0.5);
        matrixStackIn.mulPose(new Quaternion(0, (float) -yRot, elevRot, true));
        matrixStackIn.mulPose(new Quaternion(rotation, 0, 0, true));
        matrixStackIn.translate(-0.5, -0.84375, -0.5);

        model = getModel(BallistixClientRegister.MODEL_CIWSTURRET_BARREL);



        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

        matrixStackIn.popPose();

    }

}
