package ballistix.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import ballistix.References;
import ballistix.client.guidebook.chapters.ChapterBlocks;
import ballistix.client.guidebook.chapters.ChapterItems;
import electrodynamics.client.guidebook.utils.ImageWrapperObject;
import electrodynamics.client.guidebook.utils.components.Chapter;
import electrodynamics.client.guidebook.utils.components.Module;
import electrodynamics.prefab.utilities.TextUtils;
import net.minecraft.network.chat.MutableComponent;

public class ModuleBallistix extends Module {

	private static final ImageWrapperObject LOGO = new ImageWrapperObject(10, 38, 0, 0, 32, 32, 32, 32, References.ID + ":textures/screen/guidebook/ballistixlogo.png");

	@Override
	public ImageWrapperObject getLogo() {
		return LOGO;
	}

	@Override
	protected List<Chapter> genChapters() {
		List<Chapter> chapters = new ArrayList<>();
		chapters.add(new ChapterBlocks());
		chapters.add(new ChapterItems());
		return chapters;
	}

	@Override
	public MutableComponent getTitle() {
		return TextUtils.guidebook(References.ID);
	}

	@Override
	public boolean isFirst() {
		return false;
	}

}
