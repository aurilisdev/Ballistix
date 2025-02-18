package ballistix.prefab.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ballistix.client.screen.ScreenSearchRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class WrapperSearchFrequencyManager {

    private final ScreenSearchRadar screen;

    public ScreenComponentBallistixButton<?> button;
    private ScreenComponentBallistixButton<?> toggleButton;
    private ScreenComponentBallistixButton<?> add;

    private ScreenComponentBallistixLabel whitelistLabel;

    private ScreenComponentBallistixButton[] deleteButtons = new ScreenComponentBallistixButton[5];
    private ScreenComponentFrequency[] frequencies = new ScreenComponentFrequency[5];

    public ScreenComponentEditBox addEditBox;

    private int topRowIndex = 0;
    private int lastRowCount = 0;

    private static final int BUTTON_COUNT = 5;

    public WrapperSearchFrequencyManager(ScreenSearchRadar screen, int tabX, int tabY, int x, int y) {
        this.screen = screen;

        screen.addComponent(button = (ScreenComponentBallistixButton<?>) new ScreenComponentBallistixButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, tabX, tabY).setOnPress(button -> {
            //
            button.isPressed = !button.isPressed;

            if (button.isPressed) {

                updateVisibility(true);
                screen.slider.setVisible(true);

                screen.detectionsWrapper.updateVisibility(false);
                screen.detectionsSlider.setVisible(false);

            } else {

                updateVisibility(false);
                screen.slider.setVisible(false);

                screen.detectionsWrapper.updateVisibility(true);
                screen.detectionsSlider.setVisible(true);

            }

        }).onTooltip((graphics, but, xAxis, yAxis) -> {
            //
            ScreenComponentBallistixButton<?> button = (ScreenComponentBallistixButton<?>) but;
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("radar.frequencymanager").withStyle(ChatFormatting.DARK_GRAY));
            if (!button.isPressed) {
                tooltips.add(ElectroTextUtils.tooltip("inventoryio.presstoshow").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            } else {
                tooltips.add(ElectroTextUtils.tooltip("inventoryio.presstohide").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }

            graphics.renderComponentTooltip(screen.getFontRenderer(), tooltips, xAxis, yAxis);

        }).setIcon(BallistixIconTypes.FREQUENCY));

        //screen.addComponent(titleLabel = new ScreenComponentSimpleLabel(x + 15, y + 20, 10, Color.TEXT_GRAY, BallistixTextUtils.tooltip("radar.frequencymanager")));

        screen.addComponent(whitelistLabel = new ScreenComponentBallistixLabel(x + 15, y + 30, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("radar.frequencywhitelist.mode")));
        screen.addComponent(toggleButton = new ScreenComponentBallistixButton<>(x + 90, y + 25, 70, 20).setOnPress(button -> {

            TileSearchRadar radar = screen.getMenu().getHostFromIntArray();

            if(radar == null) {
                return;
            }

            radar.usingWhitelist.set(!radar.usingWhitelist.get());
            
            radar.usingWhitelist.updateServer();

        }).setLabel(() -> {

            TileSearchRadar radar = screen.getMenu().getHostFromIntArray();

            if(radar == null) {
                return Component.empty();
            }

            return radar.usingWhitelist.get() ? BallistixTextUtils.gui("radar.frequencywhitelist.enabled") : BallistixTextUtils.gui("radar.frequencywhitelist.disabled");

        }));

        screen.addComponent(add = new ScreenComponentBallistixButton<>(x + 90, y + 50, 70, 20).setOnPress(button -> {

            TileSearchRadar radar = screen.getMenu().getHostFromIntArray();

            if(radar == null) {
                return;
            }

            try {

                int freq = Integer.parseInt(addEditBox.getValue());

                radar.whitelistedFrequencies.get().add(freq);

                radar.whitelistedFrequencies.updateServer();

            } catch (Exception e) {

            }

        }).setLabel(BallistixTextUtils.gui("radar.frequencywhitelist.add")));

        screen.addEditBox(addEditBox = new ScreenComponentEditBox(x + 15, y + 52, 70, 15, screen.getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(9).setFilter(ScreenComponentEditBox.INTEGER));

        int butOffX = 25;
        int butOffY = 80;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            frequencies[i] = new ScreenComponentFrequency(x + butOffX, y + butOffY + 15 * i, 125, 15);
        }

        for (int i = 0; i < BUTTON_COUNT; i++) {

            final int index = i;

            deleteButtons[i] = (ScreenComponentBallistixButton) new ScreenComponentBallistixButton<>(x + butOffX + 125, y + butOffY + 15 * i,15, 15).setOnPress(but -> {

                ScreenComponentFrequency frequency = frequencies[index];

                TileSearchRadar tile = screen.getMenu().getHostFromIntArray();

                if(frequency.getFrequency() == null) {
                    return;
                }

                tile.whitelistedFrequencies.get().remove((Integer) frequency.getFrequency().intValue());

                tile.whitelistedFrequencies.forceDirty();
                
                tile.whitelistedFrequencies.updateServer();

            }).onTooltip((graphics, button, xAxis, yAxis) -> graphics.renderTooltip(screen.getFontRenderer(), BallistixTextUtils.tooltip("radar.frequencymanager.delete"), xAxis, yAxis)).setIcon(BallistixIconTypes.DELETE);
        }

        screen.addComponent(button);
        screen.addComponent(toggleButton);
        screen.addComponent(add);

        //screen.addComponent(titleLabel);
        screen.addComponent(whitelistLabel);

        screen.addComponent(addEditBox);

        for (int i = 0; i < 5; i++) {
            screen.addComponent(frequencies[i]);
        }
        for (int i = 0; i < 5; i++) {
            screen.addComponent(deleteButtons[i]);
        }

        updateVisibility(false);

    }

    public void tick() {
        TileSearchRadar tile = screen.getMenu().getHostFromIntArray();
        if(tile == null) {
            return;
        }

        List<Integer> frequencyList = tile.whitelistedFrequencies.get();

        lastRowCount = frequencyList.size();

        for(int i = 0; i < BUTTON_COUNT; i++) {

            ScreenComponentFrequency button = frequencies[i];

            int index = topRowIndex + i;

            if (index < frequencyList.size()) {
                button.setFrequency(frequencyList.get(index));
            } else {
                button.setFrequency(null);
            }

        }

        ScreenComponentVerticalSlider slider = screen.slider;
        if (lastRowCount > BUTTON_COUNT) {
            slider.updateActive(true);
            if (!slider.isSliderHeld()) {
                int moveRoom = screen.slider.height - 15 -2;

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
                    double moved = topRowIndex / (lastRowCount - (double)BUTTON_COUNT);
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

    public void updateVisibility(boolean show) {

        toggleButton.setVisible(show);
        add.setVisible(show);

        //titleLabel.setVisible(show);
        whitelistLabel.setVisible(show);


        for(ScreenComponentFrequency component : frequencies) {
            component.setVisible(show);
        }

        for(ScreenComponentBallistixButton<?> button : deleteButtons) {
            button.setVisible(show);
        }

        addEditBox.setVisible(show);
        addEditBox.setValue("");

    }

}
