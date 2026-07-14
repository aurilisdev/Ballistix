package ballistix.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.api.radar.IDetected;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenComponentDetection extends ScreenComponentGeneric {

    private IDetected.Detected detection;

    public ScreenComponentDetection(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        if (!isVisible()) {
            return;
        }

        GenericScreen<ContainerSearchRadar> screen = (GenericScreen<ContainerSearchRadar>) gui;

        TileSearchRadar tile = screen.getMenu().getSafeHost();

        if (tile == null) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        RenderingUtils.bindTexture(texture.getLocation());
        ScreenComponentEditBox.drawExpandedBox(poseStack, x + guiWidth, y + guiHeight, width, height);

        if(detection == null) {
            return;
        }

        RenderingUtils.renderItemScaled(detection.getItem(),  guiWidth + x + 2, guiHeight + y + 4, 1.0F);

        FontRenderer font = screen.getFontRenderer();

        ITextComponent text = new StringTextComponent(new BlockPos((int)detection.getPosition().x,(int)detection.getPosition().y,(int)detection.getPosition().z).toString());

        int x = this.x + 20;
        int y = this.y + 4;

        int maxWidth = width - x - 2;

        int width = font.width(text);

        float scale = 1.0F;

        if(width > maxWidth) {
            scale = (float) maxWidth / (float) width;
            y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
        }

        poseStack.pushPose();

        poseStack.translate(guiWidth + x, guiHeight + y, 0);

        poseStack.scale(scale, scale, 0);

        font.draw(poseStack, text, 0, 0, Color.TEXT_GRAY.color());

        poseStack.popPose();

        y = this.y + 15;

        if(detection.showBearing()) {

            double deltaX = tile.getBlockPos().getX() - detection.getPosition().x;
            double deltaZ = tile.getBlockPos().getZ() - detection.getPosition().z;

            double mag = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            deltaX = deltaX / mag;

            deltaZ = deltaZ / mag;

            double angleRads = Math.atan2(deltaZ, deltaX);

            double theta = angleRads / Math.PI * 180.0 + (angleRads > 0 ? 0.0 : 360.0);

            int thetaMin = (int) (Math.floor(theta) - 1);
            int thetaMax = (int) (Math.floor(theta) + 1);

            text = BallistixTextUtils.gui("radar.bearing", new StringTextComponent("" + thetaMin % 360).withStyle(TextFormatting.WHITE), new StringTextComponent("" + thetaMax % 360).withStyle(TextFormatting.WHITE)).withStyle(TextFormatting.BLACK);

            scale = 1.0F;

            if(width > maxWidth) {
                scale = (float) maxWidth / (float) width;
                y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
            }

            poseStack.pushPose();

            poseStack.translate(guiWidth + x, guiHeight + y, 0);

            poseStack.scale(scale, scale, 0);

            font.draw(poseStack, text, 0, 0, Color.TEXT_GRAY.color());

            poseStack.popPose();


        } else {
        	font.draw(poseStack, BallistixTextUtils.gui("radar.bearingunknown"), guiWidth + x, guiHeight + y, Color.BLACK.color());
        }

    }

    public void setDetection(IDetected.Detected detection) {
        this.detection = detection;
    }

}
