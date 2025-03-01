package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerFireControlRadar;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.ScreenComponentBallistixButton;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentRadarGrid;
import ballistix.prefab.screen.ScreenComponentVerticalSlider;
import ballistix.prefab.screen.WrapperFireControlFrequencyManager;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ScreenFireControlRadar extends GenericScreen<ContainerFireControlRadar> {

    public final ScreenComponentVerticalSlider slider;
    public final WrapperFireControlFrequencyManager frequencyWrapper;
    public final ScreenComponentCustomRender trackingLabel;
    public final ScreenComponentRadarGrid radarGrid;

    public ScreenFireControlRadar(ContainerFireControlRadar container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.FIRE_CONTROL_RADAR_USAGE));
        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, BallistixIconTypes.SONAR_PROFILE, () -> {
            List<IReorderingProcessor> info = new ArrayList<>();

            TileFireControlRadar radar = menu.getHostFromIntArray();

            if (radar == null) {
                return info;
            }

            info.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            info.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(Constants.FIRE_CONTROL_RADAR_RANGE, 1).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());


            return info;

        }, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

        frequencyWrapper = new WrapperFireControlFrequencyManager(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2, 0, 0);

        addComponent(slider = new ScreenComponentVerticalSlider(11, 80, 75).setClickConsumer(frequencyWrapper.getSliderClickedConsumer()).setDragConsumer(frequencyWrapper.getSliderDraggedConsumer()));

        slider.setVisible(false);

        addComponent(trackingLabel = new ScreenComponentCustomRender(10, 20, graphics -> {
            TileFireControlRadar tile = menu.getHostFromIntArray();
            if (tile == null) {
                return;
            }
            ITextComponent radar = tile.trackingPos.get().equals(TileFireControlRadar.OUT_OF_REACH) ? BallistixTextUtils.gui("turret.radarnone").withStyle(TextFormatting.GREEN) : new StringTextComponent(new BlockPos((int)tile.trackingPos.get().x,(int)tile.trackingPos.get().y,(int)tile.trackingPos.get().z).toString()).withStyle(TextFormatting.DARK_GRAY);

            int x = (int) (getGuiWidth() + 10);
            int y = (int) (getGuiHeight() + 20);

            ITextComponent label = BallistixTextUtils.gui("radar.tracking").withStyle(TextFormatting.BLACK);

            int width = getFontRenderer().width(label);
            int height = getFontRenderer().lineHeight;

            getFontRenderer().draw(graphics, label, x, y, Color.WHITE.color());

            x += width;

            float scale = 1.0F;

            width = font.width(radar);

            if (width > 100) {
                scale = 100.0F / width;
            }

            float remHeight = (height - height * scale) / 2.0F;

            graphics.pushPose();

            graphics.translate(x, y + remHeight, 0);

            graphics.scale(scale, scale, scale);

            getFontRenderer().draw(graphics, radar, 0, 0, Color.WHITE.color());

            graphics.popPose();


        }));

        addComponent(radarGrid = new ScreenComponentRadarGrid(27, 33, 121, 121));

        addComponent(new ScreenComponentBallistixButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 3 + 2)
                //
                .setOnPress(button -> {

                    TileFireControlRadar tile = menu.getHostFromIntArray();

                    if(tile == null) {
                        return;
                    }

                    tile.usingRedstone.set(!tile.usingRedstone.get());

                    tile.usingRedstone.updateServer();

                })
                //
                .setIcon(BallistixIconTypes.REDSTONE)
                //
                .onTooltip((graphics, button, x, y) -> {

                    TileFireControlRadar tile = menu.getHostFromIntArray();

                    if(tile == null) {
                        return;
                    }

                    List<IReorderingProcessor> info = new ArrayList<>();

                    info.add(BallistixTextUtils.tooltip("radar.redstone").withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());

                    String key = tile.usingRedstone.get() ? "radar.redstone.enabled" : "radar.redstone.disabled";

                    info.add(BallistixTextUtils.tooltip(key).withStyle(TextFormatting.GRAY, TextFormatting.ITALIC).getVisualOrderText());

                    renderTooltip(graphics, info, x, y);

                }));
    }
    
    @Override
    public void tick() {
    	super.tick();
    	frequencyWrapper.tick();
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        playerInvLabel.setVisible(false);
    }
    
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
    	if (frequencyWrapper != null) {
            if (pDelta > 0) {
                // scroll up
                frequencyWrapper.handleMouseScroll(-1);
            } else if (pDelta < 0) {
                // scroll down
                frequencyWrapper.handleMouseScroll(1);
            }
        }
    	return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (slider != null && slider.isVisible()) {
            slider.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (slider != null && slider.isVisible()) {
            slider.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputMappings.Input mouseKey = InputMappings.getKey(pKeyCode, pScanCode);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && frequencyWrapper.addEditBox.isVisible() && frequencyWrapper.addEditBox.isFocused()) {
            return false;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (slider.isVisible()) {
            return slider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

}
