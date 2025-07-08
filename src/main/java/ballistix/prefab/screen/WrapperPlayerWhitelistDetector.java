package ballistix.prefab.screen;

import ballistix.client.screen.ScreenProximityDetector;
import ballistix.common.tile.TileProximityDetector;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import voltaic.prefab.screen.component.button.ScreenComponentButton;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.ScreenComponentVerticalSlider;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.math.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WrapperPlayerWhitelistDetector {

    private final ScreenProximityDetector screen;

    public ScreenComponentButton<?> button;
    private ScreenComponentButton<?> add;

    private ScreenComponentSimpleLabel whitelistLabel;

    private ScreenComponentButton[] deleteButtons = new ScreenComponentButton[5];
    private ScreenComponentWhitelistedPlayer[] players = new ScreenComponentWhitelistedPlayer[5];

    public ScreenComponentEditBox addEditBox;

    private int topRowIndex = 0;
    private int lastRowCount = 0;

    private static final int BUTTON_COUNT = 5;

    public WrapperPlayerWhitelistDetector(ScreenProximityDetector screen, int tabX, int tabY, int x, int y) {
        this.screen = screen;

        screen.addComponent(button = (ScreenComponentButton<?>) new ScreenComponentButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, tabX, tabY).setOnPress(button -> {
            //
            button.isPressed = !button.isPressed;

            if (button.isPressed) {

                updateVisibility(true);
                screen.whitelistSlider.setVisible(true);

                screen.updateVisibility(false);

            } else {

                updateVisibility(false);
                screen.whitelistSlider.setVisible(false);

                screen.updateVisibility(true);
            }

        }).onTooltip((poseStack, but, xAxis, yAxis) -> {
            //
            ScreenComponentButton<?> button = (ScreenComponentButton<?>) but;
            List<ITextComponent> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("turret.whitelistmanager").withStyle(TextFormatting.DARK_GRAY));
            if (!button.isPressed) {
                tooltips.add(VoltaicTextUtils.tooltip("inventoryio.presstoshow").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
            } else {
                tooltips.add(VoltaicTextUtils.tooltip("inventoryio.presstohide").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
            }

            screen.renderComponentTooltip(poseStack, tooltips, xAxis, yAxis);

        }).setIcon(BallistixIconTypes.PLAYER_WHITELIST));

        screen.addComponent(whitelistLabel = new ScreenComponentSimpleLabel(x + 10, y + 23, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("turret.playerwhitelist.newplayer")));

        screen.addComponent(add = new ScreenComponentButton<>(x + 10, y + 52, 156, 20).setOnPress(button -> {

            TileProximityDetector turret = screen.getMenu().getSafeHost();

            if(turret == null) {
                return;
            }

            turret.whitelistedPlayers.addValue(addEditBox.getValue());

        }).setLabel(BallistixTextUtils.gui("turret.playerwhitelist.add")));

        screen.addEditBox(addEditBox = new ScreenComponentEditBox(x + 10, y + 35, 156, 15, screen.getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(50));

        int butOffX = 25;
        int butOffY = 80;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            players[i] = new ScreenComponentWhitelistedPlayer(x + butOffX, y + butOffY + 15 * i, 125, 15);
        }

        for (int i = 0; i < BUTTON_COUNT; i++) {

            final int index = i;

            deleteButtons[i] = (ScreenComponentButton) new ScreenComponentButton<>(x + butOffX + 125, y + butOffY + 15 * i,15, 15).setOnPress(but -> {

                ScreenComponentWhitelistedPlayer player = players[index];

                TileProximityDetector tile = screen.getMenu().getSafeHost();

                if(player.getName() == null) {
                    return;
                }

                tile.whitelistedPlayers.removeValue(player.getName());

            }).onTooltip((poseStack, button, xAxis, yAxis) -> screen.renderTooltip(poseStack, BallistixTextUtils.tooltip("radar.frequencymanager.delete"), xAxis, yAxis)).setIcon(BallistixIconTypes.DELETE);
        }

        screen.addComponent(button);
        //screen.addComponent(toggleButton);
        screen.addComponent(add);

        //screen.addComponent(titleLabel);
        screen.addComponent(whitelistLabel);

        screen.addComponent(addEditBox);

        for (int i = 0; i < 5; i++) {
            screen.addComponent(players[i]);
        }
        for (int i = 0; i < 5; i++) {
            screen.addComponent(deleteButtons[i]);
        }

        updateVisibility(false);

    }

    public void tick() {
        TileProximityDetector tile = screen.getMenu().getSafeHost();
        if(tile == null) {
            return;
        }

        List<String> frequencyList = tile.whitelistedPlayers.getValue();

        lastRowCount = frequencyList.size();

        for(int i = 0; i < BUTTON_COUNT; i++) {

            ScreenComponentWhitelistedPlayer button = players[i];

            int index = topRowIndex + i;

            if (index < frequencyList.size()) {
                button.setFrequency(frequencyList.get(index));
            } else {
                button.setFrequency(null);
            }

        }

        ScreenComponentVerticalSlider slider = screen.whitelistSlider;
        if (lastRowCount > BUTTON_COUNT) {
            slider.updateActive(true);
            if (!slider.isSliderHeld()) {
                int moveRoom = screen.whitelistSlider.getHeight() - 15 -2;

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
            topRowIndex = MathHelper.clamp(topRowIndex += dir, 0, lastRowIndex - (BUTTON_COUNT - 1));
        } else {
            topRowIndex = 0;
        }
    }

    public Consumer<Integer> getSliderClickedConsumer() {
        return (mouseY) -> {
            ScreenComponentVerticalSlider slider = screen.whitelistSlider;
            if (slider.isSliderActive()) {
                int sliderY = slider.y;
                int sliderHeight = slider.getHeight();
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
                    int moveRoom = slider.getHeight() - 15 - 2;
                    double moved = topRowIndex / (lastRowCount - (double)BUTTON_COUNT);
                    slider.setSliderYOffset((int) (moveRoom * moved));
                }
            }
        };
    }

    public Consumer<Integer> getSliderDraggedConsumer() {
        return (mouseY) -> {
            ScreenComponentVerticalSlider slider = screen.whitelistSlider;
            if (slider.isSliderActive()) {
                int sliderY = slider.y;
                int sliderHeight = slider.getHeight();
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

        //toggleButton.setVisible(show);
        add.setVisible(show);

        //titleLabel.setVisible(show);
        whitelistLabel.setVisible(show);


        for(ScreenComponentWhitelistedPlayer component : players) {
            component.setVisible(show);
        }

        for(ScreenComponentButton<?> button : deleteButtons) {
            button.setVisible(show);
        }

        addEditBox.setVisible(show);
        addEditBox.setValue("");

    }

}
