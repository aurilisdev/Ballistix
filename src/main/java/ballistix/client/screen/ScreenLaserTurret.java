package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import ballistix.client.screen.util.ScreenPlayerWhitelistTurret;
import ballistix.common.inventory.container.ContainerLaserTurret;
import ballistix.common.settings.Constants;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.ScreenComponentBallistixButton;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentVerticalSlider;
import ballistix.prefab.screen.WrapperPlayerWhitelist;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.common.tile.machines.quarry.TileQuarry;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public class ScreenLaserTurret extends ScreenPlayerWhitelistTurret<ContainerLaserTurret> {

    private final ScreenComponentCustomRender radarText;
    private final ScreenComponentCustomRender heatBar;
    private final ScreenComponentBallistixLabel statusLabel;
    private final ScreenComponentBallistixLabel tempLabel;

    public ScreenLaserTurret(ContainerLaserTurret vertexconsumer, Inventory inv, Component title) {
        super(vertexconsumer, inv, title);

        inventoryLabelY += 10;
        imageHeight += 10;

        whitelistWrapper = new WrapperPlayerWhitelist(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2, 0, 0);
        addComponent(whitelistSlider = new ScreenComponentVerticalSlider(11, 80, 80).setClickConsumer(whitelistWrapper.getSliderClickedConsumer()).setDragConsumer(whitelistWrapper.getSliderDraggedConsumer()));

        whitelistSlider.setVisible(false);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.LASER_TURRET_USAGEPERTICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, BallistixIconTypes.TARGET_MISSILE, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretLaser turret = menu.getHostFromIntArray();
            if (turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.get(), 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, BallistixIconTypes.TARGET_ENTITY, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretLaser turret = menu.getHostFromIntArray();
            if (turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.entityrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.get() / 4.0, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, 176, 2));

        addComponent(radarText = new ScreenComponentCustomRender(10, 50, graphics -> {
            TileTurretAntimissile turret = menu.getHostFromIntArray();
            if (turret == null) {
                return;
            }
            Component radar = turret.isNotLinked.get() ? BallistixTextUtils.gui("turret.radarnone").withStyle(ChatFormatting.RED) : Component.literal(turret.boundFireControl.get().toShortString()).withStyle(ChatFormatting.DARK_GRAY);

            int x = (int) (getGuiWidth() + 10);
            int y = (int) (getGuiHeight() + 50);

            Component label = BallistixTextUtils.gui("turret.radar").withStyle(ChatFormatting.BLACK);

            int width = getFontRenderer().width(label);
            int height = getFontRenderer().lineHeight;

            graphics.drawString(getFontRenderer(), label, x, y, Color.WHITE.color(), false);

            x += width;

            float scale = 1.0F;

            width = font.width(radar);

            if (width > 100) {
                scale = 100.0F / width;
            }

            float remHeight = (height - height * scale) / 2.0F;

            graphics.pose().pushPose();

            graphics.pose().translate(x, y + remHeight, 0);

            graphics.pose().scale(scale, scale, scale);

            graphics.drawString(getFontRenderer(), radar, 0, 0, Color.WHITE.color(), false);

            graphics.pose().popPose();


        }));

        addComponent(statusLabel = new ScreenComponentBallistixLabel(10, 65, 10, Color.WHITE, () -> {
            TileTurretLaser turret = menu.getHostFromIntArray();
            if (turret == null) {
                return Component.empty();
            }
            Component status = Component.empty();

            if (turret.hasNoPower.get()) {
                status = BallistixTextUtils.gui("turret.statusnopower").withStyle(ChatFormatting.RED);
            } else {

                if (turret.targetingEntity.get()) {
                    if (!turret.hasTarget.get()) {
                        status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(ChatFormatting.GREEN);
                    } else if (!turret.inRange.get()) {
                        status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(ChatFormatting.YELLOW);
                    } else if (turret.overheated.get()) {
                        status = BallistixTextUtils.gui("turret.statusoverheated").withStyle(ChatFormatting.RED);
                    } else {
                        status = BallistixTextUtils.gui("turret.statusgood").withStyle(ChatFormatting.GREEN);
                    }
                } else {
                    if (turret.boundFireControl.get().equals(TileQuarry.OUT_OF_REACH)) {
                        status = BallistixTextUtils.gui("turret.statusunlinked").withStyle(ChatFormatting.RED);
                    } else if (!turret.hasTarget.get()) {
                        status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(ChatFormatting.GREEN);
                    } else if (!turret.inRange.get()) {
                        status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(ChatFormatting.YELLOW);
                    } else if (turret.overheated.get()) {
                        status = BallistixTextUtils.gui("turret.statusoverheated").withStyle(ChatFormatting.RED);
                    } else {
                        status = BallistixTextUtils.gui("turret.statusgood").withStyle(ChatFormatting.GREEN);
                    }
                }
            }


            return BallistixTextUtils.gui("turret.status", status).withStyle(ChatFormatting.BLACK);
        }));

        addComponent(tempLabel = new ScreenComponentBallistixLabel(10, 20, 10, Color.BLACK, () -> {
            TileTurretLaser turret = menu.getHostFromIntArray();
            if (turret == null) {
                return Component.empty();
            }
            return BallistixTextUtils.gui("turret.temperature", ChatFormatter.getChatDisplayShort(turret.heat.get() + 32, DisplayUnit.TEMPERATURE_CELCIUS).withStyle(ChatFormatting.DARK_GRAY));
        }));

        addComponent(heatBar = new ScreenComponentCustomRender(0, 0, graphics -> {

            int width = (int) getGuiWidth();
            int height = (int) getGuiHeight();

            int x = width + 10;
            int y = height + 33;

            TileTurretLaser turret = menu.getHostFromIntArray();
            if (turret == null) {
                return;
            }

            Color start = new Color(0, 255, 0, 255);

            Color end = start;

            int maxX = (int) (155.0F * turret.heat.get() / Constants.LASER_TURRET_MAXHEAT);

            if (turret.heat.get() > Constants.LASER_TURRET_MAXHEAT * 0.8) {
                start = new Color(255, 255, 0, 255);
                end = new Color(255, 0, 0, 255);
            } else if (turret.heat.get() > Constants.LASER_TURRET_MAXHEAT * 0.4) {
                end = new Color(255, 255, 0, 255);
            }

            graphics.fill(x, y, x + 156, y + 12, ScreenComponentCustomRender.TEXT_GRAY.color());

            VertexConsumer vertex = graphics.bufferSource().getBuffer(RenderType.gui());

            Matrix4f matrix4f = graphics.pose().last().pose();
            vertex.vertex(matrix4f, x + 1, y + 1, 0).color(start.color()).endVertex();
            vertex.vertex(matrix4f, x + 1, y + 11, 0).color(start.color()).endVertex();
            vertex.vertex(matrix4f, x + maxX, y + 11, 0).color(end.color()).endVertex();
            vertex.vertex(matrix4f, x + maxX, y + 1, 0).color(end.color()).endVertex();

            //graphics.fillGradient(x + 1, y + 1, x + maxX, y + 11, start.color(), end.color());


        }));

        addComponent(new ScreenComponentBallistixButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, 176, AbstractScreenComponentInfo.SIZE + 2).setOnPress(button -> {
            TileTurretLaser turret = menu.getHostFromIntArray();
            if(turret == null) {
                return;
            }
            turret.onlyTargetPlayers.set(!turret.onlyTargetPlayers.get());
            turret.onlyTargetPlayers.updateServer();
        }).onTooltip((graphics, but, xAxis, yAxis) -> {
            //
            TileTurretLaser turret = menu.getHostFromIntArray();
            if(turret == null) {
                return;
            }
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("turret.targetmode").withStyle(ChatFormatting.DARK_GRAY));
            if (turret.onlyTargetPlayers.get()) {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeplayers").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            } else {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeliving").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }

            graphics.renderComponentTooltip(getFontRenderer(), tooltips, xAxis, yAxis);

        }).setIcon(BallistixIconTypes.TARGET_ONLY_PLAYERS));

    }

    @Override
    public void updateVisibility(boolean show) {
        radarText.setVisible(show);
        heatBar.setVisible(show);
        statusLabel.setVisible(show);
        tempLabel.setVisible(show);
        for (int i = menu.slotCount; i < menu.slots.size(); i++) {

            ((SlotGeneric) menu.slots.get(i)).setActive(show);

        }
    }

}
