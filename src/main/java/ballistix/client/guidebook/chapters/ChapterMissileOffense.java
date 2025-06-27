package ballistix.client.guidebook.chapters;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.settings.BallistixConstants;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.guidebook.utils.components.Chapter;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;
import voltaic.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;
import voltaic.client.guidebook.utils.pagedata.text.TextWrapperObject;

public class ChapterMissileOffense extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3));

    public ChapterMissileOffense(Module module) {
        super(module);
    }

    @Override
    public ItemWrapperObject getLogo() {
        return LOGO;
    }

    @Override
    public MutableComponent getTitle() {
        return BallistixTextUtils.guidebook("chapter.missileoffense");
    }

    @Override
    public void addData() {
    	
    	// MISSILE SILO

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l1.1")).setIndentions(1).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.controlpanel")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.launchplatform")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.supportframe")).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l1.2")).setSeparateStart());

        // Control Panel

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.controlpanel").withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l2",
                //
                BallistixTextUtils.guidebook("chapter.missileoffense.controlpanel")
                //
        )).setIndentions(1).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l3",
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier1).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier2).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3).getDescription().copy().withStyle(ChatFormatting.BOLD)
                //
        )).setIndentions(1).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/silo1.png")));

        // Launch Platform

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.launchplatform").withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l4",
                //
                BallistixTextUtils.guidebook("chapter.missileoffense.launchplatform"),
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier1).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                BallistixConstants.LAUNCHER_PLATFORM_RANGE_T1,
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier2).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                BallistixConstants.LAUNCHER_PLATFORM_RANGE_T2,
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launcherplatformtier3).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                BallistixConstants.LAUNCHER_PLATFORM_RANGE_T3
                //
        )).setIndentions(1).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l5")).setIndentions(1).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/silo2.png")));

        // Support Frame

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.supportframe").withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l6",
                //
                BallistixTextUtils.guidebook("chapter.missileoffense.supportframe"),
                //
                Component.literal(BallistixConstants.LAUNCH_PLATFORM_DEFAULT_INACCURACY + "").withStyle(ChatFormatting.BOLD),
                //
                Component.literal(BallistixConstants.LAUNCH_PLATFORM_DEFAULT_INACCURACY + ""),
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier1).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                Component.literal("" + 30).withStyle(ChatFormatting.BOLD),
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier2).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                Component.literal("" + 15).withStyle(ChatFormatting.BOLD),
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchersupportframetier3).getDescription().copy().withStyle(ChatFormatting.BOLD),
                //
                Component.literal("" + 0).withStyle(ChatFormatting.BOLD)
                //
        )).setIndentions(1).setSeparateStart());

        //Assembly

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l7",
                //
                BallistixTextUtils.guidebook("chapter.missileoffense.launchplatform"),
                //
                BallistixTextUtils.guidebook("chapter.missileoffense.controlpanel"),
                //
                BallistixTextUtils.guidebook("chapter.missileoffense.supportframe")
                //
        )).setIndentions(1).setNewPage());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 151, Ballistix.rl("textures/screen/guidebook/silo3.png")));

        // VLS

        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setSeparateStart());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l8",
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.vls).getDescription().copy(),
                //
                BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.launchercontrolpaneltier3).getDescription().copy(),
                //
                BallistixConstants.LAUNCHER_PLATFORM_RANGE_T1
                //
        )).setIndentions(1).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.missileoffense.l9")).setIndentions(1).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/vls1.png")));

    }

}
