package ballistix.client.guidebook;

import ballistix.Ballistix;
import ballistix.client.guidebook.chapters.ChapterExplosives;
import ballistix.client.guidebook.chapters.ChapterItems;
import ballistix.client.guidebook.chapters.ChapterMisc;
import ballistix.client.guidebook.chapters.ChapterMissileDefense;
import ballistix.client.guidebook.chapters.ChapterMissileOffense;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.network.chat.MutableComponent;
import voltaic.client.guidebook.utils.components.Module;
import voltaic.client.guidebook.utils.pagedata.graphics.ImageWrapperObject;

public class ModuleBallistix extends Module {

	private static final ImageWrapperObject LOGO = new ImageWrapperObject(0, 0, 0, 0, 32, 32, 32, 32, Ballistix.rl("textures/screen/guidebook/ballistixlogo.png"));

	@Override
	public ImageWrapperObject getLogo() {
		return LOGO;
	}

	@Override
	public MutableComponent getTitle() {
		return BallistixTextUtils.guidebook(Ballistix.ID);
	}

	@Override
	public void addChapters() {
		chapters.add(new ChapterExplosives(this));
		chapters.add(new ChapterMissileOffense(this));
		chapters.add(new ChapterItems(this));
		chapters.add(new ChapterMissileDefense(this));
		chapters.add(new ChapterMisc(this));
	}

}
