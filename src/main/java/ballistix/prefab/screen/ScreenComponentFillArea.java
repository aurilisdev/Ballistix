package ballistix.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.utilities.math.Color;

public class ScreenComponentFillArea extends ScreenComponentGeneric {

    private final Color fill;
    private final Color outline;

    public ScreenComponentFillArea(int x, int y, int width, int height, Color fill, Color outline) {
        super(x, y, width, height);
        this.fill = fill;
        this.outline = outline;
    }

    public ScreenComponentFillArea(int x, int y, int width, int height, Color fill) {
        this(x, y, width, height, fill, fill);
    }

    @Override
    public void renderBackground(MatrixStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if(!isVisible()) {
            return;
        }
        fill(stack, x + guiWidth, y + guiHeight, x + guiWidth + width, y + guiHeight + height, fill.color());
        renderOutline(stack, x + guiWidth - 1, y + guiHeight - 1, width + 1, height + 1, outline.color());
    }
    
    private void renderOutline(MatrixStack stack, int pX, int pY, int pWidth, int pHeight, int pColor) {
        fill(stack, pX, pY, pX + pWidth, pY + 1, pColor);
        fill(stack, pX, pY + pHeight - 1, pX + pWidth, pY + pHeight, pColor);
        fill(stack, pX, pY + 1, pX + 1, pY + pHeight - 1, pColor);
        fill(stack, pX + pWidth - 1, pY + 1, pX + pWidth, pY + pHeight - 1, pColor);
     }
}
