package ballistix.client.guidebook.chapters;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.ScreenGuidebook;
import voltaic.client.guidebook.utils.components.Chapter;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.AbstractGraphicWrapper;
import voltaic.client.guidebook.utils.pagedata.graphics.ItemWrapperObject;
import voltaic.client.guidebook.utils.pagedata.text.TextWrapperObject;

public class ChapterExplosives extends Chapter {

    private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter));

    public ChapterExplosives(Module module) {
        super(module);
    }

    @Override
    public AbstractGraphicWrapper<?> getLogo() {
        return LOGO;
    }

    @Override
    public MutableComponent getTitle() {
        return BallistixTextUtils.guidebook("chapter.explosives");
    }

    @Override
    public void addData() {

        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.l1")).setSeparateStart().setIndentions(1));

        /* TIER 1 */

        // Obsidian

        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.obsidian).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.obsidian)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.obsidian.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.obsidian.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_OBSIDIAN_SIZE)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.obsidian1")).setSeparateStart());

        // Condensive
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.condensive)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.condensive.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.condensive.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_CONDENSIVE_SIZE)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.condensive1")).setSeparateStart());

        // Attractive
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.attractive).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.attractive)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.attractive.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.attractive.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_ATTRACTIVE_SIZE)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.attractive1")).setSeparateStart());

        // Repulsive
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.repulsive)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.repulsive.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.repulsive.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_REPULSIVE_SIZE)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.repulsive1")).setSeparateStart());

        // Incendiary
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.incendiary).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.incendiary)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.incendiary.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.incendiary.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_INCENDIARY_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.incendiary1")).setSeparateStart());

        // Shrapnel
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.shrapnel)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.shrapnel.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.shrapnel.fuse())).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.shrapnel1", BallistixConstants.EXPLOSIVE_SHRAPNEL_SHRAPNEL_COUNT)).setSeparateStart());

        // Chemical
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.chemical).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.chemical)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.chemical.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.chemical.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_CHEMICAL_SIZE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.duration", BallistixConstants.EXPLOSIVE_CHEMICAL_DURATION)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.chemical1", 360)).setSeparateStart());

        // Shrapnel
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.anvil).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.anvil)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.anvil.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.anvil.fuse())).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.anvil1", BallistixConstants.EXPLOSIVE_ANVIL_ANVILSPERBLAST)).setSeparateStart());

        // Infestive
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.infestive).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.infestive)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.infestive.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.infestive.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_INFESTIVE_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.infestive1")).setSeparateStart());

        // Debilitation
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.debilitation).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.debilitation)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.debilitation.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.debilitation.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_DEBILITATION_SIZE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.duration", BallistixConstants.EXPLOSIVE_DEBILITATION_DURATION)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.debilitation1", 360, 1200)).setSeparateStart());

        /* TIER 2 */

        // Fragmentation
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.fragmentation)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.fragmentation.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.fragmentation.fuse())).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fragmentation1", BallistixConstants.EXPLOSIVE_FRAGMENTATION_SHRAPNEL_COUNT)).setSeparateStart());

        // Contagious
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.contagious).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.contagious)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.contagious.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.contagious.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_CONTAGIOUS_SIZE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.duration", BallistixConstants.EXPLOSIVE_CONTAGIOUS_DURATION)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.contagious1", 360)).setSeparateStart());

        // Breaching
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.breaching).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.breaching)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.breaching.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.breaching.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_BREACHING_SIZE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.energy", BallistixConstants.EXPLOSIVE_BREACHING_ENERGY)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.breaching1")).setSeparateStart());

        // Thermobaric
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.thermobaric)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.thermobaric.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.thermobaric.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.energy", BallistixConstants.EXPLOSIVE_THERMOBARIC_ENERGY)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.thermobaric1")).setSeparateStart());

        // Sonic
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.sonic).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.sonic)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.sonic.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.sonic.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_SONIC_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.sonic1")).setSeparateStart());

        /* TIER 3 */

        // Antigravity
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antigravity).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antigravity)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.antigravity.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.antigravity.fuse())).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.antigravity1", BallistixConstants.EXPLOSIVE_ANTIGRAVITY_CHUNKRADIUS, BallistixConstants.EXPLOSIVE_ANTIGRAVITY_CHUNKDURATION)).setSeparateStart());

        // EMP
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.emp).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.emp)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.emp.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.emp.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_EMP_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.emp1")).setSeparateStart());

        // Nuclear
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.nuclear).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.nuclear)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.nuclear.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.nuclear.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_NUCLEAR_SIZE)).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.energy", BallistixConstants.EXPLOSIVE_NUCLEAR_ENERGY)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.nuclear1")).setSeparateStart());

        // Endothermic
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.endothermic).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.endothermic)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.endothermic.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.endothermic.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_ENDOTHERMIC_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.endothermic1")).setSeparateStart());

        // Exothermic
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.exothermic).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.exothermic)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.exothermic.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.exothermic.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_EXOTHERMIC_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.exothermic1")).setSeparateStart());

        // Ender
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.ender).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.ender)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.ender.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.ender.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_ENDER_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.ender1")).setSeparateStart());

        // Hypersonic
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.hypersonic).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.hypersonic)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.hypersonic.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.hypersonic.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_HYPERSONIC_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.hypersonic1")).setSeparateStart());

        // Rejuvination
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.rejuvination).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.rejuvination)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.rejuvination.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.rejuvination.fuse())).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.rejuvination1")).setSeparateStart());

        // Antimatter
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antimatter).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.antimatter)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.antimatter.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.antimatter.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_ANTIMATTER_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.antimatter1")).setSeparateStart());

        // Large Antimatter
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.largeantimatter)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.largeantimatter.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.largeantimatter.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_LARGEANTIMATTER_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.largeantimatter1")).setSeparateStart());

        // Dark Matter
        pageData.add(new TextWrapperObject(BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter).getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
        pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 32, 32, 32, 2.0F, BallistixItems.ITEMS_EXPLOSIVE.getValue(SubtypeBlast.darkmatter)));
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.tier", SubtypeBlast.darkmatter.tier())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.fuse", SubtypeBlast.darkmatter.fuse())).setSeparateStart());
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.radius", BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS)).setSeparateStart());
        blankLine();
        pageData.add(new TextWrapperObject(BallistixTextUtils.guidebook("chapter.explosives.darkmatter1")).setSeparateStart());


    }


}
