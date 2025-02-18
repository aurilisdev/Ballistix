package ballistix.prefab.screen;

import ballistix.client.screen.ScreenFireControlRadar;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.math.Color;
import electrodynamics.prefab.utilities.math.MathUtils;
import net.minecraft.client.gui.GuiGraphics;

public class ScreenComponentRadarGrid extends ScreenComponentGeneric {

    private static final Color RADAR_BLACK = new Color(0, 0, 0, 255);
    private static final Color RADAR_GRID_GREEN = new Color(19, 125, 62, 255);
    private static final Color RADAR_PULSE_GREEN = new Color(38, 253, 9, 255);

    public ScreenComponentRadarGrid(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        int x = xLocation + guiWidth;
        int y = yLocation + guiHeight;

        TileFireControlRadar tile = ((ScreenFireControlRadar)gui).getMenu().getHostFromIntArray();

        if(tile == null) {
            return;
        }

        //BG

        graphics.fill(x, y, x + width, y + height, RADAR_BLACK.color());

        // GRID

        int gridWidth = 12;

        for(int i = 1; i < 10; i++) {

            graphics.fill(x + 1, y + gridWidth * i, x + this.width - 1, y + 1 + gridWidth * i, RADAR_GRID_GREEN.color());

        }

        for(int i = 1; i < 10; i++) {

            graphics.fill(x + + gridWidth * i, y + 1, x + 1 + gridWidth * i, y + height - 1, RADAR_GRID_GREEN.color());

        }

        if(!tile.running.get()) {

            //OUTLINE

            graphics.fill(x - 3, y - 3, x + 1, y + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

            graphics.fill(x + width - 1, y - 3, x + width + 3, y + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

            graphics.fill(x, y - 3, x + width, y + 1, ScreenComponentCustomRender.TEXT_GRAY.color());

            graphics.fill(x, y + height - 1, x + width, y + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

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

        graphics.pose().pushPose();

        graphics.pose().translate(x + center, y + center, 0);

        graphics.pose().mulPose(MathUtils.rotQuaternionDeg(0, 0, theta));

        graphics.pose().translate(-x -center, -y -center, 0);

        graphics.fill((int) Math.floor(x + 1 - extra - 2), (int) Math.floor(y + center - 1), (int) Math.ceil(x + center), (int) Math.ceil(y + center + 1), RADAR_PULSE_GREEN.color());

        graphics.pose().popPose();

        //OUTLINE

        graphics.fill(x - 3, y - 3, x + 1, y + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

        graphics.fill(x + width - 1, y - 3, x + width + 3, y + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

        graphics.fill(x, y - 3, x + width, y + 1, ScreenComponentCustomRender.TEXT_GRAY.color());

        graphics.fill(x, y + height - 1, x + width, y + height + 3, ScreenComponentCustomRender.TEXT_GRAY.color());

        graphics.fill((int) Math.floor(x + center - 1), (int) Math.floor(y + center - 1), (int) Math.ceil(x + center + 1), (int) Math.ceil(y + center + 1), ScreenComponentCustomRender.JEI_TEXT_GRAY.color());

        //DOT

        if(tile.trackingPos.get().equals(TileFireControlRadar.OUT_OF_REACH)) {
            return;
        }

        float deltaX = (float) ((tile.trackingPos.get().x - tile.getBlockPos().getX()) / (2.0f * Constants.FIRE_CONTROL_RADAR_RANGE)) * width;

        float deltaZ = (float) ((tile.trackingPos.get().z - tile.getBlockPos().getZ()) / (2.0f * Constants.FIRE_CONTROL_RADAR_RANGE)) * width;

        double angleRads = Math.atan2(deltaZ, deltaX);

        float dotTheta = (float) (angleRads / Math.PI * 180.0) + 180.0F;

        int alpha = (int) ((dotTheta + 360.0F - theta) / 360.0F * 255.0F);

        graphics.fill((int) Math.floor(x + center + deltaX - 1), (int) Math.floor(y + center + deltaZ - 1), (int) Math.ceil(x + center + deltaX + 1), (int) Math.ceil(y + center + deltaZ + 1), new Color(255, 0, 0, alpha).color());
    }
}
