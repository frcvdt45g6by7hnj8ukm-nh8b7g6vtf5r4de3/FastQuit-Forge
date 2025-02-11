package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Mixin(SaveHandler.class)
public class SaveHandlerMixin {
    @Shadow @Final private String saveDirectoryName;

    @Inject(method = "checkSessionLock", at = @At("HEAD"))
    private void fastquit$warnIfUnSynchronizedSessionAccess(CallbackInfo ci) {
        if (!Thread.holdsLock(this)) {
            SaveManager.getSavingWorld((ISaveHandler) this, this.saveDirectoryName).ifPresent(server -> {
                ModLogger.warn("Un-synchronized access to \"" + this.saveDirectoryName + "\" session!");
                if (Thread.currentThread() != server.getServerThread()) {
                    SaveManager.wait(server);
                }
            });
        }
    }

    @WrapWithCondition(method = "setSessionLock", at = @At(value = "INVOKE", target = "Ljava/io/DataOutputStream;close()V"))
    private boolean fastquit$checkSessionClose(DataOutputStream instance) {
        return !SaveManager.occupiedSessions.remove((ISaveFormat) this);
    }

    @WrapWithCondition(method = "checkSessionLock", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;close()V"))
    private boolean fastquit$checkSessionClose(DataInputStream instance) {
        return !SaveManager.occupiedSessions.remove((ISaveFormat) this);
    }
}
