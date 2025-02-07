package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.mojang.datafixers.DataFixer;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Mixin(LevelStorageSource.class)
public abstract class LevelStorageMixin {

    @Shadow
    @Final
    private Path baseDir;

    @Inject(method = "createAccess", at = @At("HEAD"))
    private void fastquit$waitForSaveOnSessionCreation(String levelName, CallbackInfoReturnable<LevelStorageSource.LevelStorageAccess> cir) {
        if (!ModConfigManager.getConfig().allowMultipleServers()) {
            SaveManager.wait(SaveManager.savingWorlds.keySet());
        }
        SaveManager.getSavingWorld(this.baseDir.resolve(levelName)).ifPresent(SaveManager::wait);
    }

    @Inject(method = "getLevelList", at = @At(value = "CONSTANT", args = "stringValue=Failed to read {} lock"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void fastquit$addCurrentlySavingLevelsToWorldList(CallbackInfoReturnable<List<LevelSummary>> cir, List<LevelSummary> levelSummaryList, File[] afile, File[] var3, int var4, int var5, File file1) {
        SaveManager.getSession(file1.toPath()).ifPresent(session -> {
            try (session) {
                levelSummaryList.add(session.getSummary());
                cir.setReturnValue(levelSummaryList);
            } catch (Exception e) {
                ModLogger.error("Failed to load level summary from saving server!", e);
            }
        });
    }
}