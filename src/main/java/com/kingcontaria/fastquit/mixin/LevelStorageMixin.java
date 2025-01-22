package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(LevelStorageSource.class)
public abstract class LevelStorageMixin {

    @Shadow @Final private Path baseDir;

    @Inject(method = "validateAndCreateAccess", at = @At("HEAD"))
    private void fastquit$waitForSaveOnSessionCreation(String levelName, CallbackInfoReturnable<LevelStorageSource.LevelStorageAccess> cir) {
        if (!ModConfigManager.getConfig().allowMultipleServers()) {
            SaveManager.wait(SaveManager.savingWorlds.keySet());
        }
        SaveManager.getSavingWorld(this.baseDir.resolve(levelName)).ifPresent(SaveManager::wait);
    }

    @Inject(method = "lambda$loadLevelSummaries$2(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelDirectory;)Lnet/minecraft/world/level/storage/LevelSummary;", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true)
    private void fastquit$addCurrentlySavingLevelsToWorldList(LevelStorageSource.LevelDirectory levelSave, CallbackInfoReturnable<LevelSummary> cir) {
        SaveManager.getSession(levelSave.path()).ifPresent(session -> {
            try (session) {
                cir.setReturnValue(session.getSummary());
            } catch (Exception e) {
                ModLogger.error("Failed to load level summary from saving server!", e);
            }
        });
    }
}