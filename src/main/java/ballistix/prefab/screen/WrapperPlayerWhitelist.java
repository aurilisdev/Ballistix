package ballistix.prefab.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ballistix.client.screen.util.ScreenPlayerWhitelistTurret;
import ballistix.common.tile.turret.GenericTileTurret;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class WrapperPlayerWhitelist {

    private final ScreenPlayerWhitelistTurret<?> screen;

    public ScreenComponentBallistixButton<?> button;
    private ScreenComponentBallistixButton<?> add;

    private ScreenComponentBallistixLabel whitelistLabel;

    private ScreenComponentBallistixButton[] deleteButtons = new ScreenComponentBallistixButton[4];
    private ScreenComponentWhitelistedPlayer[] players = new ScreenComponentWhitelistedPlayer[4];

    public ScreenComponentEditBox addEditBox;

    private int topRowIndex = 0;
    private int lastRowCount = 0;

    private static final int BUTTON_COUNT = 4;

    public WrapperPlayerWhitelist(ScreenPlayerWhitelistTurret<?> screen, int tabX, int tabY, int x, int y) {
        this.screen = screen;

        screen.addComponent(button = (ScreenComponentBallistixButton<?>) new ScreenComponentBallistixButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, tabX, tabY).setOnPress(button -> {
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

        }).onTooltip((graphics, but, xAxis, yAxis) -> {
            //
            ScreenComponentBallistixButton<?> button = (ScreenComponentBallistixButton<?>) but;
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("turret.whitelistmanager").withStyle(ChatFormatting.DARK_GRAY));
            if (!button.isPressed) {
                tooltips.add(ElectroTextUtils.tooltip("inventoryio.presstoshow").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            } else {
                tooltips.add(ElectroTextUtils.tooltip("inventoryio.presstohide").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }

            graphics.renderComponentTooltip(screen.getFontRenderer(), tooltips, xAxis, yAxis);

        }).setIcon(BallistixIconTypes.PLAYER_WHITELIST));

        screen.addComponent(whitelistLabel = new ScreenComponentBallistixLabel(x + 10, y + 23, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("turret.playerwhitelist.newplayer")));

        screen.addComponent(add = new ScreenComponentBallistixButton<>(x + 10, y + 52, 156, 20).setOnPress(button -> {

            GenericTileTurret turret = screen.getMenu().getHostFromIntArray();

            if(turret == null) {
                return;
            }

            turret.whitelistedPlayers.get().add(addEditBox.getValue());

            turret.whitelistedPlayers.updateServer();

        }).setLabel(BallistixTextUtils.gui("turret.playerwhitelist.add")));

        screen.addEditBox(addEditBox = new ScreenComponentEditBox(x + 10, y + 35, 156, 15, screen.getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(50));

        int butOffX = 25;
        int butOffY = 80;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            players[i] = new ScreenComponentWhitelistedPlayer(x + butOffX, y + butOffY + 20 * i, 125, 20);
        }

        for (int i = 0; i < BUTTON_COUNT; i++) {

            final int index = i;

            deleteButtons[i] = (ScreenComponentBallistixButton) new ScreenComponentBallistixButton<>(x + butOffX + 125, y + butOffY + 20 * i, 15, 20).setOnPress(but -> {

                ScreenComponentWhitelistedPlayer player = players[index];

                GenericTileTurret tile = screen.getMenu().getHostFromIntArray();

                if(player.getName() == null) {
                    return;
                }

                tile.whitelistedPlayers.get().remove(player.getName());

                tile.whitelistedPlayers.forceDirty();
                
                tile.whitelistedPlayers.updateServer();

            }).onTooltip((graphics, button, xAxis, yAxis) -> graphics.renderTooltip(screen.getFontRenderer(), BallistixTextUtils.tooltip("radar.frequencymanager.delete"), xAxis, yAxis)).setIcon(BallistixIconTypes.DELETE);
        }

        screen.addComponent(button);
        //screen.addComponent(toggleButton);
        screen.addComponent(add);

        //screen.addComponent(titleLabel);
        screen.addComponent(whitelistLabel);

        screen.addComponent(addEditBox);

        for (int i = 0; i < BUTTON_COUNT; i++) {
            screen.addComponent(players[i]);
        }
        for (int i = 0; i < BUTTON_COUNT; i++) {
            screen.addComponent(deleteButtons[i]);
        }

        updateVisibility(false);

    }

    public void tick() {
        GenericTileTurret tile = screen.getMenu().getHostFromIntArray();
        if(tile == null) {
            return;
        }

        List<String> frequencyList = tile.whitelistedPlayers.get();

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
                int moveRoom = screen.whitelistSlider.height - 15 -2;

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
            ScreenComponentVerticalSlider slider = screen.whitelistSlider;
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
            ScreenComponentVerticalSlider slider = screen.whitelistSlider;
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

        //toggleButton.setVisible(show);
        add.setVisible(show);

        //titleLabel.setVisible(show);
        whitelistLabel.setVisible(show);


        for(ScreenComponentWhitelistedPlayer component : players) {
            component.setVisible(show);
        }

        for(ScreenComponentBallistixButton<?> button : deleteButtons) {
            button.setVisible(show);
        }

        addEditBox.setVisible(show);
        addEditBox.setValue("");

    }

}
