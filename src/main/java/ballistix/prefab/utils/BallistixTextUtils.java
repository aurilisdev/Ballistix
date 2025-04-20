package ballistix.prefab.utils;

import ballistix.Ballistix;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BallistixTextUtils {

	public static final String GUI_BASE = "gui";
	public static final String TOOLTIP_BASE = "tooltip";
	public static final String JEI_BASE = "jei";
	public static final String GUIDEBOOK_BASE = "guidebook";
	public static final String MESSAGE_BASE = "chat";
	public static final String JEI_INFO_ITEM = "info.item";
	public static final String JEI_INFO_FLUID = "info.fluid";
	public static final String BLOCK_BASE = "block";
	public static final String GAS_BASE = "gas";
	public static final String ADVANCEMENT_BASE = "advancement";
	public static final String DIMENSION = "dimension";
	public static final String CREATIVE_TAB = "creativetab";

	public static MutableComponent tooltip(String key, Object... additional) {
		return translated(TOOLTIP_BASE, key, additional);
	}

	public static MutableComponent guidebook(String key, Object... additional) {
		return translated(GUIDEBOOK_BASE, key, additional);
	}

	public static MutableComponent gui(String key, Object... additional) {
		return translated(GUI_BASE, key, additional);
	}

	public static MutableComponent chatMessage(String key, Object... additional) {
		return translated(MESSAGE_BASE, key, additional);
	}

	public static MutableComponent dimension(String key, Object... additional) {
		return translated("dimension", key, additional);
	}

	public static MutableComponent jeiTranslated(String key, Object... additional) {
		return Component.translatable(JEI_BASE + "." + key, additional);
	}

	public static MutableComponent jeiItemTranslated(String key, Object... additional) {
		return jeiTranslated(JEI_INFO_ITEM + "." + key, additional);
	}

	public static MutableComponent jeiFluidTranslated(String key, Object... additional) {
		return jeiTranslated(JEI_INFO_FLUID + "." + key, additional);
	}

	public static MutableComponent block(String key, Object... additional) {
		return translated(BLOCK_BASE, key, additional);
	}

	public static MutableComponent creativeTab(String key, Object... additional) {
		return translated(CREATIVE_TAB, key, additional);
	}

	public static MutableComponent translated(String base, String key, Object... additional) {
		return Component.translatable(base + "." + Ballistix.ID + "." + key, additional);
	}

	public static boolean dimensionExists(String key) {
		return translationExists("dimension", key);
	}

	public static boolean guiExists(String key) {
		return translationExists(GUI_BASE, key);
	}

	public static boolean tooltipExists(String key) {
		return translationExists(TOOLTIP_BASE, key);
	}

	public static boolean translationExists(String base, String key) {
		return I18n.exists(base + "." + Ballistix.ID + "." + key);
	}

}
