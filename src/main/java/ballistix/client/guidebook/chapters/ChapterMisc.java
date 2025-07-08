package ballistix.client.guidebook.chapters;

import ballistix.Ballistix;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.guidebook.utils.components.Chapter;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import voltaic.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;
import voltaic.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;
import voltaic.client.guidebook.utils.pagedata.text.TextWrapperObject;

public class ChapterMisc extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.cluster));

    public ChapterMisc(Module module) {
        super(module);
    }

    @Override
    public AbstractGraphicWrapper<?> getLogo() {
        return LOGO;
    }

    @Override
    public MutableComponent getTitle() {
        return BallistixTextUtils.guidebook("chapter.misc");
    }

    @Override
    public void addData() {

        // Cluster Missile

        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.cluster).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_MISSILE.getValue(SubtypeMissile.cluster)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.clustermissile1")).setSeparateStart());

        // Air Raid Siren

        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.airraidsiren).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.airraidsiren)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.airraidsiren1")).setSeparateStart());

        // Proximity Detector

        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.proximitydetector).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_BALLISTIXMACHINE.getValue(SubtypeBallistixMachine.proximitydetector)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector1")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector2.1")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/proximitydetector1.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector2.2")).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/proximitydetector2.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector3.1")).setSeparateStart().setIndentions(1));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/proximitydetector3.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector3.2")).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/proximitydetector4.png")));
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 150, 150, 150, Ballistix.rl("textures/screen/guidebook/proximitydetector5.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector3.3")).setSeparateStart());
        pageData.add(new ImageWrapperObject(0, 0, 0, 0, 150, 75, 150, 79, Ballistix.rl("textures/screen/guidebook/proximitydetector6.png")));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector3.4")).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.misc.proximitydetector4")).setSeparateStart().setIndentions(1));



    }


}
