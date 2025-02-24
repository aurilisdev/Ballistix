package ballistix.prefab.screen;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.prefab.screen.component.AbstractScreenComponent;
import electrodynamics.prefab.utilities.math.Color;

public class ScreenComponentCustomRender extends AbstractScreenComponent {
	
	public static final Color TEXT_GRAY = new Color(64, 64, 64, 255);
	public static final Color JEI_TEXT_GRAY = new Color(128, 128, 128, 255);

    private final Consumer<PoseStack> graphicsConsumer;

    public ScreenComponentCustomRender(int x, int y, Consumer<PoseStack> graphicsConsumer) {
        super(x, y, 0, 0);
        this.graphicsConsumer = graphicsConsumer;
    }

    @Override
    public void renderBackground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if(!isVisible()){
            return;
        }
        graphicsConsumer.accept(stack);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}
