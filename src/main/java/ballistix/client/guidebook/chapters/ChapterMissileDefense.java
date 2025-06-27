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

        // Fire Control Radar

        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l2",
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.firecontrolradar).getDescription().copy(),
                //
                BallistixConstants.FIRE_CONTROL_RADAR_RANGE
        //
        )).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l3.1",
                //
                BallistixItems.ITEM_RADARGUN.get().getDescription().copy().withStyle(TextFormatting.BOLD)
                //
        )).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/turretbind1.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l3.2",
                //
                BallistixItems.ITEM_RADARGUN.get().getDescription()
                //
        )).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/turretbind2.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l3.3",
                //
                BallistixConstants.MAX_DISTANCE_FROM_RADAR
                //
        )).setSeparateStart());

        // SAM Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.samturret)));

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretminrange", 100)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretmaxrange", BallistixConstants.SAM_TURRET_BASE_RANGE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretammo", BallistixItems.ITEM_AAMISSILE.get().getDescription())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretelevation", 90)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretdepression", 45)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.samturret1",
                //
                BallistixItems.ITEM_AAMISSILE.get().getDescription(),
                //
                ChatFormatter.getChatDisplayShort(BallistixConstants.SAM_CHANCE_TO_DESTROY * 100, DisplayUnits.PERCENTAGE),
                //
                BallistixConstants.SAM_TURRET_COOLDOWN
        //
        )).setSeparateStart());


        // CIWS Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.ciwsturret)));

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretminrange", 0)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretmaxrange", BallistixConstants.CIWS_TURRET_BASE_RANGE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretammo", BallistixItems.ITEM_BULLET.get().getDescription())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretelevation", 90)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretdepression", 45)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret1",
                //
                BallistixItems.ITEM_BULLET.get().getDescription(),
                //
                BallistixConstants.MISSILE_HEALTH
                //
        )).setSeparateStart());

        // Laser Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.laserturret)));

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretminrange", 0)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretmaxrange", BallistixConstants.LASER_TURRET_BASE_RANGE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretammo", BallistixTextUtils.guidebook("chapter.missiledefense.laserturret.energy"))).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretelevation", 90)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretdepression", 45)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.laserturret1")).setSeparateStart());

        // Railgun Turret
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.railgunturret)));

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretminrange", 0)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretmaxrange", BallistixConstants.RAILGUN_TURRET_BASE_RANGE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretammo", BallistixTextUtils.guidebook("chapter.missiledefense.railgunturret.rod"))).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretelevation", 45)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.turretdepression", 45)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.railgunturret1", BallistixConstants.RAILGUN_TURRET_COOLDOWN)).setSeparateStart());

        // Turret Targeting Features

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l4")).setNewPage().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l5.1")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/turrettargeting1.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l5.2")).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/turrettargeting2.png")));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/turrettargeting3.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l5.3")).setSeparateStart());

        // Anti-Ballistic Missile
        pageData.add(new TextWrapperObject(BallistixItems.ITEM_AAMISSILEMK2.get().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEM_AAMISSILEMK2.get()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.antiballisticmissile1",
                //
                BallistixItems.ITEM_AAMISSILEMK2.get().getDescription(),
                //
                ChatFormatter.getChatDisplayShort(BallistixConstants.ANTIBALLISTICMISSILE_CHANCE_TO_DESTROY * 100, DisplayUnits.PERCENTAGE)
        //
        )).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/antiballistic1.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.antiballisticmissile2")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/antiballistic2.png")));

        //

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l6")).setNewPage().setIndentions(1));

        // ESM Tower
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.esmtower)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.esmtower1", BallistixConstants.ESM_TOWER_SEARCH_RADIUS)).setSeparateStart().setIndentions(1));


        //

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l7")).setNewPage().setIndentions(1));


        // Search Radar
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar).getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.radar)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.searchradar1", BallistixConstants.RADAR_RANGE)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.searchradar2")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/searchradar1.png")));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/searchradar2.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.searchradar3")).setSeparateStart());


        // ESM Tower Counter-launch

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l8")).setNewPage().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l9")).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l10")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/counterlaunch1.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.l11")).setSeparateStart());

    }


}
