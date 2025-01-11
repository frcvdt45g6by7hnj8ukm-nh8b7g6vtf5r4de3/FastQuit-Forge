package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.FastQuit;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditWorldScreen.class)
public abstract class EditWorldScreenMixin {

    @Shadow @Final private LevelStorageSource.LevelStorageAccess levelAccess;

    @Inject(method = {"lambda$init$7", "lambda$init$5"}, at = @At("HEAD"), remap = false, cancellable = true)
    private void fastquit$waitForSaveOnBackupOrOptimizeWorld_cancellable(CallbackInfo ci) {
        FastQuit.getSavingWorld(this.levelAccess).ifPresent(server -> FastQuit.wait(server, ci));
    }
}