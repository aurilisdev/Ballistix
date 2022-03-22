package ballistix.client.guidebook.chapters;

import java.util.ArrayList;
import java.util.List;

import ballistix.DeferredRegisters;
import electrodynamics.client.guidebook.utils.ItemWrapperObject;
import electrodynamics.client.guidebook.utils.TextWrapperObject;
import electrodynamics.client.guidebook.utils.components.Chapter;
import electrodynamics.client.guidebook.utils.components.Page;
import net.minecraft.ChatFormatting;

public class ChapterItems extends Chapter {

	private static final ItemWrapperObject LOGO = new ItemWrapperObject(17, 60, 2.0F, DeferredRegisters.ITEM_ROCKETLAUNCHER.get());
	
	@Override
	protected List<Page> genPages() {
		List<Page> pages = new ArrayList<>();
		
		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(45, 53, 4210752, "guidebook.ballistix.chapter.items.rocketlaunchertitle").setTextStyles(ChatFormatting.UNDERLINE),
				//
				new TextWrapperObject(10, 80, 4210752, "guidebook.ballistix.chapter.items.p1l1"),
				//
				new TextWrapperObject(10, 90, 4210752, "guidebook.ballistix.chapter.items.p1l2"),
				//
				new TextWrapperObject(10, 100, 4210752, "guidebook.ballistix.chapter.items.p1l3"),
				//
				new TextWrapperObject(10, 110, 4210752, "guidebook.ballistix.chapter.items.p1l4"),
				//
				new TextWrapperObject(10, 120, 4210752, "guidebook.ballistix.chapter.items.p1l5"),
				//
				new TextWrapperObject(10, 130, 4210752, "guidebook.ballistix.chapter.items.p1l6"),
				//
				new TextWrapperObject(10, 140, 4210752, "guidebook.ballistix.chapter.items.p1l7"),
				//
				new TextWrapperObject(10, 150, 4210752, "guidebook.ballistix.chapter.items.p1l8"),
				//
				new TextWrapperObject(10, 160, 4210752, "guidebook.ballistix.chapter.items.p1l9"),
				//
				new TextWrapperObject(10, 170, 4210752, "guidebook.ballistix.chapter.items.p1l10")},
				new ItemWrapperObject[] {
						//
						new ItemWrapperObject(17, 50, 2.0F, DeferredRegisters.ITEM_ROCKETLAUNCHER.get()) }));
		
		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(45, 53, 4210752, "guidebook.ballistix.chapter.items.radguntitle").setTextStyles(ChatFormatting.UNDERLINE),
				//
				new TextWrapperObject(10, 80, 4210752, "guidebook.ballistix.chapter.items.p2l1"),
				//
				new TextWrapperObject(10, 90, 4210752, "guidebook.ballistix.chapter.items.p2l2"),
				//
				new TextWrapperObject(10, 100, 4210752, "guidebook.ballistix.chapter.items.p2l3"),
				//
				new TextWrapperObject(10, 110, 4210752, "guidebook.ballistix.chapter.items.p2l4"),
				//
				new TextWrapperObject(10, 120, 4210752, "guidebook.ballistix.chapter.items.p2l5"),
				//
				new TextWrapperObject(10, 130, 4210752, "guidebook.ballistix.chapter.items.p2l6"),
				//
				new TextWrapperObject(10, 140, 4210752, "guidebook.ballistix.chapter.items.p2l7"),
				//
				new TextWrapperObject(10, 150, 4210752, "guidebook.ballistix.chapter.items.p2l8"),
				//
				new TextWrapperObject(10, 160, 4210752, "guidebook.ballistix.chapter.items.p2l9")},
				new ItemWrapperObject[] {
						//
						new ItemWrapperObject(17, 50, 2.0F, DeferredRegisters.ITEM_RADARGUN.get()) }));
		
		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(45, 53, 4210752, "guidebook.ballistix.chapter.items.laserdestitle").setTextStyles(ChatFormatting.UNDERLINE),
				//
				new TextWrapperObject(10, 80, 4210752, "guidebook.ballistix.chapter.items.p3l1"),
				//
				new TextWrapperObject(10, 90, 4210752, "guidebook.ballistix.chapter.items.p3l2"),
				//
				new TextWrapperObject(10, 100, 4210752, "guidebook.ballistix.chapter.items.p3l3"),
				//
				new TextWrapperObject(10, 110, 4210752, "guidebook.ballistix.chapter.items.p3l4"),
				//
				new TextWrapperObject(10, 120, 4210752, "guidebook.ballistix.chapter.items.p3l5"),
				//
				new TextWrapperObject(10, 130, 4210752, "guidebook.ballistix.chapter.items.p3l6"),
				//
				new TextWrapperObject(10, 140, 4210752, "guidebook.ballistix.chapter.items.p3l7"),
				//
				new TextWrapperObject(10, 150, 4210752, "guidebook.ballistix.chapter.items.p3l8"),
				//
				new TextWrapperObject(10, 160, 4210752, "guidebook.ballistix.chapter.items.p3l9"),
				//
				new TextWrapperObject(10, 170, 4210752, "guidebook.ballistix.chapter.items.p3l10"),
				//
				new TextWrapperObject(10, 180, 4210752, "guidebook.ballistix.chapter.items.p3l11") },
				new ItemWrapperObject[] {
						//
						new ItemWrapperObject(17, 50, 2.0F, DeferredRegisters.ITEM_LASERDESIGNATOR.get()) }));
		
		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(45, 53, 4210752, "guidebook.ballistix.chapter.items.defusertitle").setTextStyles(ChatFormatting.UNDERLINE),
				//
				new TextWrapperObject(10, 80, 4210752, "guidebook.ballistix.chapter.items.p4l1"),
				//
				new TextWrapperObject(10, 90, 4210752, "guidebook.ballistix.chapter.items.p4l2"),
				//
				new TextWrapperObject(10, 100, 4210752, "guidebook.ballistix.chapter.items.p4l3"),
				//
				new TextWrapperObject(10, 110, 4210752, "guidebook.ballistix.chapter.items.p4l4"),
				//
				new TextWrapperObject(10, 120, 4210752, "guidebook.ballistix.chapter.items.p4l5"),
				//
				new TextWrapperObject(10, 130, 4210752, "guidebook.ballistix.chapter.items.p4l6"),
				//
				new TextWrapperObject(10, 140, 4210752, "guidebook.ballistix.chapter.items.p4l7"),
				//
				new TextWrapperObject(10, 150, 4210752, "guidebook.ballistix.chapter.items.p4l8"),
				//
				new TextWrapperObject(10, 160, 4210752, "guidebook.ballistix.chapter.items.p4l9") },
				new ItemWrapperObject[] {
						//
						new ItemWrapperObject(17, 50, 2.0F, DeferredRegisters.ITEM_DEFUSER.get()) }));
		
		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(45, 53, 4210752, "guidebook.ballistix.chapter.items.trackertitle").setTextStyles(ChatFormatting.UNDERLINE),
				//
				new TextWrapperObject(10, 80, 4210752, "guidebook.ballistix.chapter.items.p5l1"),
				//
				new TextWrapperObject(10, 90, 4210752, "guidebook.ballistix.chapter.items.p5l2"),
				//
				new TextWrapperObject(10, 100, 4210752, "guidebook.ballistix.chapter.items.p5l3"),
				//
				new TextWrapperObject(10, 110, 4210752, "guidebook.ballistix.chapter.items.p5l4"),
				//
				new TextWrapperObject(10, 120, 4210752, "guidebook.ballistix.chapter.items.p5l5"),
				//
				new TextWrapperObject(10, 130, 4210752, "guidebook.ballistix.chapter.items.p5l6"),
				//
				new TextWrapperObject(10, 140, 4210752, "guidebook.ballistix.chapter.items.p5l7"),
				//
				new TextWrapperObject(10, 150, 4210752, "guidebook.ballistix.chapter.items.p5l8"),
				//
				new TextWrapperObject(10, 160, 4210752, "guidebook.ballistix.chapter.items.p5l9"),
				//
				new TextWrapperObject(10, 170, 4210752, "guidebook.ballistix.chapter.items.p5l10"),
				//
				new TextWrapperObject(10, 180, 4210752, "guidebook.ballistix.chapter.items.p5l11") },
				new ItemWrapperObject[] {
						//
						new ItemWrapperObject(17, 50, 2.0F, DeferredRegisters.ITEM_TRACKER.get()) }));
		
		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(45, 53, 4210752, "guidebook.ballistix.chapter.items.scannertitle").setTextStyles(ChatFormatting.UNDERLINE),
				//
				new TextWrapperObject(10, 80, 4210752, "guidebook.ballistix.chapter.items.p6l1"),
				//
				new TextWrapperObject(10, 90, 4210752, "guidebook.ballistix.chapter.items.p6l2"),
				//
				new TextWrapperObject(10, 100, 4210752, "guidebook.ballistix.chapter.items.p6l3"),
				//
				new TextWrapperObject(10, 110, 4210752, "guidebook.ballistix.chapter.items.p6l4"),
				//
				new TextWrapperObject(10, 120, 4210752, "guidebook.ballistix.chapter.items.p6l5"),
				//
				new TextWrapperObject(10, 130, 4210752, "guidebook.ballistix.chapter.items.p6l6"),
				//
				new TextWrapperObject(10, 140, 4210752, "guidebook.ballistix.chapter.items.p6l7") },
				new ItemWrapperObject[] {
						//
						new ItemWrapperObject(17, 50, 2.0F, DeferredRegisters.ITEM_SCANNER.get()) }));
		
		return pages;
	}

	@Override
	public ItemWrapperObject getLogo() {
		return LOGO;
	}

	@Override
	public String getTitleKey() {
		return "guidebook.ballistix.chapter.items";
	}

}
