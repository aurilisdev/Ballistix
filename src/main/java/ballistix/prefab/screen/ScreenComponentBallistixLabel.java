package ballistix.prefab.screen;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.prefab.screen.component.AbstractScreenComponent;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ScreenComponentBallistixLabel extends AbstractScreenComponent {

	private Supplier<Component> text = () -> TextComponent.EMPTY;
	public Color color = Color.WHITE;

	public ScreenComponentBallistixLabel(int x, int y, int height, Color color, Component text) {
		this(x, y, height, color, () -> text);
	}

	public ScreenComponentBallistixLabel(int x, int y, int height, Color color, Supplier<Component> text) {
		super(x, y, 0, height);
		this.text = text;
		this.color = color;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return isPointInRegion(xLocation, yLocation, mouseX - gui.getGuiWidth(), mouseY - gui.getGuiHeight(), gui.getFontRenderer().width(text.get()), height);
	}

	@Override
	public void renderForeground(PoseStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		if (isVisible()) {
			gui.getFontRenderer().draw(stack, text.get(), xLocation, yLocation, color.color());
		}
	}

}
