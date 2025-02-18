package ballistix.prefab.screen;

import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixBlocks;
import electrodynamics.api.screen.ITexture;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ScreenComponentFireControlRadar extends ScreenComponentGeneric {

    private BlockPos pos;

    public ScreenComponentFireControlRadar(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        if (!isVisible()) {
            return;
        }

        GenericScreen<ContainerESMTower> screen = (GenericScreen<ContainerESMTower>) gui;

        TileESMTower tile = screen.getMenu().getHostFromIntArray();

        if (tile == null) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        ScreenComponentEditBox.drawExpandedBox(graphics, texture.getLocation(), xLocation + guiWidth, yLocation + guiHeight, width, height);

        if(pos == null) {
            return;
        }

        graphics.renderItem(new ItemStack(BallistixBlocks.blockFireControlRadar), guiWidth + xLocation + 2, guiHeight + yLocation + 4);

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

        graphics.pose().pushPose();

        graphics.pose().translate(guiWidth + x, guiHeight + y, 0);

        graphics.pose().scale(scale, scale, 0);

        graphics.drawString(font, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY.color(), false);

        graphics.pose().popPose();

    }

    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

}
