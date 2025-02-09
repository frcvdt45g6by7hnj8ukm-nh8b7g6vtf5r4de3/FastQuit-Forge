package com.kingcontaria.fastquit.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.*;

/**
 * 用于简化移植到旧版Minecraft的工具类。
 * <p>
 * Utility class for ease of porting to older Minecraft versions.
 */
public final class TextHelper {

    public static final String OFF = I18n.format("options.off");
    public static final String BACK = I18n.format("gui.back");

    public static ITextComponent translatable(String key, Object... args) {
        return new TextComponentTranslation(key, args);
    }
}