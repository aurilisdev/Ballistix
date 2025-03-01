package ballistix.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.client.screen.ScreenFireControlRadar;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.util.math.vector.Quaternion;

public class ScreenComponentRadarGrid extends ScreenComponentGeneric {

    private static final Color RADAR_BLACK = new Color(0, 0, 0, 255);
    private static final Color RADAR_GRID_GREEN = new Color(19, 125, 62, 255);
    private static final Color RADAR_PULSE_GREEN = new Color(38, 253, 9, 255);

    public ScreenComponentRadarGrid(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(MatrixStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        int xLoc = x + guiWidth;
        int yLoc = y + guiHeight;

        TileFireControlRadar tile = ((ScreenFireControlRadar)gui).getMenu().getHostFromIntArray();

        if(tile == null) {
            return;
        }

        //BG

        fill(stack, xLoc, yLoc, xLoc + width, yLoc + height, RADAR_BLACK.color());

        // GRID

        int gridWidth = 12;

        for(int i = 1; i < 10; i++) {

        	fill(stack, xLoc + 1, yLoc + gridWidth * i, xLoc + this.width - 1, yLoc + 1 + gridWidth * i, RADAR_GRID_GREEN.color());

        }

        for(int i = 1; i < 10; i++) {

        	fill(stack, xLoc + + gridWidth * i, yLoc + 1, xLoc + 1 + gridWidth * i, yLoc + height - 1, RADAR_GRID_GREEN.color());

        }

        if(!tile.running.get()) {

            //OUTLINE

            fill(stack, xLoc - 3, yLoc - 3, xLoc + 1, yLoc + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

            fill(stack, xLoc + width - 1, yLoc - 3, xLoc + width + 3, yLoc + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

            fill(stack, xLoc, yLoc - 3, xLoc + width, yLoc + 1, ScreenComponentCustomRender.TEXT_GRAY.color());

            fill(stack, xLoc, yLoc + height - 1, xLoc + width, yLoc + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

            return;

        }

        float center = (width - 2) / 2.0F + 1.0F;

        float ratio = (float) (tile.<ComponentTickable>getComponent(IComponentType.Tickable).getTicks() % TileFireControlRadar.PULSE_TIME_TICKS) / (float) TileFireControlRadar.PULSE_TIME_TICKS;

        float theta = ratio * 360.0F;

        // LINE

        float quad = theta % 90.0F;

        if(quad > 45.0F) {
            quad = 90 - quad;
        }

        float angleRad = (float) (quad / 180.0F * Math.PI);

        float leg = (float) Math.abs(Math.tan(angleRad)) * center;

        float hyp = (float) Math.sqrt(leg * leg + center * center);

        float extra = hyp - center;

        stack.pushPose();

        stack.translate(xLoc + center, yLoc + center, 0);

        stack.mulPose(new Quaternion(0, 0, theta, true));

        stack.translate(-xLoc -center, -yLoc -center, 0);

        fill(stack, (int) Math.floor(xLoc + 1 - extra - 2), (int) Math.floor(yLoc + center - 1), (int) Math.ceil(xLoc + center), (int) Math.ceil(yLoc + center + 1), RADAR_PULSE_GREEN.color());

        stack.popPose();

        //OUTLINE

        fill(stack, xLoc - 3, yLoc - 3, xLoc + 1, yLoc + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

        fill(stack, xLoc + width - 1, yLoc - 3, xLoc + width + 3, yLoc + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

        fill(stack, xLoc, yLoc - 3, xLoc + width, yLoc + 1, ScreenComponentCustomRender.TEXT_GRAY.color());

        fill(stack, xLoc, yLoc + height - 1, xLoc + width, yLoc + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

        fill(stack, (int) Math.floor(xLoc + center - 1), (int) Math.floor(yLoc + center - 1), (int) Math.ceil(xLoc + center + 1), (int) Math.ceil(yLoc + center + 1), ScreenComponentCustomRender.JEI_TEXT_GRAY.color());

        //DOT

        if(tile.trackingPos.get().equals(TileFireControlRadar.OUT_OF_REACH)) {
            return;
        }

        float deltaX = (float) ((tile.trackingPos.get().x - tile.getBlockPos().getX()) / (2.0f * Constants.FIRE_CONTROL_RADAR_RANGE)) * width;

        float deltaZ = (float) ((tile.trackingPos.get().z - tile.getBlockPos().getZ()) / (2.0f * Constants.FIRE_CONTROL_RADAR_RANGE)) * width;

        double angleRads = Math.atan2(deltaZ, deltaX);

        float dotTheta = (float) (angleRads / Math.PI * 180.0) + 180.0F;

        int alpha = (int) ((dotTheta + 360.0F - theta) / 360.0F * 255.0F);

        fill(stack, (int) Math.floor(xLoc + center + deltaX - 1), (int) Math.floor(yLoc + center + deltaZ - 1), (int) Math.ceil(xLoc + center + deltaX + 1), (int) Math.ceil(yLoc + center + deltaZ + 1), new Color(255, 0, 0, alpha).color());
    }
}
