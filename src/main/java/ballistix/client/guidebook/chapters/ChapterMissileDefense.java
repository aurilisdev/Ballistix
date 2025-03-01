package ballistix.client.guidebook.chapters;

import ballistix.References;
import ballistix.common.settings.Constants;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import electrodynamics.client.guidebook.ScreenGuidebook;
import electrodynamics.client.guidebook.utils.components.Chapter;
import electrodynamics.client.guidebook.utils.components.Module;
import electrodynamics.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import electrodynamics.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;
import electrodynamics.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;
import electrodynamics.client.guidebook.utils.pagedata.text.TextWrapperObject;
import electrodynamics.common.item.subtype.SubtypeRod;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ChapterMissileDefense extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockRadar.asItem());

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
        pageData.add(new TextWrapperObject(BallistixBlocks.blockRadar.asItem().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockRadar.asItem()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.searchradar1", Constants.RADAR_RANGE)).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, new ResourceLocation(References.ID, "textures/screen/guidebook/searchradar1.png")));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, new ResourceLocation(References.ID, "textures/screen/guidebook/searchradar2.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.searchradar2")).setSeparateStart());

        // Fire Control Radar
        pageData.add(new TextWrapperObject(BallistixBlocks.blockFireControlRadar.asItem().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockFireControlRadar.asItem()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.firecontrolradar1", BallistixItems.ITEM_RADARGUN.get().getDescription().copy().withStyle(TextFormatting.BOLD), Constants.MAX_DISTANCE_FROM_RADAR)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.firecontrolradar2", Constants.FIRE_CONTROL_RADAR_RANGE)).setSeparateStart().setIndentions(1));

        // ESM Tower
        pageData.add(new TextWrapperObject(BallistixBlocks.blockEsmTower.asItem().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockEsmTower.asItem()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.esmtower1", Constants.ESM_TOWER_SEARCH_RADIUS)).setSeparateStart().setIndentions(1));

        // SAM Turret
        pageData.add(new TextWrapperObject(BallistixBlocks.blockSamTurret.asItem().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockSamTurret.asItem()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.samturret1", BallistixItems.ITEM_AAMISSILE.get().getDescription().copy().withStyle(TextFormatting.BOLD), Constants.SAM_TURRET_BASE_RANGE, Constants.SAM_TURRET_COOLDOWN)).setSeparateStart().setIndentions(1));

        // CIWS Turret
        pageData.add(new TextWrapperObject(BallistixBlocks.blockCiwsTurret.asItem().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockCiwsTurret.asItem()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret1", BallistixItems.ITEM_BULLET.get().getDescription().copy().withStyle(TextFormatting.BOLD), Constants.CIWS_TURRET_BASE_RANGE, Constants.MISSILE_HEALTH)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret2")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, new ResourceLocation(References.ID, "textures/screen/guidebook/ciwsturret1.png")));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, new ResourceLocation(References.ID, "textures/screen/guidebook/ciwsturret2.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret3")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.ciwsturret4")).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, new ResourceLocation(References.ID, "textures/screen/guidebook/ciwsturret3.png")));

        // Laser Turret
        pageData.add(new TextWrapperObject(BallistixBlocks.blockLaserTurret.asItem().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockLaserTurret.asItem()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.laserturret1", Constants.LASER_TURRET_BASE_RANGE)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.laserturret2")).setSeparateStart().setIndentions(1));

        // Railgun Turret
        pageData.add(new TextWrapperObject(BallistixBlocks.blockRailgunTurret.asItem().getDescription().copy().withStyle(TextFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixBlocks.blockRailgunTurret.asItem()));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.railgunturret1", ElectrodynamicsItems.getItem(SubtypeRod.steel).getDescription().copy().withStyle(TextFormatting.BOLD), Constants.RAILGUN_TURRET_BASE_RANGE, Constants.RAILGUN_TURRET_COOLDOWN)).setSeparateStart().setIndentions(1));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missiledefense.railgunturret2")).setSeparateStart().setIndentions(1));


    }


}
