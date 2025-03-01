package ballistix.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixBlocks;
import electrodynamics.api.screen.ITexture;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ScreenComponentFireControlRadar extends ScreenComponentGeneric {

    private BlockPos pos;

    public ScreenComponentFireControlRadar(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderBackground(MatrixStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

        if (!isVisible()) {
            return;
        }

        GenericScreen<ContainerESMTower> screen = (GenericScreen<ContainerESMTower>) gui;

        TileESMTower tile = screen.getMenu().getHostFromIntArray();

        if (tile == null) {
            return;
        }

        ITexture texture = RadarTextures.FREQUENCY;

        RenderingUtils.bindTexture(texture.getLocation());
        ScreenComponentEditBox.drawExpandedBox(stack, x + guiWidth, y + guiHeight, width, height);

        if(pos == null) {
            return;
        }

        RenderingUtils.renderItemScaled(BallistixBlocks.blockFireControlRadar.asItem(), guiWidth + x + 2, guiHeight + y + 4, 1.0F);

        FontRenderer font = screen.getFontRenderer();

        IFormattableTextComponent text = new StringTextComponent(pos.toShortString());

        int xLoc = x + 20;
        int yLoc = y + 8;

        int maxWidth = width - xLoc - 2;

        int width = font.width(text);

        float scale = 1.0F;

        if(width > maxWidth) {
            scale = (float) maxWidth / (float) width;
            yLoc += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
        }

        stack.pushPose();

        stack.translate(guiWidth + xLoc, guiHeight + yLoc, 0);

        stack.scale(scale, scale, 0);

        font.draw(stack, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY.color());

        stack.popPose();

    }

    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

}
