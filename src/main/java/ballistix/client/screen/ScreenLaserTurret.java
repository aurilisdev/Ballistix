package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import ballistix.client.screen.util.ScreenPlayerWhitelistTurret;
import ballistix.common.inventory.container.ContainerLaserTurret;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.WrapperPlayerWhitelist;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.component.button.ScreenComponentButton;
import voltaic.prefab.screen.component.types.ScreenComponentCustomRender;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.ScreenComponentVerticalSlider;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenLaserTurret extends ScreenPlayerWhitelistTurret<ContainerLaserTurret> {

    private final ScreenComponentCustomRender radarText;
    private final ScreenComponentCustomRender heatBar;
    private final ScreenComponentSimpleLabel statusLabel;
    private final ScreenComponentSimpleLabel tempLabel;

    public ScreenLaserTurret(ContainerLaserTurret vertexconsumer, Inventory inv, Component title) {
        super(vertexconsumer, inv, title);

        inventoryLabelY += 10;
        imageHeight += 10;

        whitelistWrapper = new WrapperPlayerWhitelist(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2, 0, 0);
        addComponent(whitelistSlider = new ScreenComponentVerticalSlider(11, 80, 75).setClickConsumer(whitelistWrapper.getSliderClickedConsumer()).setDragConsumer(whitelistWrapper.getSliderDraggedConsumer()));

        whitelistSlider.setVisible(false);

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(BallistixConstants.LASER_TURRET_USAGEPERTICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, BallistixIconTypes.TARGET_MISSILE, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretLaser turret = menu.getSafeHost();
            if (turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.getValue(), 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, BallistixIconTypes.TARGET_ENTITY, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretLaser turret = menu.getSafeHost();
            if (turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.entityrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.getValue() / 4.0, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, 176, 2));

        addComponent(radarText = new ScreenComponentCustomRender(10, 50, poseStack -> {
            TileTurretAntimissile turret = menu.getSafeHost();
            if (turret == null) {
                return;
            }
            Component radar = turret.isNotLinked.getValue() ? BallistixTextUtils.gui("turret.radarnone").withStyle(ChatFormatting.RED) : Component.literal(turret.boundFireControl.getValue().toShortString()).withStyle(ChatFormatting.DARK_GRAY);

            int x = (int) (getGuiWidth() + 10);
            int y = (int) (getGuiHeight() + 50);

            Component label = BallistixTextUtils.gui("turret.radar").withStyle(ChatFormatting.BLACK);

            int width = getFontRenderer().width(label);
            int height = getFontRenderer().lineHeight;

            getFontRenderer().draw(poseStack, label, x, y, Color.WHITE.color());

            x += width;

            float scale = 1.0F;

            width = font.width(radar);

            if (width > 100) {
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
            TileTurretLaser turret = menu.getSafeHost();
            if (turret == null) {
                return Component.empty();
            }
            Component status = Component.empty();

            if (turret.hasNoPower.getValue()) {
                status = BallistixTextUtils.gui("turret.statusnopower").withStyle(ChatFormatting.RED);
            } else {

                if (turret.targetingEntity.getValue()) {
                    if (!turret.hasTarget.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(ChatFormatting.GREEN);
                    } else if (!turret.inRange.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(ChatFormatting.YELLOW);
                    } else if (turret.overheated.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusoverheated").withStyle(ChatFormatting.RED);
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
                    } else if (turret.overheated.getValue()) {
                        status = BallistixTextUtils.gui("turret.statusoverheated").withStyle(ChatFormatting.RED);
                    } else {
                        status = BallistixTextUtils.gui("turret.statusgood").withStyle(ChatFormatting.GREEN);
                    }
                }
            }


            return BallistixTextUtils.gui("turret.status", status).withStyle(ChatFormatting.BLACK);
        }));

        addComponent(tempLabel = new ScreenComponentSimpleLabel(10, 20, 10, Color.BLACK, () -> {
            TileTurretLaser turret = menu.getSafeHost();
            if (turret == null) {
                return Component.empty();
            }
            return BallistixTextUtils.gui("turret.temperature", ChatFormatter.getChatDisplayShort(turret.heat.getValue() + 32, DisplayUnits.TEMPERATURE_CELCIUS).withStyle(ChatFormatting.DARK_GRAY));
        }));

        addComponent(heatBar = new ScreenComponentCustomRender(0, 0, poseStack -> {

            int width = (int) getGuiWidth();
            int height = (int) getGuiHeight();

            int x = width + 10;
            int y = height + 33;

            TileTurretLaser turret = menu.getSafeHost();
            if (turret == null) {
                return;
            }

            Color start = new Color(0, 255, 0, 255);

            Color end = start;

            int maxX = (int) (155.0F * turret.heat.getValue() / BallistixConstants.LASER_TURRET_MAXHEAT);

            if (turret.heat.getValue() > BallistixConstants.LASER_TURRET_MAXHEAT * 0.8) {
                start = new Color(255, 255, 0, 255);
                end = new Color(255, 0, 0, 255);
            } else if (turret.heat.getValue() > BallistixConstants.LASER_TURRET_MAXHEAT * 0.4) {
                end = new Color(255, 255, 0, 255);
            }

            fill(poseStack, x, y, x + 156, y + 12, Color.TEXT_GRAY.color());

            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder vertex = tesselator.getBuilder();
            vertex.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            Matrix4f matrix4f = poseStack.last().pose();
            vertex.vertex(matrix4f, x + 1, y + 1, 0).color(start.color()).endVertex();
            vertex.vertex(matrix4f, x + 1, y + 11, 0).color(start.color()).endVertex();
            vertex.vertex(matrix4f, x + maxX, y + 11, 0).color(end.color()).endVertex();
            vertex.vertex(matrix4f, x + maxX, y + 1, 0).color(end.color()).endVertex();
            tesselator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();

            //poseStack.fillGradient(x + 1, y + 1, x + maxX, y + 11, start.color(), end.color());


        }));

        addComponent(new ScreenComponentButton<>(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR_RIGHT, 176, AbstractScreenComponentInfo.SIZE + 2).setOnPress(button -> {
            TileTurretLaser turret = menu.getSafeHost();
            if(turret == null) {
                return;
            }
            turret.onlyTargetPlayers.setValue(!turret.onlyTargetPlayers.getValue());
        }).onTooltip((poseStack, but, xAxis, yAxis) -> {
            //
            TileTurretLaser turret = menu.getSafeHost();
            if(turret == null) {
                return;
            }
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(BallistixTextUtils.tooltip("turret.targetmode").withStyle(ChatFormatting.DARK_GRAY));
            if (turret.onlyTargetPlayers.getValue()) {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeplayers").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            } else {
                tooltips.add(BallistixTextUtils.tooltip("turret.targetmodeliving").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }

            renderComponentTooltip(poseStack, tooltips, xAxis, yAxis);

        }).setIcon(BallistixIconTypes.TARGET_ONLY_PLAYERS));

    }

    @Override
    public void updateVisibility(boolean show) {
        radarText.setVisible(show);
        heatBar.setVisible(show);
        statusLabel.setVisible(show);
        tempLabel.setVisible(show);
        for (int i = menu.getAdditionalSlotCount(); i < menu.slots.size(); i++) {

            ((SlotGeneric) menu.slots.get(i)).setActive(show);

        }
    }

}
