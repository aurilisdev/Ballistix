package ballistix.prefab.screen;

import ballistix.Ballistix;
import net.minecraft.resources.ResourceLocation;
import voltaic.api.screen.ITexture;

public enum RadarTextures implements ITexture {
    FREQUENCY(18, 18, 0, 0, 18, 18, Ballistix.rl("textures/screen/component/radar/frequency.png"));

    private final int textureWidth;
    private final int textureHeight;
    private final int textureU;
    private final int textureV;
    private final int imageWidth;
    private final int imageHeight;
    private final ResourceLocation loc;

    private RadarTextures(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth,
	    int imageHeight, ResourceLocation loc) {
	this.textureWidth = textureWidth;
	this.textureHeight = textureHeight;
	this.textureU = textureU;
	this.textureV = textureV;
	this.imageWidth = imageWidth;
	this.imageHeight = imageHeight;
	this.loc = loc;
    }

    @Override
    public ResourceLocation getLocation() {
	return loc;
    }

    @Override
    public int imageHeight() {
	return imageHeight;
    }

    @Override
    public int imageWidth() {
	return imageWidth;
    }

    @Override
    public int textureHeight() {
	return textureHeight;
    }

    @Override
    public int textureU() {
	return textureU;
    }

    @Override
    public int textureV() {
	return textureV;
    }

    @Override
    public int textureWidth() {
	return textureWidth;
    }

}
