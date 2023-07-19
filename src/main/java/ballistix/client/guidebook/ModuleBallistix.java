package ballistix.client.guidebook;

import ballistix.References;
import ballistix.client.guidebook.chapters.ChapterMissileSilo;
import ballistix.client.guidebook.chapters.ChapterItems;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.client.guidebook.utils.components.Module;
import electrodynamics.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ModuleBallistix extends Module {

	private static final ImageWrapperObject LOGO = new ImageWrapperObject(0, 0, 0, 0, 32, 32, 32, 32, new ResourceLocation(References.ID, "textures/screen/guidebook/ballistixlogo.png"));

	@Override
	public ImageWrapperObject getLogo() {
		return LOGO;
	}

	@Override
	public MutableComponent getTitle() {
		return BallistixTextUtils.guidebook(References.ID);
	}

	@Override
	public void addChapters() {
		chapters.add(new ChapterMissileSilo(this));
		chapters.add(new ChapterItems(this));
	}

}
