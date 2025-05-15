package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentCustomRender;
import voltaic.prefab.screen.component.types.ScreenComponentSimpleLabel;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import voltaic.prefab.screen.component.types.wrapper.WrapperInventoryIO;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.math.Color;

public class ScreenSAMTurret extends GenericScreen<ContainerSAMTurret> {
    public ScreenSAMTurret(ContainerSAMTurret container, Inventory inv, Component title) {
        super(container, inv, title);

        inventoryLabelY += 10;
        imageHeight += 10;

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(BallistixConstants.SAM_TURRET_USAGEPERTICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, BallistixIconTypes.TARGET_MISSILE, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretSAM turret = menu.getSafeHost();
            if(turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.getValue(), 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

        new WrapperInventoryIO(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2, 75, 92, 8, 82);

        addComponent(new ScreenComponentCustomRender(10, 50, poseStack -> {
            TileTurretAntimissile turret = menu.getSafeHost();
            if(turret == null) {
                return;
            }
            Component radar = turret.isNotLinked.getValue() ? BallistixTextUtils.gui("turret.radarnone").withStyle(ChatFormatting.RED) : Component.literal(turret.boundFireControl.getValue().toShortString()).withStyle(ChatFormatting.DARK_GRAY);

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

            graphics.pushPose();
            poseStack.pushPose();

            poseStack.translate(x, y + remHeight, 0);

            poseStack.scale(scale, scale, scale);

            getFontRenderer().draw(poseStack, radar, 0, 0, Color.WHITE.color());

            poseStack.popPose();



        }));

        addComponent(new ScreenComponentSimpleLabel(10, 65, 10, Color.WHITE, () -> {
            TileTurretSAM turret = menu.getSafeHost();
            if(turret == null) {
                return Component.empty();
            }
            Component status;

            if(turret.hasNoPower.getValue()) {
                status = BallistixTextUtils.gui("turret.statusnopower").withStyle(ChatFormatting.RED);
            } else if (turret.boundFireControl.getValue().equals(BlockEntityUtils.OUT_OF_REACH)) {
                status = BallistixTextUtils.gui("turret.statusunlinked").withStyle(ChatFormatting.RED);
            } else if (!turret.hasTarget.getValue()) {
                status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(ChatFormatting.GREEN);
            } else if (!turret.inRange.getValue()) {
                status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(ChatFormatting.YELLOW);
            } else if (turret.outOfAmmo.getValue()) {
                status = BallistixTextUtils.gui("turret.statusnoammo").withStyle(ChatFormatting.RED);
            } else if (turret.cooldown.getValue() > 0) {
                status = BallistixTextUtils.gui("turret.statuscooldown", turret.cooldown.getValue()).withStyle(ChatFormatting.RED);
            } else {
                status = BallistixTextUtils.gui("turret.statusgood").withStyle(ChatFormatting.GREEN);
            }


            return BallistixTextUtils.gui("turret.status", status).withStyle(ChatFormatting.BLACK);
        }));
    }
}
