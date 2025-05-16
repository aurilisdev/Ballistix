package ballistix.prefab.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.tile.TileESMTower;
import ballistix.registers.BallistixItems;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    public void renderBackground(MatrixStack poseStack, int xAxis, int yAxis, int guiWidth, int guiHeight) {

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
        ScreenComponentEditBox.drawExpandedBox(poseStack, x + guiWidth, y + guiHeight, width, height);

        if(pos == null) {
            return;
        }

        RenderingUtils.renderItemScaled(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar), guiWidth + x + 2, guiHeight + y + 4, 1.0F);

        FontRenderer font = screen.getFontRenderer();

        ITextComponent text = new StringTextComponent(pos.toShortString());

        int x = this.x + 20;
        int y = this.y + 8;

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
