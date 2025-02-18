package ballistix.client.render.tile;

import java.util.Random;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.client.ClientRegister;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import electrodynamics.client.render.tile.AbstractTileRenderer;
import electrodynamics.prefab.utilities.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.Vec3;

public class RenderRailgunTurret extends AbstractTileRenderer<TileTurretRailgun> {

    public RenderRailgunTurret(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull TileTurretRailgun tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        BakedModel model = getModel(ClientRegister.MODEL_RAILGUNTURRET_BALLJOINT);

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

        model = getModel(ClientRegister.MODEL_RAILGUNTURRET_HEAD);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), 0);

        matrixStackIn.popPose();
    }
}
