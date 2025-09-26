package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.client.screen.util.ScreenPlayerWhitelistTurret;
import ballistix.common.inventory.container.ContainerCIWSTurret;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.GenericTileTurret;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.WrapperPlayerWhitelistTurret;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.component.button.ScreenComponentButton;
import voltaic.prefab.screen.component.types.ScreenComponentCustomRender;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.ScreenComponentVerticalSlider;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import voltaic.prefab.screen.component.types.wrapper.WrapperInventoryIO;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenCIWSTurret extends ScreenPlayerWhitelistTurret<ContainerCIWSTurret> {

    private final ScreenComponentCustomRender radarLabel;
    private final ScreenComponentSimpleLabel statusLabel;
    private final WrapperInventoryIO wrapperInventoryIO;

    public ScreenCIWSTurret(ContainerCIWSTurret container, Inventory inv, Component title) {
        super(container, inv, title);

        inventoryLabelY += 10;
        imageHeight += 10;

        whitelistWrapper = new WrapperPlayerWhitelistTurret(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2, 0, 0);
        addComponent(whitelistSlider = new ScreenComponentVerticalSlider(11, 80, 75).setClickConsumer(whitelistWrapper.getSliderClickedConsumer()).setDragConsumer(whitelistWrapper.getSliderDraggedConsumer()));

        whitelistSlider.setVisible(false);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(BallistixConstants.SAM_TURRET_USAGEPERTICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, BallistixIconTypes.TARGET_MISSILE, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretCIWS turret = menu.getSafeHost();
            if(turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.getValue(), 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, 176, 2));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, BallistixIconTypes.TARGET_ENTITY, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretCIWS turret = menu.getSafeHost();
            if(turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.entityrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.getValue() / 4.0, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, 176, AbstractScreenComponentInfo.SIZE + 2));

        addComponent(radarLabel = new ScreenComponentCustomRender(10, 50, poseStack -> {
            TileTurretAntimissile turret = menu.getSafeHost();
            if(turret == null) {
                return;
            }
            Component radar = turret.isNotLinked.getValue() ? BallistixTextUtils.gui("turret.radarnone").withStyle(ChatFormatting.RED) : new TextComponent(turret.boundFireControl.getValue().toShortString()).withStyle(ChatFormatting.DARK_GRAY);

            int x = (int) (getGuiWidth() + 10);
            int y = (int) (getGuiHeight() + 50);

            Component label = BallistixTextUtils.gui("turret.radar").withStyle(ChatFormatting.BLACK);

            int width = getFontRenderer().width(label);
            int height = getFontRenderer().lineHeight;

            getFontRenderer().draw(poseStack, label, x, y, Color.WHITE.color());

            x+= width;

            float scale = 1.0F;

            width = font.width(radar);

            if(width > 100) {
                scale = 100.0F / width;
            }

            float remHeight = (height - height * scale) / 2.0F;

            poseStack.pushPose();

            poseStack.translate(x, y + remHeight, 0);

            poseStack.scale(scale, scale, scale);

            getFontRenderer().draw(poseStack, radar, 0, 0, Color.WHITE.color());

            poseStack.popPose();


        }));

        addComponent(statusLabel = new ScreenComponentSimpleLabel(10, 65, 10, Color.WHITE, () -> {
            TileTurretCIWS turret = menu.getSafeHost();
            if(turret == null) {
                return TextComponent.EMPTY;
            }
            Component status = TextComponent.EMPTY;

            if(turret.hasNoPower.getValue()) {
                status = BallistixTextUtils.gui("turret.statusnopower").withStyle(ChatFormatting.RED);
            } else {

                if(turret.targetingEntity.getValue()) {
                    if (!turret.hasTarget.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(ChatFormatting.GREEN);
                    } else if (!turret.inRange.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(ChatFormatting.YELLOW);
                    } else if (turret.outOfAmmo.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusnoammo").withStyle(ChatFormatting.RED);
                    } else {
                        status = BallistixTextUtils.gui("turret.statusgood").withStyle(ChatFormatting.GREEN);
                    }
                } else {
                    if (turret.boundFireControl.getValue().equals(BlockEntityUtils.OUT_OF_REACH)) {
                        status = BallistixTextUtils.gui("turret.statusunlinked").withStyle(ChatFormatting.RED);
                    } else if (!turret.hasTarget.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(ChatFormatting.GREEN);
                    } else if (!turret.inRange.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(ChatFormatting.YELLOW);
                    } else if (turret.outOfAmmo.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusnoammo").withStyle(ChatFormatting.RED);
                    } else {
                        status = BallistixTextUtils.gui("turret.statusgood").withStyle(ChatFormatting.GREEN);
                    }
                }
            }


            return BallistixTextUtils.gui("turret.status", status).withStyle(ChatFormatting.BLACK);
        }));

        wrapperInventoryIO = new WrapperInventoryIO(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2, 75, 92, 8, 82).hideAdditional(show -> {
            radarLabel.setVisible(show);
            statusLabel.setVisible(show);
            whitelistWrapper.updateVisibility(false);
            whitelistWrapper.button.isPressed = false;
            whitelistSlider.setVisible(false);
            if(!show) {
                for (int i = 0; i < menu.slots.size(); i++) {

                    ((SlotGeneric) menu.slots.get(i)).setActive(true);

                }
            }
        });

        addComponent(new ScreenComponentButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, 176, AbstractScreenComponentInfo.SIZE * 2 + 2).setOnPress(button -> {
            TileTurretCIWS turret = menu.getSafeHost();
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
            TileTurretCIWS turret = menu.getSafeHost();
            if(turret == null) {
                return;
            }
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("turret.targetmode").withStyle(ChatFormatting.DARK_GRAY));
            GenericTileTurret.TargetingMode mode = GenericTileTurret.TargetingMode.values()[turret.entityTargetingMode.getValue()];
            if (mode == GenericTileTurret.TargetingMode.ONLY_PLAYERS) {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeplayers").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            } else if (mode == GenericTileTurret.TargetingMode.ALL) {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeliving").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            } else {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodenone").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }

            renderComponentTooltip(poseStack, tooltips, xAxis, yAxis);

        }).setIcon(BallistixIconTypes.TARGET_ONLY_PLAYERS));

    }

    @Override
    public void updateVisibility(boolean show) {
        radarLabel.setVisible(show);
        statusLabel.setVisible(show);
        wrapperInventoryIO.resetSlots();
        if(!show) {
            wrapperInventoryIO.updateVisibility(false);
        }
        wrapperInventoryIO.button.isPressed = false;


        for (int i = 0; i < menu.slots.size(); i++) {

            ((SlotGeneric) menu.slots.get(i)).setActive(show);

        }

        playerInvLabel.setVisible(show);
    }

}
