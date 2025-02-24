package ballistix.prefab.screen;

import com.mojang.blaze3d.vertex.PoseStack;

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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ScreenComponentDetection extends ScreenComponentGeneric {

    private IDetected.Detected detection;

    public ScreenComponentDetection(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

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
        ScreenComponentEditBox.drawExpandedBox(stack, xLocation + guiWidth, yLocation + guiHeight, width, height);

        if(detection == null) {
            return;
        }

        RenderingUtils.renderItemScaled(detection.getItem(),  guiWidth + xLocation + 2, guiHeight + yLocation + 4, 1.0F);

        Font font = screen.getFontRenderer();

        Component text = new TextComponent(new BlockPos((int)detection.getPosition().x,(int)detection.getPosition().y,(int)detection.getPosition().z).toString());

        int x = xLocation + 20;
        int y = yLocation + 4;

        int maxWidth = width - x - 2;

        int width = font.width(text);

        float scale = 1.0F;

        if(width > maxWidth) {
            scale = (float) maxWidth / (float) width;
            y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
        }

        stack.pushPose();

        stack.translate(guiWidth + x, guiHeight + y, 0);

        stack.scale(scale, scale, 0);

        font.draw(stack, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY.color());

        stack.popPose();

        y = yLocation + 15;

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

            text = BallistixTextUtils.gui("radar.bearing", new TextComponent("" + thetaMin % 360).withStyle(ChatFormatting.WHITE), new TextComponent("" + thetaMax % 360).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.BLACK);

            scale = 1.0F;

            if(width > maxWidth) {
                scale = (float) maxWidth / (float) width;
                y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
            }

            stack.pushPose();

            stack.translate(guiWidth + x, guiHeight + y, 0);

            stack.scale(scale, scale, 0);

            font.draw(stack, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY.color());

            stack.popPose();


        } else {
        	font.draw(stack, BallistixTextUtils.gui("radar.bearingunknown"), guiWidth + x, guiHeight + y, Color.BLACK.color());
        }

    }

    public void setDetection(IDetected.Detected detection) {
        this.detection = detection;
    }

}
