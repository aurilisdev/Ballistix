package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.Ballistix;
import ballistix.client.screen.util.ScreenPlayerWhitelistTurret;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.settings.Constants;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.ScreenComponentBallistixButton;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentVerticalSlider;
import ballistix.prefab.screen.WrapperPlayerWhitelist;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import electrodynamics.prefab.screen.component.types.wrapper.InventoryIOWrapper;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ScreenRailgunTurret extends ScreenPlayerWhitelistTurret<ContainerRailgunTurret> {

    private final ScreenComponentCustomRender radarLabel;
    private final ScreenComponentBallistixLabel statusLabel;
    private final InventoryIOWrapper wrapperInventoryIO;

    public ScreenRailgunTurret(ContainerRailgunTurret container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        inventoryLabelY += 10;
        imageHeight += 10;

        whitelistWrapper = new WrapperPlayerWhitelist(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2, 0, 0);
        addComponent(whitelistSlider = new ScreenComponentVerticalSlider(11, 80, 80).setClickConsumer(whitelistWrapper.getSliderClickedConsumer()).setDragConsumer(whitelistWrapper.getSliderDraggedConsumer()));

        whitelistSlider.setVisible(false);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.SAM_TURRET_USAGEPERTICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, BallistixIconTypes.TARGET_MISSILE, () -> {
            List<IReorderingProcessor> text = new ArrayList<>();
            TileTurretRailgun turret = menu.getHostFromIntArray();
            if(turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.get(), 1).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, 176, 2));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, BallistixIconTypes.TARGET_ENTITY, () -> {
            List<IReorderingProcessor> text = new ArrayList<>();
            TileTurretRailgun turret = menu.getHostFromIntArray();
            if(turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.entityrange").withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.get() / 4.0, 1).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, 176, AbstractScreenComponentInfo.SIZE + 2));

        addComponent(radarLabel = new ScreenComponentCustomRender(10, 50, graphics -> {
            TileTurretAntimissile turret = menu.getHostFromIntArray();
            if(turret == null) {
                return;
            }
            ITextComponent radar = turret.isNotLinked.get() ? BallistixTextUtils.gui("turret.radarnone").withStyle(TextFormatting.RED) : new StringTextComponent(turret.boundFireControl.get().toShortString()).withStyle(TextFormatting.DARK_GRAY);

            int x = (int) (getGuiWidth() + 10);
            int y = (int) (getGuiHeight() + 50);

            ITextComponent label = BallistixTextUtils.gui("turret.radar").withStyle(TextFormatting.BLACK);

            int width = getFontRenderer().width(label);
            int height = getFontRenderer().lineHeight;

            getFontRenderer().draw(graphics, label, x, y, Color.WHITE.color());

            x+= width;

            float scale = 1.0F;

            width = font.width(radar);

            if(width > 100) {
                scale = 100.0F / width;
            }

            float remHeight = (height - height * scale) / 2.0F;

            graphics.pushPose();

            graphics.translate(x, y + remHeight, 0);

            graphics.scale(scale, scale, scale);

            getFontRenderer().draw(graphics, radar, 0, 0, Color.WHITE.color());

            graphics.popPose();


        }));

        addComponent(statusLabel = new ScreenComponentBallistixLabel(10, 65, 10, Color.WHITE, () -> {
            TileTurretRailgun turret = menu.getHostFromIntArray();
            if(turret == null) {
                return ElectroTextUtils.empty();
            }
            ITextComponent status = ElectroTextUtils.empty();

            if(turret.hasNoPower.get()) {
                status = BallistixTextUtils.gui("turret.statusnopower").withStyle(TextFormatting.RED);
            } else {

                if(turret.targetingEntity.get()) {
                    if (!turret.hasTarget.get()) {
                        status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(TextFormatting.GREEN);
                    } else if (!turret.inRange.get()) {
                        status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(TextFormatting.YELLOW);
                    } else if (turret.outOfAmmo.get()) {
                        status = BallistixTextUtils.gui("turret.statusnoammo").withStyle(TextFormatting.RED);
                    } else if (turret.cooldown.get() > 0) {
                        status = BallistixTextUtils.gui("turret.statuscooldown", turret.cooldown.get()).withStyle(TextFormatting.RED);
                    } else {
                        status = BallistixTextUtils.gui("turret.statusgood").withStyle(TextFormatting.GREEN);
                    }
                } else {
                    if (turret.boundFireControl.get().equals(Ballistix.OUT_OF_REACH)) {
                        status = BallistixTextUtils.gui("turret.statusunlinked").withStyle(TextFormatting.RED);
                    } else if (!turret.hasTarget.get()) {
                        status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(TextFormatting.GREEN);
                    } else if (!turret.inRange.get()) {
                        status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(TextFormatting.YELLOW);
                    } else if (turret.outOfAmmo.get()) {
                        status = BallistixTextUtils.gui("turret.statusnoammo").withStyle(TextFormatting.RED);
                    } else if (turret.cooldown.get() > 0) {
                        status = BallistixTextUtils.gui("turret.statuscooldown", turret.cooldown.get()).withStyle(TextFormatting.RED);
                    } else {
                        status = BallistixTextUtils.gui("turret.statusgood").withStyle(TextFormatting.GREEN);
                    }
                }
            }


            return BallistixTextUtils.gui("turret.status", status).withStyle(TextFormatting.BLACK);
        }));

        wrapperInventoryIO = new InventoryIOWrapper(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2, 75, 92, 8, 82).hideAdditional(show -> {
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

        addComponent(new ScreenComponentBallistixButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, 176, AbstractScreenComponentInfo.SIZE * 2 + 2).setOnPress(button -> {
            TileTurretRailgun turret = menu.getHostFromIntArray();
            if(turret == null) {
                return;
            }
            turret.onlyTargetPlayers.set(!turret.onlyTargetPlayers.get());
            turret.onlyTargetPlayers.updateServer();
        }).onTooltip((graphics, but, xAxis, yAxis) -> {
            //
            TileTurretRailgun turret = menu.getHostFromIntArray();
            if(turret == null) {
                return;
            }
            List<ITextComponent> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("turret.targetmode").withStyle(TextFormatting.DARK_GRAY));
            if (turret.onlyTargetPlayers.get()) {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeplayers").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
            } else {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeliving").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
            }

            renderComponentTooltip(graphics, tooltips, xAxis, yAxis);

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
