package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.plugin.annotation.Synchronized;
import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.world.storage.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.List;

@Mixin(SaveFormatOld.class)
public abstract class SaveFormatOldMixin {
    @Synchronized
    @Shadow public abstract List<WorldSummary> getSaveList();
    @Synchronized
    @Shadow public abstract WorldInfo getWorldInfo(String saveName);
    @Synchronized
    @Shadow public abstract void renameWorld(String dirName, String newName);
    @Synchronized
    @Shadow public abstract ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata);
    @Synchronized
    @Shadow public abstract boolean isNewLevelIdAcceptable(String saveName);
    @Synchronized
    @Shadow public abstract boolean deleteWorldDirectory(String saveName);

    @Shadow @Final public File savesDirectory;

    @Inject(method = "renameWorld", at = @At("TAIL"))
    private void fastquit$editSavingWorldName(String dirName, String newName, CallbackInfo ci) {
        SaveManager.getSavingWorld((ISaveFormat) this).ifPresent(server -> server.setWorldName(newName));
    }

    @Inject(method = "deleteWorldDirectory", at = @At("TAIL"))
    private void fastquit$deleteSavingWorld(String saveName, CallbackInfoReturnable<Boolean> cir) {
        SaveManager.getSavingWorld((ISaveFormat) this).map(SaveManager.savingWorlds::get).ifPresent(info -> info.deleted = true);
    }

    @Inject(method = "getSaveLoader", at = @At("HEAD"))
    public void fastquit$waitForSaveOnSessionCreation(String saveName, boolean storePlayerdata, CallbackInfoReturnable<ISaveHandler> cir){
        if (!ModConfig.allowMultipleServers()) {
            SaveManager.wait(SaveManager.snapshotSavingWorlds());
        }
        SaveManager.getSavingWorld(this.savesDirectory.toPath().resolve(saveName)).ifPresent(SaveManager::wait);
    }
}
