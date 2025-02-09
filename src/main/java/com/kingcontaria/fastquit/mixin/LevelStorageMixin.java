package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Mixin(SaveFormat.class)
public abstract class LevelStorageMixin {

    @Shadow
    @Final
    private Path baseDir;

    @Inject(method = "createAccess", at = @At("HEAD"))
    private void fastquit$waitForSaveOnSessionCreation(String pSaveName, CallbackInfoReturnable<SaveFormat.LevelSave> cir) {
        if (!ModConfigManager.getConfig().allowMultipleServers()) {
            SaveManager.wait(SaveManager.savingWorlds.keySet());
        }
        SaveManager.getSavingWorld(this.baseDir.resolve(pSaveName)).ifPresent(SaveManager::wait);
    }

    @Inject(method = "getLevelList", at = @At(value = "CONSTANT", args = "stringValue=Failed to read {} lock"), cancellable = true)
    private void fastquit$addCurrentlySavingLevelsToWorldList(CallbackInfoReturnable<List<WorldSummary>> cir, @Local List<WorldSummary> worldSummaries, @Local File file1) {
        SaveManager.getSession(file1.toPath()).ifPresent(session -> {
            try {
                worldSummaries.add(session.getSummary());
                cir.setReturnValue(worldSummaries);
            } catch (Exception e) {
                ModLogger.error("Failed to load level summary from saving server!", e);
            } finally {
                try {
                    session.close(); // 手动关闭
                } catch (Exception e) {
                    ModLogger.error("Failed to close session!", e);
                }
            }
        });
    }
}