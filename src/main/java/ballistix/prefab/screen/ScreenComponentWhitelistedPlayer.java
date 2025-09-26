package ballistix.prefab.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
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
    public void renderBackground(PoseStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if (!isVisible()) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;
        
        RenderingUtils.bindTexture(texture.getLocation());
        ScreenComponentEditBox.drawExpandedBox(poseStack, xLocation + guiWidth, yLocation + guiHeight, width, height);

        if (playerName == null) {
            return;
        }

        gui.getFontRenderer().draw(poseStack, Component.literal(playerName), guiWidth + xLocation + 5, guiHeight + yLocation + 5, Color.WHITE.color());

    }

    public void setFrequency(String name) {
        this.playerName = name;
    }

    public String getName() {
        return playerName;
    }
    
}
