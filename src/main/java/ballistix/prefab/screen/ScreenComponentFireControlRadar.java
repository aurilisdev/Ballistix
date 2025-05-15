package ballistix.prefab.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixItems;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import voltaic.api.screen.ITexture;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.ScreenComponentGeneric;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.utilities.RenderingUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenComponentFireControlRadar extends ScreenComponentGeneric {

    private BlockPos pos;

    public ScreenComponentFireControlRadar(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(PoseStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        if (!isVisible()) {
            return;
        }

        GenericScreen<ContainerESMTower> screen = (GenericScreen<ContainerESMTower>) gui;

        TileESMTower tile = screen.getMenu().getSafeHost();

        if (tile == null) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        RenderingUtils.bindTexture(texture.getLocation());
        ScreenComponentEditBox.drawExpandedBox(poseStack, xLocation + guiWidth, yLocation + guiHeight, width, height);

        if(pos == null) {
            return;
        }

        RenderingUtils.renderItemScaled(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar), guiWidth + xLocation + 2, guiHeight + yLocation + 4, 1.0F);

        Font font = screen.getFontRenderer();

        Component text = Component.literal(pos.toShortString());

        int x = xLocation + 20;
        int y = yLocation + 8;

        int maxWidth = width - x - 2;

        int width = font.width(text);

        float scale = 1.0F;

        if(width > maxWidth) {
            scale = (float) maxWidth / (float) width;
            y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
        }

        poseStack.pushPose();

        poseStack.translate(guiWidth + x, guiHeight + y, 0);

        poseStack.scale(scale, scale, 0);

        font.draw(poseStack, text, 0, 0, Color.TEXT_GRAY.color());

        poseStack.popPose();

    }

    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

}
