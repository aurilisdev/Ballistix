package ballistix.prefab.screen;

import ballistix.api.radar.IDetected;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.screen.ITexture;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentGeneric;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ScreenComponentDetection extends ScreenComponentGeneric {

    private IDetected.Detected detection;

    public ScreenComponentDetection(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        if (!isVisible()) {
            return;
        }

        GenericScreen<ContainerSearchRadar> screen = (GenericScreen<ContainerSearchRadar>) gui;

        TileSearchRadar tile = screen.getMenu().getSafeHost();

        if (tile == null) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        ScreenComponentEditBox.drawExpandedBox(graphics, texture.getLocation(), xLocation + guiWidth, yLocation + guiHeight, width, height);

        if(detection == null) {
            return;
        }

        graphics.renderItem(new ItemStack(detection.getItem()), guiWidth + xLocation + 2, guiHeight + yLocation + 4);

        Font font = screen.getFontRenderer();

        Component text = Component.literal(new BlockPos((int)detection.getPosition().x,(int)detection.getPosition().y,(int)detection.getPosition().z).toString());

        int x = xLocation + 20;
        int y = yLocation + 4;

        int maxWidth = width - x - 2;

        int width = font.width(text);

        float scale = 1.0F;

        if(width > maxWidth) {
            scale = (float) maxWidth / (float) width;
            y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
        }

        graphics.pose().pushPose();

        graphics.pose().translate(guiWidth + x, guiHeight + y, 0);

        graphics.pose().scale(scale, scale, 0);

        graphics.drawString(font, text, 0, 0, Color.TEXT_GRAY.color(), false);

        graphics.pose().popPose();

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

            text = BallistixTextUtils.gui("radar.bearing", Component.literal("" + thetaMin % 360).withStyle(ChatFormatting.WHITE), Component.literal("" + thetaMax % 360).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.BLACK);

            scale = 1.0F;

            if(width > maxWidth) {
                scale = (float) maxWidth / (float) width;
                y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
            }

            graphics.pose().pushPose();

            graphics.pose().translate(guiWidth + x, guiHeight + y, 0);

            graphics.pose().scale(scale, scale, 0);

            graphics.drawString(font, text, 0, 0, Color.TEXT_GRAY.color(), false);

            graphics.pose().popPose();


        } else {
            graphics.drawString(font, BallistixTextUtils.gui("radar.bearingunknown"), guiWidth + x, guiHeight + y, Color.BLACK.color(), false);
        }

    }

    public void setDetection(IDetected.Detected detection) {
        this.detection = detection;
    }

}
