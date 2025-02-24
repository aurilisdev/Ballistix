package ballistix.prefab.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ballistix.api.radar.IDetected;
import ballistix.client.screen.ScreenSearchRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

public class WrapperSearchRadarDetections {

    private final ScreenSearchRadar screen;

    private ScreenComponentDetection[] detections = new ScreenComponentDetection[5];

    private int topRowIndex = 0;
    private int lastRowCount = 0;

    private static final int BUTTON_COUNT = 5;

    public WrapperSearchRadarDetections(ScreenSearchRadar screen, int x, int y) {

        this.screen = screen;

        int butOffX = 25;
        int butOffY = 20;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            detections[i] = new ScreenComponentDetection(x + butOffX, y + butOffY + 26 * i, 140, 26);
        }

        for (int i = 0; i < BUTTON_COUNT; i++) {
            screen.addComponent(detections[i]);
        }

    }


    public void tick() {
        TileSearchRadar tile = screen.getMenu().getHostFromIntArray();
        if(tile == null) {
            return;
        }

        List<IDetected.Detected> frequencyList = new ArrayList<>(tile.detections);

        lastRowCount = frequencyList.size();

        for(int i = 0; i < BUTTON_COUNT; i++) {

            ScreenComponentDetection button = detections[i];

            int index = topRowIndex + i;

            if (index < frequencyList.size()) {
                button.setDetection(frequencyList.get(index));
            } else {
                button.setDetection(null);
            }

        }

        ScreenComponentVerticalSlider slider = screen.detectionsSlider;
        if (lastRowCount > BUTTON_COUNT) {
            slider.updateActive(true);
            if (!slider.isSliderHeld()) {
                int moveRoom = screen.detectionsSlider.height - 15 -2;

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
            ScreenComponentVerticalSlider slider = screen.detectionsSlider;
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
                    double moved = topRowIndex / (lastRowCount - (double)BUTTON_COUNT);
                    slider.setSliderYOffset((int) (moveRoom * moved));
                }
            }
        };
    }

    public Consumer<Integer> getSliderDraggedConsumer() {
        return (mouseY) -> {
            ScreenComponentVerticalSlider slider = screen.detectionsSlider;
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

    public void updateVisibility(boolean show) {

        for(ScreenComponentDetection component : detections) {
            component.setVisible(show);
        }

    }


}
