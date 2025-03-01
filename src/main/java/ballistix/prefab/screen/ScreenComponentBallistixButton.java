package ballistix.prefab.screen;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import electrodynamics.api.screen.ITexture;
import electrodynamics.prefab.screen.component.types.ScreenComponentGeneric;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;

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
    public Supplier<IFormattableTextComponent> label = null;

    public SoundEvent pressSound = SoundEvents.UI_BUTTON_CLICK;
    
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

    public T setLabel(IFormattableTextComponent label) {
        return setLabel(() -> label);
    }

    public T setLabel(Supplier<IFormattableTextComponent> label) {
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
    public void renderBackground(MatrixStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
    	if (isVanillaRender && isVisible()) {
			Minecraft minecraft = Minecraft.getInstance();

			RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
			// RenderingUtils.bindTexture(AbstractWidget.WIDGETS_LOCATION);
			RenderingUtils.setShaderColor(color);
			int i = this.getVanillaYImage(isHovered());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			minecraft.getTextureManager().bind(WIDGETS_LOCATION);
			blit(stack, this.x + guiWidth, this.y + guiHeight, 0, 46 + i * 20, this.width / 2, this.height);
			blit(stack, this.x + guiWidth + this.width / 2, this.y + guiHeight, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			
			if (this.icon != null) {
                int xOffset = (this.width - this.icon.imageWidth()) / 2;
                int yOffset = (this.height - this.icon.imageHeight()) / 2;
                RenderingUtils.bindTexture(this.icon.getLocation());
                blit(stack, guiWidth + this.x + xOffset, guiHeight + this.y + yOffset, (float)this.icon.textureU(), (float)this.icon.textureV(), this.icon.textureWidth(), this.icon.textureHeight(), this.icon.imageWidth(), this.icon.imageHeight());
            }

			FontRenderer font = minecraft.font;
			if (label != null) {
				drawCenteredString(stack, font, label.get(), this.x + guiWidth + this.width / 2, this.y + guiHeight + (this.height - 8) / 2, color.color());
			}

		} else {
            super.renderBackground(stack, xAxis, yAxis, guiWidth, guiHeight);
            if(icon != null) {
    			int xOffset = (texture.imageWidth() - icon.imageWidth()) / 2;
    			int yOffset = (texture.imageHeight() - icon.imageHeight()) / 2;
    			RenderingUtils.bindTexture(this.icon.getLocation());
    			blit(stack, guiWidth + x + xOffset, guiHeight + y + yOffset, icon.textureU(), icon.textureV(), icon.textureWidth(), icon.textureHeight(), icon.imageWidth(), icon.imageHeight());
    		}
        }
    	
    	
    }
    
    @Override
	public void renderForeground(MatrixStack stack, int xAxis, int yAxis, int guiWidth, int guiHeight) {
		super.renderForeground(stack, xAxis, yAxis, guiWidth, guiHeight);
		if (isVisible() && isHovered() && onTooltip != null) {
			onTooltip.onTooltip(stack, this, xAxis, yAxis);
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

    public void playDownSound(SoundHandler soundManager) {
        soundManager.play(SimpleSound.forUI(pressSound, 1.0F));
    }
    
    public ScreenComponentBallistixButton<?> setIcon(ITexture icon) {
		this.icon = icon;
		return this;
	}

    public static interface OnPress {

        public void onPress(ScreenComponentBallistixButton<?> button);

    }
    public static interface OnTooltip {

		public void onTooltip(MatrixStack stack, ScreenComponentBallistixButton<?> button, int xAxis, int yAxis);

	}

}
