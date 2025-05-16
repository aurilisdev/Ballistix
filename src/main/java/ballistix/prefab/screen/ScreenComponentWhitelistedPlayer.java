package ballistix.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.StringTextComponent;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenComponentWhitelistedPlayer extends ScreenComponentGeneric {

    private String playerName = null;

    public ScreenComponentWhitelistedPlayer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if (!isVisible()) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;
        
        RenderingUtils.bindTexture(texture.getLocation());
        ScreenComponentEditBox.drawExpandedBox(poseStack, x + guiWidth, y + guiHeight, width, height);

        if (playerName == null) {
            return;
        }

        gui.getFontRenderer().draw(poseStack, new StringTextComponent(playerName), guiWidth + x + 5, guiHeight + y + 7, Color.WHITE.color());

    }

    public void setFrequency(String name) {
        this.playerName = name;
    }

    public String getName() {
        return playerName;
    }
    
}
