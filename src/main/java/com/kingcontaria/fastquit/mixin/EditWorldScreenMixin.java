package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.client.gui.components.Button;
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
    @Inject(method = "lambda$init$6(Lnet/minecraft/client/gui/components/Button;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"), cancellable = true)
    private void fastquit$waitForSaveOnOptimize(Button p_101292_, CallbackInfo ci) {
        ModLogger.log("I am optimize");
        SaveManager.getSavingWorld(this.levelAccess).ifPresent(server -> SaveManager.wait(server, ci));
    }

    @Inject(method = "lambda$init$3(Lnet/minecraft/client/gui/components/Button;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/EditWorldScreen;makeBackupAndShowToast(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Z"), cancellable = true)
    private void fastquit$waitForSaveOnBackup(Button p_101292_, CallbackInfo ci) {
        ModLogger.log("I am backup");
        SaveManager.getSavingWorld(this.levelAccess).ifPresent(server -> SaveManager.wait(server, ci));
    }
}