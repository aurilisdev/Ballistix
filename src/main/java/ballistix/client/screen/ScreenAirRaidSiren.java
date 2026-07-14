package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerAirRaidSiren;
import ballistix.common.tile.TileAirRaidSiren;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentHorizontalSlider;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.utilities.math.Color;

public class ScreenAirRaidSiren extends GenericScreen<ContainerAirRaidSiren> {

    private ScreenComponentHorizontalSlider volumeSlider;
    private ScreenComponentHorizontalSlider pitchSlider;
    private ScreenComponentHorizontalSlider rangeSlider;

    private boolean hasInitHappened = false;

    public ScreenAirRaidSiren(ContainerAirRaidSiren container, Inventory inv, Component title) {
        super(container, inv, title);

        playerInvLabel.setVisible(false);

        // VOLUME

        addComponent(new ScreenComponentSimpleLabel(19, 30, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("airraidsiren.volume")));
        addComponent(new ScreenComponentSimpleLabel(144, 30, 10, Color.TEXT_GRAY, () -> {
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if(siren == null) {
                return TextComponent.EMPTY;
            }
            return new TextComponent("" + siren.volume.getValue());
        }));

        addComponent(volumeSlider = new ScreenComponentHorizontalSlider(20, 40, 138).setClickConsumer(mouseX -> {
            ScreenComponentHorizontalSlider slider = volumeSlider;
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if (slider.isSliderActive() && siren != null) {
                int sliderX = slider.xLocation;
                int sliderWidth = slider.width;
                int mouseWidth = mouseX - sliderX;
                if (mouseWidth >= sliderWidth - 2 - 15) {
                    siren.volume.setValue(TileAirRaidSiren.MAX_VOLUME);
                    slider.setSliderXOffset(sliderWidth - 2 - 15);
                } else if (mouseWidth <= 2) {
                    siren.volume.setValue(TileAirRaidSiren.MIN_VOLUME);
                    slider.setSliderXOffset(0);
                } else {
                    double heightRatio = (double) mouseWidth / (double) sliderWidth;
                    siren.volume.setValue(round(TileAirRaidSiren.MAX_VOLUME * heightRatio, 1));
                    int moveRoom = slider.width - 15 - 2;
                    double moved = siren.volume.getValue() / TileAirRaidSiren.MAX_VOLUME;
                    slider.setSliderXOffset((int) (moveRoom * moved));
                }
            }
        }).setDragConsumer(mouseX -> {
            ScreenComponentHorizontalSlider slider = volumeSlider;
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if (slider.isSliderActive() && siren != null) {
                int sliderX = slider.xLocation;
                int sliderWidth = slider.width;
                if (mouseX <= sliderX + 2) {
                    siren.volume.setValue(TileAirRaidSiren.MIN_VOLUME);
                    slider.setSliderXOffset(0);
                } else if (mouseX >= sliderX + sliderWidth - 2 - 15) {
                    siren.volume.setValue(TileAirRaidSiren.MAX_VOLUME);
                    slider.setSliderXOffset(sliderWidth - 2 - 15);
                } else {
                    int mouseWidth = mouseX - sliderX;
                    slider.setSliderXOffset(mouseWidth);
                    double widthRatio = (double) mouseWidth / (double) sliderWidth;
                    siren.volume.setValue(round(TileAirRaidSiren.MAX_VOLUME * widthRatio, 1));
                }
            }
        }));

        addComponent(new ScreenComponentSimpleLabel(19, 55, 10, Color.TEXT_GRAY, new TextComponent("" + TileAirRaidSiren.MIN_VOLUME)));
        addComponent(new ScreenComponentSimpleLabel(144, 55, 10, Color.TEXT_GRAY, new TextComponent("" + TileAirRaidSiren.MAX_VOLUME)));

        volumeSlider.updateActive(true);

        // PITCH

        addComponent(new ScreenComponentSimpleLabel(19, 70, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("airraidsiren.pitch")));
        addComponent(new ScreenComponentSimpleLabel(144, 70, 10, Color.TEXT_GRAY, () -> {
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if(siren == null) {
                return TextComponent.EMPTY;
            }
            return new TextComponent("" + siren.pitch.getValue());
        }));

        addComponent(pitchSlider = new ScreenComponentHorizontalSlider(20, 80, 138).setClickConsumer(mouseX -> {
            ScreenComponentHorizontalSlider slider = pitchSlider;
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if (slider.isSliderActive() && siren != null) {
                int sliderX = slider.xLocation;
                int sliderWidth = slider.width;
                int mouseWidth = mouseX - sliderX;
                if (mouseWidth >= sliderWidth - 2 - 15) {
                    siren.pitch.setValue(TileAirRaidSiren.MAX_PITCH);
                    slider.setSliderXOffset(sliderWidth - 2 - 15);
                } else if (mouseWidth <= 2) {
                    siren.pitch.setValue(TileAirRaidSiren.MIN_PITCH);
                    slider.setSliderXOffset(0);
                } else {
                    double heightRatio = (double) mouseWidth / (double) sliderWidth;
                    siren.pitch.setValue(round((TileAirRaidSiren.MAX_PITCH - TileAirRaidSiren.MIN_PITCH) * heightRatio + TileAirRaidSiren.MIN_PITCH, 1));
                    int moveRoom = slider.width - 15 - 2;
                    double moved = (siren.pitch.getValue() - TileAirRaidSiren.MIN_PITCH) / (TileAirRaidSiren.MAX_PITCH - TileAirRaidSiren.MIN_PITCH);
                    slider.setSliderXOffset((int) (moveRoom * moved + TileAirRaidSiren.MIN_PITCH));
                }
            }
        }).setDragConsumer(mouseX -> {
            ScreenComponentHorizontalSlider slider = pitchSlider;
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if (slider.isSliderActive() && siren != null) {
                int sliderX = slider.xLocation;
                int sliderWidth = slider.width;
                if (mouseX <= sliderX + 2) {
                    siren.pitch.setValue(TileAirRaidSiren.MIN_PITCH);
                    slider.setSliderXOffset(0);
                } else if (mouseX >= sliderX + sliderWidth - 2 - 15) {
                    siren.pitch.setValue(TileAirRaidSiren.MAX_PITCH);
                    slider.setSliderXOffset(sliderWidth - 2 - 15);
                } else {
                    int mouseWidth = mouseX - sliderX;
                    slider.setSliderXOffset(mouseWidth);
                    double widthRatio = (double) mouseWidth / (double) sliderWidth;
                    siren.pitch.setValue(round((TileAirRaidSiren.MAX_PITCH - TileAirRaidSiren.MIN_PITCH) * widthRatio + TileAirRaidSiren.MIN_PITCH, 1));
                }
            }
        }));

        addComponent(new ScreenComponentSimpleLabel(19, 95, 10, Color.TEXT_GRAY, new TextComponent("" + TileAirRaidSiren.MIN_PITCH)));
        addComponent(new ScreenComponentSimpleLabel(144, 95, 10, Color.TEXT_GRAY, new TextComponent("" + TileAirRaidSiren.MAX_PITCH)));

        pitchSlider.updateActive(true);

        // RANGE

        addComponent(new ScreenComponentSimpleLabel(19, 110, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("airraidsiren.radius")));
        addComponent(new ScreenComponentSimpleLabel(140, 110, 10, Color.TEXT_GRAY, () -> {
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if(siren == null) {
                return TextComponent.EMPTY;
            }
            return new TextComponent("" + siren.range.getValue());
        }));

        addComponent(rangeSlider = new ScreenComponentHorizontalSlider(20, 120, 138).setClickConsumer(mouseX -> {
            ScreenComponentHorizontalSlider slider = rangeSlider;
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if (slider.isSliderActive() && siren != null) {
                int sliderX = slider.xLocation;
                int sliderWidth = slider.width;
                int mouseWidth = mouseX - sliderX;
                if (mouseWidth >= sliderWidth - 2 - 15) {
                    siren.range.setValue(TileAirRaidSiren.MAX_RANGE);
                    slider.setSliderXOffset(sliderWidth - 2 - 15);
                } else if (mouseWidth <= 2) {
                    siren.range.setValue(TileAirRaidSiren.MIN_RANGE);
                    slider.setSliderXOffset(0);
                } else {
                    double heightRatio = (double) mouseWidth / (double) sliderWidth;
                    siren.range.setValue((int) round((TileAirRaidSiren.MAX_RANGE - TileAirRaidSiren.MIN_RANGE) * heightRatio + TileAirRaidSiren.MIN_RANGE, 1));
                    int moveRoom = slider.width - 15 - 2;
                    double moved = (double) (siren.range.getValue() - TileAirRaidSiren.MIN_RANGE) / (double) (TileAirRaidSiren.MAX_RANGE - TileAirRaidSiren.MIN_RANGE);
                    slider.setSliderXOffset((int) (moveRoom * moved + TileAirRaidSiren.MIN_RANGE));
                }
            }
        }).setDragConsumer(mouseX -> {
            ScreenComponentHorizontalSlider slider = rangeSlider;
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if (slider.isSliderActive() && siren != null) {
                int sliderX = slider.xLocation;
                int sliderWidth = slider.width;
                if (mouseX <= sliderX + 2) {
                    siren.range.setValue(TileAirRaidSiren.MIN_RANGE);
                    slider.setSliderXOffset(0);
                } else if (mouseX >= sliderX + sliderWidth - 2 - 15) {
                    siren.range.setValue(TileAirRaidSiren.MAX_RANGE);
                    slider.setSliderXOffset(sliderWidth - 2 - 15);
                } else {
                    int mouseWidth = mouseX - sliderX;
                    slider.setSliderXOffset(mouseWidth);
                    double widthRatio = (double) mouseWidth / (double) sliderWidth;
                    siren.range.setValue((int) round((TileAirRaidSiren.MAX_RANGE - TileAirRaidSiren.MIN_RANGE) * widthRatio + TileAirRaidSiren.MIN_RANGE, 1));
                }
            }
        }));

        addComponent(new ScreenComponentSimpleLabel(19, 135, 10, Color.TEXT_GRAY, new TextComponent("" + TileAirRaidSiren.MIN_RANGE)));
        addComponent(new ScreenComponentSimpleLabel(140, 135, 10, Color.TEXT_GRAY, new TextComponent("" + TileAirRaidSiren.MAX_RANGE)));

        rangeSlider.updateActive(true);

    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if(!hasInitHappened) {
            TileAirRaidSiren siren = getMenu().getSafeHost();
            if(siren == null) {
                return;
            }
            int moveRoom = volumeSlider.width - 15 - 2;
            double ratio = siren.volume.getValue() / TileAirRaidSiren.MAX_VOLUME;
            volumeSlider.setSliderXOffset((int) (moveRoom * ratio));

            moveRoom = pitchSlider.width - 15 - 2;
            ratio = (siren.pitch.getValue() - TileAirRaidSiren.MIN_PITCH) / (TileAirRaidSiren.MAX_PITCH - TileAirRaidSiren.MIN_PITCH);
            pitchSlider.setSliderXOffset((int) (moveRoom * ratio + TileAirRaidSiren.MIN_PITCH));

            moveRoom = rangeSlider.width - 15 - 2;
            ratio = (double) (siren.range.getValue() - TileAirRaidSiren.MIN_RANGE) / (double) (TileAirRaidSiren.MAX_RANGE - TileAirRaidSiren.MIN_RANGE);
            rangeSlider.setSliderXOffset((int) (moveRoom * ratio));


            hasInitHappened = true;
        }

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        TileAirRaidSiren siren = getMenu().getSafeHost();
        double scroll = 0;
        if (scrollY > 0) {
            // scroll up
            scroll = 0.1;
        } else if (scrollY < 0) {
            // scroll down
            scroll = -0.1;
        }
        if (Screen.hasControlDown()) {
            scroll *= 10;
        }
        if (volumeSlider != null && siren != null && isPointInRegion(volumeSlider.xLocation, volumeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), volumeSlider.width, volumeSlider.height)) {
            int moveRoom = volumeSlider.width - 15 -2;
            siren.volume.setValue(round(Mth.clamp(siren.volume.getValue() + scroll, TileAirRaidSiren.MIN_VOLUME, TileAirRaidSiren.MAX_VOLUME), 1));
            volumeSlider.setSliderXOffset((int) (siren.volume.getValue() / TileAirRaidSiren.MAX_VOLUME * moveRoom));
            return true;
        }
        if (pitchSlider != null && siren != null && isPointInRegion(pitchSlider.xLocation, pitchSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), pitchSlider.width, pitchSlider.height)) {
            int moveRoom = pitchSlider.width - 15 -2;
            siren.pitch.setValue(round(Mth.clamp(siren.pitch.getValue() + scroll, TileAirRaidSiren.MIN_PITCH, TileAirRaidSiren.MAX_PITCH), 1));
            pitchSlider.setSliderXOffset((int) ((siren.pitch.getValue() - TileAirRaidSiren.MIN_PITCH) / (TileAirRaidSiren.MAX_PITCH - TileAirRaidSiren.MIN_PITCH) * moveRoom));
            return true;
        }
        if (rangeSlider != null && siren != null && isPointInRegion(rangeSlider.xLocation, rangeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), rangeSlider.width, rangeSlider.height)) {
            scroll *= 10;
            int moveRoom = rangeSlider.width - 15 -2;
            siren.range.setValue((int) round(Mth.clamp(siren.range.getValue() + scroll, TileAirRaidSiren.MIN_RANGE, TileAirRaidSiren.MAX_RANGE), 1));
            rangeSlider.setSliderXOffset((int) ((double) (siren.range.getValue() - TileAirRaidSiren.MIN_RANGE) / (double) (TileAirRaidSiren.MAX_RANGE - TileAirRaidSiren.MIN_RANGE) * moveRoom));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (volumeSlider != null && isPointInRegion(volumeSlider.xLocation, volumeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), volumeSlider.width, volumeSlider.height)) {
            volumeSlider.mouseClicked(mouseX, mouseY, button);
        }
        if (pitchSlider != null && isPointInRegion(pitchSlider.xLocation, pitchSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), pitchSlider.width, pitchSlider.height)) {
            pitchSlider.mouseClicked(mouseX, mouseY, button);
        }
        if (rangeSlider != null && isPointInRegion(rangeSlider.xLocation, rangeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), rangeSlider.width, rangeSlider.height)) {
            rangeSlider.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (volumeSlider != null && isPointInRegion(volumeSlider.xLocation, volumeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), volumeSlider.width, volumeSlider.height)) {
            volumeSlider.mouseReleased(mouseX, mouseY, button);
        }
        if (pitchSlider != null && isPointInRegion(pitchSlider.xLocation, pitchSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), pitchSlider.width, pitchSlider.height)) {
            pitchSlider.mouseReleased(mouseX, mouseY, button);
        }
        if (rangeSlider != null && isPointInRegion(rangeSlider.xLocation, rangeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), rangeSlider.width, rangeSlider.height)) {
            rangeSlider.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isPointInRegion(volumeSlider.xLocation, volumeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), volumeSlider.width, volumeSlider.height)) {
            return volumeSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        if (isPointInRegion(pitchSlider.xLocation, pitchSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), pitchSlider.width, pitchSlider.height)) {
            return pitchSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        if (isPointInRegion(rangeSlider.xLocation, rangeSlider.yLocation, mouseX - getGuiWidth(), mouseY - getGuiHeight(), rangeSlider.width, rangeSlider.height)) {
            return rangeSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private static boolean isPointInRegion(int x, int y, double xAxis, double yAxis, int width, int height) {
        return xAxis >= x && xAxis <= x + width - 1 && yAxis >= y && yAxis <= y + height - 1;
    }

}
