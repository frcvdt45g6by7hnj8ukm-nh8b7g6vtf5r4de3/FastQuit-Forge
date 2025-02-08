package com.kingcontaria.fastquit.util;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.util.text.*;

/**
 * 用于简化移植到旧版Minecraft的工具类。
 * <p>
 * Utility class for ease of porting to older Minecraft versions.
 */
public final class TextHelper {

    public static final ITextComponent OFF = DialogTexts.OPTION_OFF;
    public static final ITextComponent BACK = DialogTexts.GUI_BACK;

    public static ITextComponent translatable(String key, Object... args) {
        return new TranslationTextComponent(key, args);
    }

    public static ITextComponent literal(String string) {
        return ITextComponent.nullToEmpty(string);
    }
}