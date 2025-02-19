package ballistix.prefab.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.api.screen.ITexture;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.utilities.RenderingUtils;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.network.chat.Component;

public class ScreenComponentFrequency extends ScreenComponentGeneric {

    private Integer frequency;

    public ScreenComponentFrequency(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if (!isVisible()) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        RenderingUtils.bindTexture(texture.getLocation());
        ScreenComponentEditBox.drawExpandedBox(stack, xLocation + guiWidth, yLocation + guiHeight, width, height);

        if (frequency == null) {
            return;
        }

        gui.getFontRenderer().draw(stack, Component.literal(frequency + ""), guiWidth + xLocation + 5, guiHeight + yLocation + 5, Color.WHITE.color());

    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getFrequency() {
        return frequency;
    }

}
