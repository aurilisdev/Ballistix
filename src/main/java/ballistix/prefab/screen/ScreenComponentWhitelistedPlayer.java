package ballistix.prefab.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.utilities.math.Color;

public class ScreenComponentWhitelistedPlayer extends ScreenComponentGeneric {

    private String playerName = null;

    public ScreenComponentWhitelistedPlayer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if (!isVisible()) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        ScreenComponentEditBox.drawExpandedBox(graphics, texture.getLocation(), xLocation + guiWidth, yLocation + guiHeight, width, height);

        if (playerName == null) {
            return;
        }

        graphics.drawString(gui.getFontRenderer(), Component.literal(playerName), guiWidth + xLocation + 5, guiHeight + yLocation + 5, Color.WHITE.color(), false);

    }

    public void setFrequency(String name) {
        this.playerName = name;
    }

    public String getName() {
        return playerName;
    }
    
}
