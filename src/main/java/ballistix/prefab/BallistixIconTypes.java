package ballistix.prefab;

import ballistix.References;
import electrodynamics.api.screen.ITexture;
import net.minecraft.resources.ResourceLocation;

public enum BallistixIconTypes implements ITexture {
    MISSILE_DARK(0, 0, 14, 14, 14, 14, new ResourceLocation(References.ID, "textures/screen/component/icon/missile_dark.png")),
    EXPLOSIVE_DARK(0, 0, 16, 16, 16, 16, new ResourceLocation(References.ID, "textures/screen/component/icon/explosive_dark.png")),
    TARGET(0, 0, 11, 11, 11, 11, new ResourceLocation(References.ID, "textures/screen/component/icon/target.png")),
    FREQUENCY(0, 0, 9, 15, 9, 15, new ResourceLocation(References.ID, "textures/screen/component/icon/frequency.png")),
    DELETE(0, 0, 8, 8, 8, 8, new ResourceLocation(References.ID, "textures/screen/component/icon/delete.png")),
    REDSTONE(0, 0, 16, 16, 16, 16, new ResourceLocation(References.ID, "textures/screen/component/icon/redstone.png")),
    TARGET_ENTITY(0, 0, 15, 15, 15, 15, new ResourceLocation(References.ID, "textures/screen/component/icon/target_entity.png")),
    TARGET_MISSILE(0, 0, 15, 15, 15, 15, new ResourceLocation(References.ID, "textures/screen/component/icon/target_missile.png")),
    PLAYER_WHITELIST(0, 0, 8, 8, 8, 8, new ResourceLocation(References.ID, "textures/screen/component/icon/playerwhitelist.png")),
    TARGET_ONLY_PLAYERS(0, 0, 8, 8, 8, 8, new ResourceLocation(References.ID, "textures/screen/component/icon/targetonlyplayers.png")), //
	SONAR_PROFILE(0, 0, 16, 16, 16, 16, new ResourceLocation(References.ID, "textures/screen/component/icon/sonarpattern.png"));

    private final int textU;
    private final int textV;
    private final int textWidth;
    private final int textHeight;
    private final int imgWidth;
    private final int imgHeight;
    private final ResourceLocation loc;

    private BallistixIconTypes(int textU, int textV, int textWidth, int textHeight, int imgWidth, int imgHeight, ResourceLocation loc) {
        this.textU = textU;
        this.textV = textV;
        this.textWidth = textWidth;
        this.textHeight = textHeight;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.loc = loc;
    }

    @Override
    public ResourceLocation getLocation() {
        return loc;
    }

    @Override
    public int imageHeight() {
        return imgHeight;
    }

    @Override
    public int imageWidth() {
        return imgWidth;
    }

    @Override
    public int textureHeight() {
        return textHeight;
    }

    @Override
    public int textureU() {
        return textU;
    }

    @Override
    public int textureV() {
        return textV;
    }

    @Override
    public int textureWidth() {
        return textWidth;
    }
}
