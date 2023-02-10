package ballistix.client.guidebook.chapters;

import java.util.ArrayList;
import java.util.List;

import ballistix.References;
import ballistix.registers.BallistixBlocks;
import electrodynamics.client.guidebook.utils.ImageWrapperObject;
import electrodynamics.client.guidebook.utils.ItemWrapperObject;
import electrodynamics.client.guidebook.utils.TextWrapperObject;
import electrodynamics.client.guidebook.utils.components.Chapter;
import electrodynamics.client.guidebook.utils.components.Page;
import electrodynamics.prefab.utilities.ItemUtils;
import electrodynamics.prefab.utilities.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class ChapterBlocks extends Chapter {

	private static final ItemWrapperObject LOGO = new ItemWrapperObject(17, 60, 2.0F, ItemUtils.fromBlock(BallistixBlocks.blockMissileSilo));

	@Override
	protected List<Page> genPages() {
		List<Page> pages = new ArrayList<>();

		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(45, 53, 4210752, TextUtils.guidebook("chapter.blocks.missilesilotitle").withStyle(ChatFormatting.UNDERLINE)),
				//
				new TextWrapperObject(10, 80, 4210752, TextUtils.guidebook("chapter.blocks.p1l1")),
				//
				new TextWrapperObject(10, 90, 4210752, TextUtils.guidebook("chapter.blocks.p1l2")),
				//
				new TextWrapperObject(10, 100, 4210752, TextUtils.guidebook("chapter.blocks.p1l3")),
				//
				new TextWrapperObject(10, 110, 4210752, TextUtils.guidebook("chapter.blocks.p1l4")),
				//
				new TextWrapperObject(10, 120, 4210752, TextUtils.guidebook("chapter.blocks.p1l5")),
				//
				new TextWrapperObject(10, 130, 4210752, TextUtils.guidebook("chapter.blocks.p1l6")),
				//
				new TextWrapperObject(93, 140, 4210752, TextUtils.guidebook("chapter.blocks.silorange", 3000, TextUtils.guidebook("chapter.blocks.missileblocks"))),
				//
				new TextWrapperObject(10, 140, 4210752, TextUtils.guidebook("chapter.blocks.p1l7")),
				//
				new TextWrapperObject(93, 150, 4210752, TextUtils.guidebook("chapter.blocks.silorange", 10000, TextUtils.guidebook("chapter.blocks.missileblocks"))),
				//
				new TextWrapperObject(10, 150, 4210752, TextUtils.guidebook("chapter.blocks.p1l8")),
				//
				new TextWrapperObject(93, 160, 4210752, TextUtils.guidebook("chapter.blocks.silorange", TextUtils.guidebook("chapter.blocks.missileunlimited"))),
				//
				new TextWrapperObject(10, 160, 4210752, TextUtils.guidebook("chapter.blocks.p1l9")),
				//
				new TextWrapperObject(10, 170, 4210752, TextUtils.guidebook("chapter.blocks.p1l10")),
				//
				new TextWrapperObject(10, 180, 4210752, TextUtils.guidebook("chapter.blocks.p1l11")) },
				new ItemWrapperObject[] {
						//
						new ItemWrapperObject(17, 50, 2.0F, ItemUtils.fromBlock(BallistixBlocks.blockMissileSilo)) }));

		pages.add(new Page(new TextWrapperObject[] {
				//
				new TextWrapperObject(10, 40, 4210752, TextUtils.guidebook("chapter.blocks.p2l1")),
				//
				new TextWrapperObject(10, 50, 4210752, TextUtils.guidebook("chapter.blocks.p2l2")),
				//
				new TextWrapperObject(10, 60, 4210752, TextUtils.guidebook("chapter.blocks.p2l3")),
				//
				new TextWrapperObject(10, 70, 4210752, TextUtils.guidebook("chapter.blocks.p2l4")),
				//
				new TextWrapperObject(10, 80, 4210752, TextUtils.guidebook("chapter.blocks.p2l5")),
				//
				new TextWrapperObject(10, 90, 4210752, TextUtils.guidebook("chapter.blocks.p2l6")),
				//
				new TextWrapperObject(10, 100, 4210752, TextUtils.guidebook("chapter.blocks.p2l7")),
				//
				new TextWrapperObject(10, 110, 4210752, TextUtils.guidebook("chapter.blocks.p2l8")),
				//
				new TextWrapperObject(10, 120, 4210752, TextUtils.guidebook("chapter.blocks.p2l9")),
				//
				new TextWrapperObject(10, 130, 4210752, TextUtils.guidebook("chapter.blocks.p2l10")) }));

		pages.add(new Page(new ImageWrapperObject[] {
				//
				new ImageWrapperObject(12, 38, 0, 0, 150, 79, 150, 79, References.ID + ":textures/screen/guidebook/silo1.png"),
				//
				new ImageWrapperObject(12, 117, 0, 0, 150, 79, 150, 79, References.ID + ":textures/screen/guidebook/silo2.png") }));

		return pages;
	}

	@Override
	public ItemWrapperObject getLogo() {
		return LOGO;
	}

	@Override
	public MutableComponent getTitle() {
		return TextUtils.guidebook("chapter.blocks");
	}

}
