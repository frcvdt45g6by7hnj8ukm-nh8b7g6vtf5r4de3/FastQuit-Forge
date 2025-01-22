package com.kingcontaria.fastquit.plugin;

import org.spongepowered.asm.mixin.Shadow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Used to annotate {@link Shadow}'d methods to make them synchronized in {@link FastQuitMixinConfigPlugin#postApply}.
 * <p>
 * 用于标注 {@link Shadow} 的方法，以便在 {@link FastQuitMixinConfigPlugin#postApply} 中将它们设为同步方法。
 */
@Target(ElementType.METHOD)
public @interface Synchronized {
}