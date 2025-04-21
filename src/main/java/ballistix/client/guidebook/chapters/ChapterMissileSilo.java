package ballistix.client.guidebook.chapters;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.settings.BallistixConstants;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.utils.components.Chapter;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;
import voltaic.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;
import voltaic.client.guidebook.utils.pagedata.text.TextWrapperObject;

public class ChapterMissileSilo extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3));

    public ChapterMissileSilo(Module module) {
        super(module);
    }

    @Override
    public ItemWrapperObject getLogo() {
        return LOGO;
    }

    @Override
    public MutableComponent getTitle() {
        return BallistixTextUtils.guidebook("chapter.missilesilo");
    }

    @Override
    public void addData() {
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.l1.1")).setIndentions(1).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.controlpanel")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.launchplatform")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.supportframe")).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.l1.2")).setSeparateStart());

        // Control Panel

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.l2", BallistixTextUtils.guidebook("chapter.missilesilo.controlpanel").withStyle(ChatFormatting.BOLD))).setIndentions(1).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/silo1.png")));

        // Launch Platform

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.l3.1",
                //
                BallistixTextUtils.guidebook("chapter.missilesilo.launchplatform").withStyle(ChatFormatting.BOLD),
                //
                BallistixConstants.LAUNCHER_PLATFORM_RANGE_T1,
                //
                BallistixConstants.LAUNCHER_PLATFORM_RANGE_T2,
                //
                BallistixConstants.LAUNCHER_PLATFORM_RANGE_T3
                //
        )).setIndentions(1).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.range", BallistixTextUtils.guidebook("chapter.missilesilo.close"), BallistixConstants.LAUNCHER_PLATFORM_RANGE_T1)).setIndentions(1).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.range", BallistixTextUtils.guidebook("chapter.missilesilo.medium"), BallistixConstants.LAUNCHER_PLATFORM_RANGE_T2)).setIndentions(1).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.range", BallistixTextUtils.guidebook("chapter.missilesilo.long"), BallistixConstants.LAUNCHER_PLATFORM_RANGE_T3)).setIndentions(1).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.l3.2")).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/silo2.png")));

        // Support Frame

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missilesilo.l4", BallistixTextUtils.guidebook("chapter.missilesilo.supportframe").withStyle(ChatFormatting.BOLD), BallistixConstants.LAUNCH_PLATFORM_DEFAULT_INACCURACY)).setIndentions(1).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 151, Ballistix.rl("textures/screen/guidebook/silo3.png")));

    }

}
