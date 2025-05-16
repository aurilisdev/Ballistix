package ballistix.prefab.screen;

import ballistix.Ballistix;
import net.minecraft.util.ResourceLocation;
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

    private RadarTextures(int textureWidth, int textureHeight, int textureU, int textureV, int imageWidth, int imageHeight, ResourceLocation loc) {
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
        return this.loc;
    }

    @Override
    public int imageHeight() {
        return this.imageHeight;
    }

    @Override
    public int imageWidth() {
        return this.imageWidth;
    }

    @Override
    public int textureHeight() {
        return this.textureHeight;
    }

    @Override
    public int textureU() {
        return this.textureU;
    }

    @Override
    public int textureV() {
        return this.textureV;
    }

    @Override
    public int textureWidth() {
        return this.textureWidth;
    }

}
