package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.FastQuit;
import com.kingcontaria.fastquit.util.SaveManager;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptimizeWorldScreen.class)
public abstract class OptimizeWorldScreenMixin {

    // this now acts as a fallback in case the method gets called from somewhere else than EditWorldScreen
    // 现在它充当回退机制，以防方法从 EditWorldScreen 以外的地方被调用。

    @Inject(method = "create", at = @At("HEAD"))
    private static void fastquit$waitForSaveOnOptimizeWorld(Minecraft client, BooleanConsumer callback, DataFixer dataFixer, LevelStorageSource.LevelStorageAccess session, boolean eraseCache, CallbackInfoReturnable<OptimizeWorldScreen> cir) {
        SaveManager.getSavingWorld(session).ifPresent(SaveManager::wait);
    }
}