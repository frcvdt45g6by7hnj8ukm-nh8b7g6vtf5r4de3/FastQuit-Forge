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

    @Inject(method = {"lambda$init$8(Lnet/minecraft/client/gui/components/Button;)V", "lambda$init$5(Lnet/minecraft/client/gui/components/Button;)V"}, at = @At(value = "HEAD"), cancellable = true)
    private void fastquit$waitForSaveOnBackup(CallbackInfo ci) {
        FastQuit.getSavingWorld(this.levelAccess).ifPresent(server -> FastQuit.wait(server, ci));
    }
}