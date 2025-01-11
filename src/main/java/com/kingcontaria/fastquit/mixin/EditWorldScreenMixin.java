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

    @Inject(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/EditWorldScreen;makeBackupAndShowToast(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Z", ordinal = 0), cancellable = true)
    private void fastquit$waitForSaveOnBackup(CallbackInfo ci) {
        FastQuit.getSavingWorld(this.levelAccess).ifPresent(server -> FastQuit.wait(server, ci));
    }

    @Inject(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 0), cancellable = true)
    private void fastquit$waitForSaveOnOptimizeWorld(CallbackInfo ci) {
        FastQuit.getSavingWorld(this.levelAccess).ifPresent(server -> FastQuit.wait(server, ci));
    }
}