package ballistix.prefab.screen;

import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.prefab.screen.component.AbstractScreenComponent;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.util.text.IFormattableTextComponent;

public class ScreenComponentBallistixLabel extends AbstractScreenComponent {

	private Supplier<IFormattableTextComponent> text = () -> ElectroTextUtils.empty();
	public Color color = Color.WHITE;

	public ScreenComponentBallistixLabel(int x, int y, int height, Color color, IFormattableTextComponent text) {
		this(x, y, height, color, () -> text);
	}

	public ScreenComponentBallistixLabel(int x, int y, int height, Color color, Supplier<IFormattableTextComponent> text) {
		super(x, y, 0, height);
		this.text = text;
		this.color = color;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return isPointInRegion(x, y, mouseX - gui.getGuiWidth(), mouseY - gui.getGuiHeight(), gui.getFontRenderer().width(text.get()), height);
	}

	@Override
	public void renderForeground(MatrixStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		if (isVisible()) {
			gui.getFontRenderer().draw(stack, text.get(), x, y, color.color());
		}
	}

}
