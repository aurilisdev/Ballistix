package ballistix.prefab.screen;

import java.util.List;
import java.util.function.Consumer;

import ballistix.client.screen.ScreenESMTower;
import ballistix.common.tile.TileESMTower;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class WrapperESMTowerDetections {

	private final ScreenESMTower screen;

	private ScreenComponentFireControlRadar[] frequencies = new ScreenComponentFireControlRadar[5];

	private int topRowIndex = 0;
	private int lastRowCount = 0;

	private static final int BUTTON_COUNT = 5;

	public WrapperESMTowerDetections(ScreenESMTower screen, int x, int y) {
		this.screen = screen;

		screen.addComponent(new ScreenComponentBallistixLabel(x + 25, y + 30, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("esmtower.detectedfirecontrolradars")));

		int butOffX = 25;
		int butOffY = 40;

		for (int i = 0; i < BUTTON_COUNT; i++) {
			frequencies[i] = new ScreenComponentFireControlRadar(x + butOffX, y + butOffY + 24 * i, 135, 24);
		}

		for (int i = 0; i < BUTTON_COUNT; i++) {
			screen.addComponent(frequencies[i]);
		}

	}

	public void tick() {
		TileESMTower tile = screen.getMenu().getHostFromIntArray();
		if (tile == null) {
			return;
		}

		List<BlockPos> frequencyList = tile.fireControlRadars.get();

		lastRowCount = frequencyList.size();

		for (int i = 0; i < BUTTON_COUNT; i++) {

			ScreenComponentFireControlRadar button = frequencies[i];

			int index = topRowIndex + i;

			if (index < frequencyList.size()) {
				button.setBlockPos(frequencyList.get(index));
			} else {
				button.setBlockPos(null);
			}

		}

		ScreenComponentVerticalSlider slider = screen.slider;
		if (lastRowCount > BUTTON_COUNT) {
			slider.updateActive(true);
			if (!slider.isSliderHeld()) {
				int moveRoom = screen.slider.height - 15 - 2;

				// int moveRoom = 102 - 2;
				double moved = topRowIndex / (lastRowCount - (double) BUTTON_COUNT);
				slider.setSliderYOffset((int) (moveRoom * moved));
			}
		} else {
			slider.updateActive(false);
			slider.setSliderYOffset(0);
			topRowIndex = 0;
		}

	}

	// pos for down, neg for up
	public void handleMouseScroll(int dir) {
		if (Screen.hasControlDown()) {
			dir *= 4;
		}
		int lastRowIndex = lastRowCount - 1;
		if (lastRowCount > BUTTON_COUNT) {
			// check in case something borked
			if (topRowIndex >= lastRowCount) {
				topRowIndex = lastRowIndex - (BUTTON_COUNT - 1);
			}
			topRowIndex = Mth.clamp(topRowIndex += dir, 0, lastRowIndex - (BUTTON_COUNT - 1));
		} else {
			topRowIndex = 0;
		}
	}

	public Consumer<Integer> getSliderClickedConsumer() {
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = screen.slider;
			if (slider.isSliderActive()) {
				int sliderY = slider.yLocation;
				int sliderHeight = slider.height;
				int mouseHeight = mouseY - sliderY;
				if (mouseHeight >= sliderHeight - 2 - 15) {
					topRowIndex = lastRowCount - BUTTON_COUNT;
					slider.setSliderYOffset(sliderHeight - 2 - 15);
				} else if (mouseHeight <= 2) {
					topRowIndex = 0;
					slider.setSliderYOffset(0);
				} else {
					double heightRatio = (double) mouseHeight / (double) sliderHeight;
					topRowIndex = (int) Math.round((lastRowCount - BUTTON_COUNT) * heightRatio);
					int moveRoom = slider.height - 15 - 2;
					double moved = topRowIndex / (lastRowCount - (double) BUTTON_COUNT);
					slider.setSliderYOffset((int) (moveRoom * moved));
				}
			}
		};
	}

	public Consumer<Integer> getSliderDraggedConsumer() {
		return (mouseY) -> {
			ScreenComponentVerticalSlider slider = screen.slider;
			if (slider.isSliderActive()) {
				int sliderY = slider.yLocation;
				int sliderHeight = slider.height;
				if (mouseY <= sliderY + 2) {
					topRowIndex = 0;
					slider.setSliderYOffset(0);
				} else if (mouseY >= sliderY + sliderHeight - 2 - 15) {
					topRowIndex = lastRowCount - BUTTON_COUNT;
					slider.setSliderYOffset(sliderHeight - 2 - 15);
				} else {
					int mouseHeight = mouseY - sliderY;
					slider.setSliderYOffset(mouseHeight);
					double heightRatio = (double) mouseHeight / (double) sliderHeight;
					topRowIndex = (int) Math.round((lastRowCount - BUTTON_COUNT) * heightRatio);
				}
			}
		};
	}

}
