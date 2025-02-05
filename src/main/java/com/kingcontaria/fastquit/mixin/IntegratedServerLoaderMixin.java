package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class)
public abstract class IntegratedServerLoaderMixin {

    @Shadow @Final private LevelStorageSource levelSource;

    @Inject(method = "openWorld(Ljava/lang/String;Ljava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void fastquit$waitForSaveOnWorldLoad_cancellable(String levelName, Runnable onCancel, CallbackInfo ci) {
        SaveManager.getSavingWorld(this.levelSource.getBaseDir().resolve(levelName)).ifPresent(server -> SaveManager.wait(server, ci));
        if (ci.isCancelled()) {
            onCancel.run();
        }
    }
}