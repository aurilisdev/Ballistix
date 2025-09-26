package ballistix.client.screen;

import ballistix.client.event.HandlerDetectorLines;
import ballistix.common.inventory.container.ContainerProximityDetector;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.TileProximityDetector;
import ballistix.common.tile.turret.GenericTileTurret;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.WrapperPlayerWhitelistDetector;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.button.ScreenComponentButton;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.screen.component.types.ScreenComponentFillArea;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.ScreenComponentVerticalSlider;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.utilities.math.Color;
import voltaic.prefab.utilities.VoltaicTextUtils;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class ScreenProximityDetector extends GenericScreen<ContainerProximityDetector> {

    public final ScreenComponentVerticalSlider whitelistSlider;
    public final WrapperPlayerWhitelistDetector whitelistWrapper;
    private final ScreenComponentButton<?> toggleButton;
    private final ScreenComponentSimpleLabel whitelistLabel;
    private final ScreenComponentEditBox xMin;
    private final ScreenComponentEditBox xMax;
    private final ScreenComponentEditBox yMin;
    private final ScreenComponentEditBox yMax;
    private final ScreenComponentEditBox zMin;
    private final ScreenComponentEditBox zMax;
    private final ScreenComponentSimpleLabel coordFieldLabel;
    private final ScreenComponentSimpleLabel xCoordLabel;
    private final ScreenComponentSimpleLabel yCoordLabel;
    private final ScreenComponentSimpleLabel zCoordLabel;
    private final ScreenComponentSimpleLabel coordMinLabel;
    private final ScreenComponentSimpleLabel coordMaxLabel;
    private final ScreenComponentFillArea background;
    private final ScreenComponentSimpleLabel toggleLabel;
    private final ScreenComponentButton<?> toggleLines;

    private boolean needsUpdate = true;


    public ScreenProximityDetector(ContainerProximityDetector container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        inventoryLabelY += 10;
        imageHeight += 10;

        addComponent(background = new ScreenComponentFillArea(10, 20, 157, 95, new Color(130, 130, 130, 255)));

        whitelistWrapper = new WrapperPlayerWhitelistDetector(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2, 0, 0);
        addComponent(whitelistSlider = new ScreenComponentVerticalSlider(11, 80, 75).setClickConsumer(whitelistWrapper.getSliderClickedConsumer()).setDragConsumer(whitelistWrapper.getSliderDraggedConsumer()));

        whitelistSlider.setVisible(false);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(BallistixConstants.SAM_TURRET_USAGEPERTICK * 20));

        addComponent(new ScreenComponentButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2).setOnPress(button -> {
            TileProximityDetector turret = menu.getSafeHost();
            if(turret == null) {
                return;
            }
            int mode = turret.entityTargetingMode.getValue();
            mode++;
            if(mode >= GenericTileTurret.TargetingMode.values().length) {
                mode = 0;
            }
            turret.entityTargetingMode.setValue(mode);
        }).onTooltip((poseStack, but, xAxis, yAxis) -> {
            //
            TileProximityDetector turret = menu.getSafeHost();
            if(turret == null) {
                return;
            }
            List<ITextComponent> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("turret.targetmode").withStyle(TextFormatting.DARK_GRAY));
            GenericTileTurret.TargetingMode mode = GenericTileTurret.TargetingMode.values()[turret.entityTargetingMode.getValue()];
            if (mode == GenericTileTurret.TargetingMode.ONLY_PLAYERS) {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeplayers").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
            } else if (mode == GenericTileTurret.TargetingMode.ALL) {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeliving").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
            } else {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodenone").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
            }

            renderComponentTooltip(poseStack, tooltips, xAxis, yAxis);

        }).setIcon(BallistixIconTypes.TARGET_ONLY_PLAYERS));

        for (int i = 0; i < menu.slots.size(); i++) {

            ((SlotGeneric) menu.slots.get(i)).setActive(false);

        }

        addComponent(coordFieldLabel = new ScreenComponentSimpleLabel(15, 25, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("proximitydetector.detectionrange")));
        addComponent(coordMinLabel = new ScreenComponentSimpleLabel(80, 40, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("proximitydetector.min")));
        addComponent(coordMaxLabel = new ScreenComponentSimpleLabel(130, 40, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("proximitydetector.max")));

        addEditBox(xMin = new ScreenComponentEditBox(80, 50, 20, 16, this.getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(1).setFilter(ScreenComponentEditBox.POSITIVE_INTEGER).setResponder(this::setXMin));
        addEditBox(xMax = new ScreenComponentEditBox(130, 50, 20, 16, this.getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(1).setFilter(ScreenComponentEditBox.POSITIVE_INTEGER).setResponder(this::setXMax));
        addEditBox(yMin = new ScreenComponentEditBox(80, 70, 20, 16, this.getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(1).setFilter(ScreenComponentEditBox.POSITIVE_INTEGER).setResponder(this::setYMin));
        addEditBox(yMax = new ScreenComponentEditBox(130, 70, 20, 16, this.getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(1).setFilter(ScreenComponentEditBox.POSITIVE_INTEGER).setResponder(this::setYMax));
        addEditBox(zMin = new ScreenComponentEditBox(80, 90, 20, 16, this.getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(1).setFilter(ScreenComponentEditBox.POSITIVE_INTEGER).setResponder(this::setZMin));
        addEditBox(zMax = new ScreenComponentEditBox(130, 90, 20, 16, this.getFontRenderer()).setTextColor(Color.WHITE).setTextColorUneditable(Color.WHITE).setMaxLength(1).setFilter(ScreenComponentEditBox.POSITIVE_INTEGER).setResponder(this::setZMax));

        addComponent(xCoordLabel = new ScreenComponentSimpleLabel(20, 55, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("proximitydetector.xcoord")));
        addComponent(yCoordLabel = new ScreenComponentSimpleLabel(20, 75, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("proximitydetector.ycoord")));
        addComponent(zCoordLabel = new ScreenComponentSimpleLabel(20, 95, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("proximitydetector.zcoord")));

        addComponent(whitelistLabel = new ScreenComponentSimpleLabel(13, 126, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("radar.frequencywhitelist.mode")));
        addComponent(toggleButton = new ScreenComponentButton<>(92, 120, 70, 20).setOnPress(button -> {

            TileProximityDetector detector = getMenu().getSafeHost();

            if(detector == null) {
                return;
            }

            detector.usingWhitelist.setValue(!detector.usingWhitelist.getValue());

        }).setLabel(() -> {

            TileProximityDetector detector = getMenu().getSafeHost();

            if(detector == null) {
                return VoltaicTextUtils.empty();
            }

            return detector.usingWhitelist.getValue() ? BallistixTextUtils.gui("radar.frequencywhitelist.enabled") : BallistixTextUtils.gui("radar.frequencywhitelist.disabled");

        }));

        addComponent(toggleLabel = new ScreenComponentSimpleLabel(13, 151, 10, Color.TEXT_GRAY, BallistixTextUtils.gui("proximitydetector.detectionfield")));
        addComponent(toggleLines = new ScreenComponentButton<>(92, 145, 70, 20).setLabel(() -> {
            TileProximityDetector detector = menu.getSafeHost();
            if (detector != null) {
                return HandlerDetectorLines.containsLines(detector.getBlockPos()) ? BallistixTextUtils.gui("proximitydetector.hidefield") : BallistixTextUtils.gui("proximitydetector.showfield");
            }
            return VoltaicTextUtils.empty();
        }).setOnPress(button ->  {
            TileProximityDetector detector = menu.getSafeHost();
            if (detector != null) {
                BlockPos pos = detector.getBlockPos();
                if (HandlerDetectorLines.containsLines(pos)) {
                    HandlerDetectorLines.removeLines(pos);
                } else {
                	
                	BlockPos inverted = new BlockPos(-detector.minCorner.getValue().getX(), -detector.minCorner.getValue().getY(), -detector.minCorner.getValue().getZ());
                	
                    AxisAlignedBB box = new AxisAlignedBB(detector.getBlockPos().offset(inverted), detector.getBlockPos().offset(detector.maxCorner.getValue()).offset(1, 1, 1));
                    HandlerDetectorLines.addLines(detector.getBlockPos(), box);
                }
            }
        }));

    }

    public void updateVisibility(boolean show) {
        toggleButton.setVisible(show);
        whitelistLabel.setVisible(show);
        xMin.setVisible(show);
        xMax.setVisible(show);
        yMin.setVisible(show);
        yMax.setVisible(show);
        zMin.setVisible(show);
        zMax.setVisible(show);
        xCoordLabel.setVisible(show);
        yCoordLabel.setVisible(show);
        zCoordLabel.setVisible(show);
        coordMaxLabel.setVisible(show);
        coordMinLabel.setVisible(show);
        coordFieldLabel.setVisible(show);
        background.setVisible(show);
        toggleLines.setVisible(show);
        toggleLabel.setVisible(show);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        playerInvLabel.setVisible(false);
    }

    @Override
    public void tick() {
        super.tick();
        whitelistWrapper.tick();
        TileProximityDetector detector = menu.getSafeHost();
        if (detector != null && HandlerDetectorLines.containsLines(detector.getBlockPos())) {
            HandlerDetectorLines.removeLines(detector.getBlockPos());
            
            BlockPos inverted = new BlockPos(-detector.minCorner.getValue().getX(), -detector.minCorner.getValue().getY(), -detector.minCorner.getValue().getZ());
            
            AxisAlignedBB box = new AxisAlignedBB(detector.getBlockPos().offset(inverted), detector.getBlockPos().offset(detector.maxCorner.getValue()).offset(1, 1, 1));
            HandlerDetectorLines.addLines(detector.getBlockPos(), box);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (whitelistWrapper != null) {
            if (scrollY > 0) {
                // scroll up
                whitelistWrapper.handleMouseScroll(-1);
            } else if (scrollY < 0) {
                // scroll down
                whitelistWrapper.handleMouseScroll(1);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (whitelistSlider != null && whitelistSlider.isVisible()) {
            whitelistSlider.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (whitelistSlider != null && whitelistSlider.isVisible()) {
            whitelistSlider.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (whitelistSlider.isVisible()) {
            return whitelistSlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
    	InputMappings.Input mouseKey = InputMappings.getKey(pKeyCode, pScanCode);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && whitelistWrapper.addEditBox.isVisible() && whitelistWrapper.addEditBox.isFocused()) {
            return false;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void setXMin(String val) {
        this.xMin.setFocus(true);
        this.xMax.setFocus(false);
        this.yMin.setFocus(false);
        this.yMax.setFocus(false);
        this.zMin.setFocus(false);
        this.zMax.setFocus(false);
        this.handleXMin(val);
    }

    private void setXMax(String val) {
        this.xMin.setFocus(false);
        this.xMax.setFocus(true);
        this.yMin.setFocus(false);
        this.yMax.setFocus(false);
        this.zMin.setFocus(false);
        this.zMax.setFocus(false);
        this.handleXMax(val);
    }

    private void setYMin(String val) {
        this.xMin.setFocus(false);
        this.xMax.setFocus(false);
        this.yMin.setFocus(true);
        this.yMax.setFocus(false);
        this.zMin.setFocus(false);
        this.zMax.setFocus(false);
        this.handleYMin(val);
    }

    private void setYMax(String val) {
        this.xMin.setFocus(false);
        this.xMax.setFocus(false);
        this.yMin.setFocus(false);
        this.yMax.setFocus(true);
        this.zMin.setFocus(false);
        this.zMax.setFocus(false);
        this.handleYMax(val);
    }

    private void setZMin(String val) {
        this.xMin.setFocus(false);
        this.xMax.setFocus(false);
        this.yMin.setFocus(false);
        this.yMax.setFocus(false);
        this.zMin.setFocus(true);
        this.zMax.setFocus(false);
        this.handleZMin(val);
    }

    private void setZMax(String val) {
        this.xMin.setFocus(false);
        this.xMax.setFocus(false);
        this.yMin.setFocus(false);
        this.yMax.setFocus(false);
        this.zMin.setFocus(false);
        this.zMax.setFocus(true);
        this.handleZMax(val);
    }

    private void handleXMin(String val) {
        if(val.isEmpty()) {
            return;
        }
        Integer xCoord = 0;

        try {
            xCoord = Integer.parseInt(val);
        } catch (Exception ex) {

        }

        TileProximityDetector detector = menu.getSafeHost();
        if (detector == null) {
            return;
        }

        detector.minCorner.setValue(new BlockPos(xCoord, detector.minCorner.getValue().getY(), detector.minCorner.getValue().getZ()));
    }

    private void handleXMax(String val) {
        if(val.isEmpty()) {
            return;
        }
        Integer xCoord = 0;

        try {
            xCoord = Integer.parseInt(val);
        } catch (Exception ex) {

        }

        TileProximityDetector detector = menu.getSafeHost();
        if (detector == null) {
            return;
        }

        detector.maxCorner.setValue(new BlockPos(xCoord, detector.maxCorner.getValue().getY(), detector.maxCorner.getValue().getZ()));
    }

    private void handleYMin(String val) {
        if(val.isEmpty()) {
            return;
        }
        Integer yCoord = 0;

        try {
            yCoord = Integer.parseInt(val);
        } catch (Exception ex) {

        }

        TileProximityDetector detector = menu.getSafeHost();
        if (detector == null) {
            return;
        }

        detector.minCorner.setValue(new BlockPos(detector.minCorner.getValue().getX(), yCoord, detector.minCorner.getValue().getZ()));
    }

    private void handleYMax(String val) {
        if(val.isEmpty()) {
            return;
        }
        Integer yCoord = 0;

        try {
            yCoord = Integer.parseInt(val);
        } catch (Exception ex) {

        }

        TileProximityDetector detector = menu.getSafeHost();
        if (detector == null) {
            return;
        }

        detector.maxCorner.setValue(new BlockPos(detector.maxCorner.getValue().getX(), yCoord, detector.maxCorner.getValue().getZ()));
    }

    private void handleZMin(String val) {
        if(val.isEmpty()) {
            return;
        }
        Integer zCoord = 0;

        try {
            zCoord = Integer.parseInt(val);
        } catch (Exception ex) {

        }

        TileProximityDetector detector = menu.getSafeHost();
        if (detector == null) {
            return;
        }

        detector.minCorner.setValue(new BlockPos(detector.minCorner.getValue().getX(), detector.minCorner.getValue().getY(), zCoord));
    }

    private void handleZMax(String val) {
        if(val.isEmpty()) {
            return;
        }
        Integer zCoord = 0;

        try {
            zCoord = Integer.parseInt(val);
        } catch (Exception ex) {

        }

        TileProximityDetector detector = menu.getSafeHost();
        if (detector == null) {
            return;
        }

        detector.maxCorner.setValue(new BlockPos(detector.maxCorner.getValue().getX(), detector.maxCorner.getValue().getY(), zCoord));
    }

    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        if (this.needsUpdate) {
            this.needsUpdate = false;
            TileProximityDetector detector = menu.getSafeHost();
            if (detector != null) {
                this.xMin.setValue("" + detector.minCorner.getValue().getX());
                this.xMax.setValue("" + detector.maxCorner.getValue().getX());
                this.yMin.setValue("" + detector.minCorner.getValue().getY());
                this.yMax.setValue("" + detector.maxCorner.getValue().getY());
                this.zMin.setValue("" + detector.minCorner.getValue().getZ());
                this.zMax.setValue("" + detector.maxCorner.getValue().getZ());
            }
        }

    }

}
