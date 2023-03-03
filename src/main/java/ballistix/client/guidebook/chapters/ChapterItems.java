package ballistix.client.guidebook.chapters;

import ballistix.prefab.utils.TextUtils;
import ballistix.registers.BallistixItems;
import electrodynamics.client.guidebook.ScreenGuidebook;
import electrodynamics.client.guidebook.utils.components.Chapter;
import electrodynamics.client.guidebook.utils.components.Module;
import electrodynamics.client.guidebook.utils.pagedata.ItemWrapperObject;
import electrodynamics.client.guidebook.utils.pagedata.TextWrapperObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class ChapterItems extends Chapter {

	private static final ItemWrapperObject LOGO = new ItemWrapperObject(7, 10, 2.0F, 32, 32, BallistixItems.ITEM_ROCKETLAUNCHER.get());

	public ChapterItems(Module module) {
		super(module);
	}

	@Override
	public ItemWrapperObject getLogo() {
		return LOGO;
	}

	@Override
	public MutableComponent getTitle() {
		return TextUtils.guidebook("chapter.items");
	}

	@Override
	public void addData() {

		// Rocket Launcher
		pageData.add(new TextWrapperObject(BallistixItems.ITEM_ROCKETLAUNCHER.get().getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setSeparateStart());
		pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 2.0F, 32, 32, BallistixItems.ITEM_ROCKETLAUNCHER.get()));
		pageData.add(new TextWrapperObject(TextUtils.guidebook("chapter.items.rocketlauncher1")).setSeparateStart().setIndentions(1));

		// Radar Gun
		pageData.add(new TextWrapperObject(BallistixItems.ITEM_RADARGUN.get().getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
		pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 2.0F, 32, 32, BallistixItems.ITEM_RADARGUN.get()));
		pageData.add(new TextWrapperObject(TextUtils.guidebook("chapter.items.radargun1")).setSeparateStart().setIndentions(1));

		// Laser Designator
		pageData.add(new TextWrapperObject(BallistixItems.ITEM_LASERDESIGNATOR.get().getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
		pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 2.0F, 32, 32, BallistixItems.ITEM_LASERDESIGNATOR.get()));
		pageData.add(new TextWrapperObject(TextUtils.guidebook("chapter.items.laserdesignator1")).setSeparateStart().setIndentions(1));
		pageData.add(new TextWrapperObject(TextUtils.guidebook("chapter.items.laserdesignator2")).setSeparateStart().setIndentions(1));

		// Defuser
		pageData.add(new TextWrapperObject(BallistixItems.ITEM_DEFUSER.get().getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
		pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 2.0F, 32, 32, BallistixItems.ITEM_DEFUSER.get()));
		pageData.add(new TextWrapperObject(TextUtils.guidebook("chapter.items.defuser1")).setSeparateStart().setIndentions(1));
		
		//Tracker
		pageData.add(new TextWrapperObject(BallistixItems.ITEM_TRACKER.get().getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
		pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 2.0F, 32, 32, BallistixItems.ITEM_TRACKER.get()));
		pageData.add(new TextWrapperObject(TextUtils.guidebook("chapter.items.tracker1")).setSeparateStart().setIndentions(1));

		// Scanner
		pageData.add(new TextWrapperObject(BallistixItems.ITEM_SCANNER.get().getDescription().copy().withStyle(ChatFormatting.BOLD)).setCentered().setNewPage());
		pageData.add(new ItemWrapperObject(7 + ScreenGuidebook.TEXT_WIDTH / 2 - 16, 10, 2.0F, 32, 32, BallistixItems.ITEM_SCANNER.get()));
		pageData.add(new TextWrapperObject(TextUtils.guidebook("chapter.items.scanner1")).setSeparateStart().setIndentions(1));

	}

}
