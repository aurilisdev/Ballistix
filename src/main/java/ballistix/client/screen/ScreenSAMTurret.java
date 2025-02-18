package ballistix.client.screen;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.settings.Constants;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.BallistixIconTypes;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.common.tile.machines.quarry.TileQuarry;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentGuiTab;
import electrodynamics.prefab.screen.component.types.wrapper.InventoryIOWrapper;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public class ScreenSAMTurret extends GenericScreen<ContainerSAMTurret> {
    public ScreenSAMTurret(ContainerSAMTurret container, Inventory inv, Component title) {
        super(container, inv, title);

        inventoryLabelY += 10;
        imageHeight += 10;

        addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.SAM_TURRET_USAGEPERTICK * 20));

        addComponent(new ScreenComponentGuiTab(ScreenComponentGuiTab.GuiInfoTabTextures.REGULAR, BallistixIconTypes.TARGET_MISSILE, () -> {
            List<FormattedCharSequence> text = new ArrayList<>();
            TileTurretSAM turret = menu.getHostFromIntArray();
            if(turret == null) {
                return text;

            }
            text.add(BallistixTextUtils.tooltip("turret.blockrange").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.maxrange", ChatFormatter.formatDecimals(turret.currentRange.get(), 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            text.add(BallistixTextUtils.tooltip("turret.minrange", ChatFormatter.formatDecimals(turret.minimumRange, 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            return text;
        }, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2));

        new InventoryIOWrapper(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE * 2 + 2, 75, 92, 8, 82);

        addComponent(new ScreenComponentCustomRender(10, 50, graphics -> {
            TileTurretAntimissile turret = menu.getHostFromIntArray();
            if(turret == null) {
                return;
            }
            Component radar = turret.isNotLinked.get() ? BallistixTextUtils.gui("turret.radarnone").withStyle(ChatFormatting.RED) : Component.literal(turret.boundFireControl.get().toShortString()).withStyle(ChatFormatting.DARK_GRAY);

            int x = (int) (getGuiWidth() + 10);
            int y = (int) (getGuiHeight() + 50);

            Component label = BallistixTextUtils.gui("turret.radar").withStyle(ChatFormatting.BLACK);

            int width = getFontRenderer().width(label);
            int height = getFontRenderer().lineHeight;

            graphics.drawString(getFontRenderer(), label, x, y, Color.WHITE.color(), false);

            x+= width;

            float scale = 1.0F;

            width = font.width(radar);

            if(width > 100) {
                scale = 100.0F / width;
            }

            float remHeight = (height - height * scale) / 2.0F;

            graphics.pose().pushPose();

            graphics.pose().translate(x, y + remHeight, 0);

            graphics.pose().scale(scale, scale, scale);

            graphics.drawString(getFontRenderer(), radar, 0, 0, Color.WHITE.color(), false);

            graphics.pose().popPose();


        }));

        addComponent(new ScreenComponentBallistixLabel(10, 65, 10, Color.WHITE, () -> {
            TileTurretSAM turret = menu.getHostFromIntArray();
            if(turret == null) {
                return Component.empty();
            }
            Component status;

            if(turret.hasNoPower.get()) {
                status = BallistixTextUtils.gui("turret.statusnopower").withStyle(ChatFormatting.RED);
            } else if (turret.boundFireControl.get().equals(TileQuarry.OUT_OF_REACH)) {
                status = BallistixTextUtils.gui("turret.statusunlinked").withStyle(ChatFormatting.RED);
            } else if (!turret.hasTarget.get()) {
                status = BallistixTextUtils.gui("turret.statusnotarget").withStyle(ChatFormatting.GREEN);
            } else if (!turret.inRange.get()) {
                status = BallistixTextUtils.gui("turret.statusoutofrange").withStyle(ChatFormatting.YELLOW);
            } else if (turret.outOfAmmo.get()) {
                status = BallistixTextUtils.gui("turret.statusnoammo").withStyle(ChatFormatting.RED);
            } else if (turret.cooldown.get() > 0) {
                status = BallistixTextUtils.gui("turret.statuscooldown", turret.cooldown.get()).withStyle(ChatFormatting.RED);
            } else {
                status = BallistixTextUtils.gui("turret.statusgood").withStyle(ChatFormatting.GREEN);
            }


            return BallistixTextUtils.gui("turret.status", status).withStyle(ChatFormatting.BLACK);
        }));
    }
}
