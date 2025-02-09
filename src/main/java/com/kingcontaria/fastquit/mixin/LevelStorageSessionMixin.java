package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.mixin.accessor.LevelInfoAccessor;
import com.kingcontaria.fastquit.mixin.accessor.LevelPropertiesAccessor;
import com.kingcontaria.fastquit.plugin.annotation.Synchronized;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.server.SessionLockManager;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.PlayerData;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.io.IOException;

@Mixin(SaveFormat.LevelSave.class)
public abstract class LevelStorageSessionMixin {

    @Shadow @Final private String levelId;

    @Synchronized
    @Shadow public abstract PlayerData createPlayerStorage();

    @Synchronized
    @Shadow public abstract @Nullable WorldSummary getSummary();

    @Synchronized
    @Shadow public abstract @Nullable IServerConfiguration getDataTag(DynamicOps<INBT> pNbt, DatapackCodec pDatapackCodec);
    @Synchronized
    @Shadow public abstract @Nullable DatapackCodec getDataPacks();

    @Synchronized
    @Shadow public abstract void saveDataTag(DynamicRegistries pRegistries, IServerConfiguration pServerConfiguration, @Nullable CompoundNBT pHostPlayerNBT);

    @Synchronized
    @Shadow public abstract void deleteLevel() throws IOException;

    @Synchronized
    @Shadow public abstract void renameLevel(String name) throws IOException;

    @Synchronized
    @Shadow public abstract void close() throws IOException;


    // this now acts as a fallback in case the method gets called from somewhere else than EditWorldScreen
    // 现在它充当回退机制，以防方法从 EditWorldScreen 以外的地方被调用。

    @Inject(method = "makeWorldBackup", at = @At("HEAD"))
    private void fastquit$waitForSaveOnBackup(CallbackInfoReturnable<Long> cir) {
        SaveManager.getSavingWorld((SaveFormat.LevelSave) (Object) this).ifPresent(SaveManager::wait);
    }

    @Inject(method = "renameLevel", at = @At("TAIL"))
    private void fastquit$editSavingWorldName(String name, CallbackInfo ci) {
        SaveManager.getSavingWorld((SaveFormat.LevelSave) (Object) this).ifPresent(server -> ((LevelInfoAccessor) (Object) ((LevelPropertiesAccessor) server.getWorldData()).fastquit$getLevelInfo()).fastquit$setName(name));
    }

    @Inject(method = "deleteLevel", at = @At("TAIL"))
    private void fastquit$deleteSavingWorld(CallbackInfo ci) {
        SaveManager.getSavingWorld((SaveFormat.LevelSave) (Object) this).map(SaveManager.savingWorlds::get).ifPresent(info -> info.deleted = true);
    }

    @Redirect(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/SessionLockManager;close()V"))
    private void fastquit$checkSessionClose(SessionLockManager instance) throws IOException{
        if(!SaveManager.occupiedSessions.remove((SaveFormat.LevelSave) (Object) this)){
            instance.close();
        }
    }

    @Inject(method = "checkLock", at = @At("HEAD"))
    private void fastquit$warnIfUnSynchronizedSessionAccess(CallbackInfo ci) {
        if (!Thread.holdsLock(this)) {
            SaveManager.getSavingWorld((SaveFormat.LevelSave) (Object) this).ifPresent(server -> {
                ModLogger.warn("Un-synchronized access to \"" + this.levelId + "\" session!");
                if (!server.isSameThread()) {
                    SaveManager.wait(server);
                }
            });
        }
    }
}