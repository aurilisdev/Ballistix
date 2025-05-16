package ballistix.client.guidebook.chapters;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.settings.BallistixConstants;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixItems;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.guidebook.utils.components.Chapter;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import voltaic.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;
import voltaic.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;
import voltaic.client.guidebook.utils.pagedata.text.TextWrapperObject;

public class ChapterMissileDefense extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar));

    public ChapterMissileDefense(Module module) {
        super(module);
    }

    @Override
    public AbstractGraphicWrapper<?> getLogo() {
        return LOGO;
    }

    @Override
    public IFormattableTextComponent getTitle() {
        return BallistixTextUtils.guidebook("chapter.missiledefense");
    }

    @Override
    public void addData() {

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l1")).setSeparateStart().setIndentions(1));

        // Search Radar
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.searchradar1", BallistixConstants.RADAR_RANGE)).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/searchradar1.png")));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/searchradar2.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.searchradar2")).setSeparateStart());

        // Fire Control Radar
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.firecontrolradar1", BallistixItems.ITEM_RADARGUN.get().getDescription().copy().withStyle(TextFormatting.BOLD), BallistixConstants.MAX_DISTANCE_FROM_RADAR)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.firecontrolradar2", BallistixConstants.FIRE_CONTROL_RADAR_RANGE)).setSeparateStart().setIndentions(1));

        // ESM Tower
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.esmtower1", BallistixConstants.ESM_TOWER_SEARCH_RADIUS)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.esmtower2")).setSeparateStart().setIndentions(1));

        // SAM Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.samturret1",
                //
                BallistixItems.ITEM_AAMISSILE.get().getDescription().copy().withStyle(TextFormatting.BOLD),
                //
                BallistixConstants.SAM_TURRET_BASE_RANGE,
                //
                BallistixConstants.SAM_TURRET_COOLDOWN,
                //
                ChatFormatter.getChatDisplayShort(BallistixConstants.SAM_CHANCE_TO_DESTROY * 100, DisplayUnits.PERCENTAGE))).setSeparateStart().setIndentions(1));

        // CIWS Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret1", BallistixItems.ITEM_BULLET.get().getDescription().copy().withStyle(TextFormatting.BOLD), BallistixConstants.CIWS_TURRET_BASE_RANGE, BallistixConstants.MISSILE_HEALTH)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret2")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/ciwsturret1.png")));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/ciwsturret2.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret3")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret4")).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/ciwsturret3.png")));

        // Laser Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.laserturret1", BallistixConstants.LASER_TURRET_BASE_RANGE)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.laserturret2")).setSeparateStart().setIndentions(1));

        // Railgun Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.railgunturret1", BallistixConstants.RAILGUN_TURRET_BASE_RANGE, BallistixConstants.RAILGUN_TURRET_COOLDOWN)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.railgunturret2")).setSeparateStart().setIndentions(1));

        // Missile Silo
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.missilesilo").withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.missilesilo1", BallistixItems.ITEM_AAMISSILEMK2.get().getDescription().copy().withStyle(TextFormatting.BOLD), ChatFormatter.getChatDisplayShort(BallistixConstants.ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY * 100, DisplayUnits.PERCENTAGE))).setSeparateStart().setIndentions(1).setSeparateStart().setIndentions(1));


    }


}
