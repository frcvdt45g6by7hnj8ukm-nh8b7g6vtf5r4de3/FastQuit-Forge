package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.client.gui.GuiWorldEdit;
import net.minecraft.client.gui.screen.EditWorldScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiWorldEdit.class)
public abstract class EditWorldScreenMixin {
//    @Inject(method = "lambda$init$5(Lnet/minecraft/client/gui/widget/button/Button;)V", at = @At(value = "HEAD"), cancellable = true)
//    private void fastquit$waitForSaveOnOptimize(Button p_214304_1_, CallbackInfo ci) {
////        ModLogger.log("I am optimize");
//        SaveManager.getSavingWorld(this.levelAccess).ifPresent(server -> SaveManager.wait(server, ci));
//    }

    @Inject(method = "lambda$init$2(Lnet/minecraft/client/gui/widget/button/Button;)V", at = @At(value = "HEAD"), cancellable = true)
    private void fastquit$waitForSaveOnBackup(Button p_101292_, CallbackInfo ci) {
//        ModLogger.log("I am backup");
        SaveManager.getSavingWorld(this.levelAccess).ifPresent(server -> SaveManager.wait(server, ci));
    }

//    @Inject(method = "makeBackupAndShowToast(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Z", at = @At(value = "HEAD"), cancellable = true)
//    private static void fastquit$waitForSaveOnBackup(LevelStorageSource.LevelStorageAccess levelAccess, CallbackInfoReturnable<Boolean> ci){
//        ModLogger.log("I am backup");
//        SaveManager.getSavingWorld(levelAccess).ifPresent(server -> SaveManager.wait(server, ci));
//    }
}