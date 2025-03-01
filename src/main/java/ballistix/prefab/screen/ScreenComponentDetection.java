package ballistix.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.api.radar.IDetected;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.screen.ITexture;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.utilities.RenderingUtils;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ScreenComponentDetection extends ScreenComponentGeneric {

    private IDetected.Detected detection;

    public ScreenComponentDetection(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(MatrixStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        if (!isVisible()) {
            return;
        }

        GenericScreen<ContainerSearchRadar> screen = (GenericScreen<ContainerSearchRadar>) gui;

        TileSearchRadar tile = screen.getMenu().getHostFromIntArray();

        if (tile == null) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        RenderingUtils.bindTexture(texture.getLocation());
        ScreenComponentEditBox.drawExpandedBox(stack, x + guiWidth, y + guiHeight, width, height);

        if(detection == null) {
            return;
        }

        RenderingUtils.renderItemScaled(detection.getItem(),  guiWidth + x + 2, guiHeight + y + 4, 1.0F);

        FontRenderer font = screen.getFontRenderer();

        IFormattableTextComponent text = new StringTextComponent(new BlockPos((int)detection.getPosition().x,(int)detection.getPosition().y,(int)detection.getPosition().z).toString());

        int xPos = x + 20;
        int yPos = y + 4;

        int maxWidth = width - xPos - 2;

        int width = font.width(text);

        float scale = 1.0F;

        if(width > maxWidth) {
            scale = (float) maxWidth / (float) width;
            yPos += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
        }

        stack.pushPose();

        stack.translate(guiWidth + xPos, guiHeight + yPos, 0);

        stack.scale(scale, scale, 0);

        font.draw(stack, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY.color());

        stack.popPose();

        yPos = y + 15;

        if(detection.showBearing()) {

            double deltaX = tile.getBlockPos().getX() - detection.getPosition().x;
            double deltaZ = tile.getBlockPos().getZ() - detection.getPosition().z;

            double mag = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            deltaX = deltaX / mag;

            deltaZ = deltaZ / mag;

            double angleRads = Math.atan2(deltaZ, deltaX);

            double theta = (angleRads / Math.PI * 180.0) + (angleRads > 0 ? 0.0 : 360.0);

            int thetaMin = (int) (Math.floor(theta) - 1);
            int thetaMax = (int) (Math.floor(theta) + 1);

            text = BallistixTextUtils.gui("radar.bearing", new StringTextComponent("" + thetaMin % 360).withStyle(TextFormatting.WHITE), new StringTextComponent("" + thetaMax % 360).withStyle(TextFormatting.WHITE)).withStyle(TextFormatting.BLACK);

            scale = 1.0F;

            if(width > maxWidth) {
                scale = (float) maxWidth / (float) width;
                yPos += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
            }

            stack.pushPose();

            stack.translate(guiWidth + xPos, guiHeight + yPos, 0);

            stack.scale(scale, scale, 0);

            font.draw(stack, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY.color());

            stack.popPose();


        } else {
        	font.draw(stack, BallistixTextUtils.gui("radar.bearingunknown"), guiWidth + xPos, guiHeight + yPos, Color.BLACK.color());
        }

    }

    public void setDetection(IDetected.Detected detection) {
        this.detection = detection;
    }

}
