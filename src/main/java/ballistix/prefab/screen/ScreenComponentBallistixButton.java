package ballistix.prefab.screen;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import electrodynamics.api.screen.ITexture;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * A modification of the vanilla button to integrate it with the Electrodynamics system of doing GUI components as the
 * Button class has several annoying issues
 * 
 * @author skip999
 *
 * @param <T>
 */
public class ScreenComponentBallistixButton<T extends ScreenComponentBallistixButton<?>> extends ScreenComponentGeneric {

    public boolean isPressed = false;

    public final boolean isVanillaRender;
    @Nullable
    public OnPress onPress = null;

    @Nullable
    public Supplier<Component> label = null;

    public SoundEvent pressSound = SoundEvents.UI_BUTTON_CLICK.value();
    
    @Nullable
	public OnTooltip onTooltip = null;
    
    @Nullable
	public ITexture icon;

    public ScreenComponentBallistixButton(ITexture texture, int x, int y) {
        super(texture, x, y);
        isVanillaRender = false;
    }

    public ScreenComponentBallistixButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        isVanillaRender = true;
        this.width = width;
        this.height = height;
    }

    public T setLabel(Component label) {
        return setLabel(() -> label);
    }

    public T setLabel(Supplier<Component> label) {
        this.label = label;
        return (T) this;
    }

    public T setOnPress(OnPress onPress) {
        this.onPress = onPress;
        return (T) this;
    }

    public T onTooltip(OnTooltip onTooltip) {
        this.onTooltip = onTooltip;
        return (T) this;
    }

    public T setPressSound(SoundEvent sound) {
        pressSound = sound;
        return (T) this;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
    	if (isVanillaRender && isVisible()) {
			Minecraft minecraft = Minecraft.getInstance();

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			// RenderingUtils.bindTexture(AbstractWidget.WIDGETS_LOCATION);
			RenderingUtils.setShaderColor(color);
			int i = this.getVanillaYImage(isHovered());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			graphics.blit(AbstractWidget.WIDGETS_LOCATION, this.xLocation + guiWidth, this.yLocation + guiHeight, 0, 46 + i * 20, this.width / 2, this.height);
			graphics.blit(AbstractWidget.WIDGETS_LOCATION, this.xLocation + guiWidth + this.width / 2, this.yLocation + guiHeight, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			
			if (this.icon != null) {
                int xOffset = (this.width - this.icon.imageWidth()) / 2;
                int yOffset = (this.height - this.icon.imageHeight()) / 2;
                graphics.blit(this.icon.getLocation(), guiWidth + this.xLocation + xOffset, guiHeight + this.yLocation + yOffset, (float)this.icon.textureU(), (float)this.icon.textureV(), this.icon.textureWidth(), this.icon.textureHeight(), this.icon.imageWidth(), this.icon.imageHeight());
            }

			Font font = minecraft.font;
			if (label != null) {
				graphics.drawCenteredString(font, label.get(), this.xLocation + guiWidth + this.width / 2, this.yLocation + guiHeight + (this.height - 8) / 2, color.color());
			}

		} else {
            super.renderBackground(graphics, xAxis, yAxis, guiWidth, guiHeight);
            if(icon != null) {
    			int xOffset = (texture.imageWidth() - icon.imageWidth()) / 2;
    			int yOffset = (texture.imageHeight() - icon.imageHeight()) / 2;
    			graphics.blit(icon.getLocation(), guiWidth + xLocation + xOffset, guiHeight + yLocation + yOffset, icon.textureU(), icon.textureV(), icon.textureWidth(), icon.textureHeight(), icon.imageWidth(), icon.imageHeight());
    		}
        }
    	
    	
    }
    
    @Override
	public void renderForeground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		super.renderForeground(graphics, xAxis, yAxis, guiWidth, guiHeight);
		if (isVisible() && isHovered() && onTooltip != null) {
			onTooltip.onTooltip(graphics, this, xAxis, yAxis);
		}
	}

    public int getVanillaYImage(boolean isMouseOver) {
        if (!isVisible()) {
            return 0;
        }
        if (isMouseOver) {
            return 2;
        }

        return 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isActiveAndVisible() && isValidClick(button) && isInClickRegion(mouseX, mouseY)) {

            onMouseClick(mouseX, mouseY);

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isValidClick(button)) {
            onMouseRelease(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public void onMouseClick(double mouseX, double mouseY) {
        if (onPress != null) {
            onPress();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isActiveAndVisible()) {
            return false;
        }
        if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
            return false;
        }
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        this.onPress();
        return true;
    }

    public void onPress() {
        onPress.onPress(this);
        playDownSound(Minecraft.getInstance().getSoundManager());
    }

    public boolean isValidMouseClick(int button) {
        return button == 0;
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(pressSound, 1.0F));
    }
    
    public ScreenComponentBallistixButton<?> setIcon(ITexture icon) {
		this.icon = icon;
		return this;
	}

    public static interface OnPress {

        public void onPress(ScreenComponentBallistixButton<?> button);

    }
    public static interface OnTooltip {

		public void onTooltip(GuiGraphics graphics, ScreenComponentBallistixButton<?> button, int xAxis, int yAxis);

	}

}
