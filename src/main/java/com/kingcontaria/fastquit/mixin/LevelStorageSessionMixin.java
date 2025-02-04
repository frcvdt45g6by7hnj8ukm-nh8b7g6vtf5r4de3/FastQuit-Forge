package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.mixin.accessor.LevelInfoAccessor;
import com.kingcontaria.fastquit.mixin.accessor.LevelPropertiesAccessor;
import com.kingcontaria.fastquit.plugin.annotation.Synchronized;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.DirectoryLock;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class LevelStorageSessionMixin {

    @Shadow @Final private String levelId;

    @Synchronized
    @Shadow public abstract PlayerDataStorage createPlayerStorage();

    @Synchronized
    @Shadow public abstract @Nullable LevelSummary getSummary(Dynamic<?> p_310283_);

    @Synchronized
    @Shadow protected abstract @Nullable Dynamic<?> getDataTag(boolean p_310699_);

    @Synchronized
    @Shadow public abstract void saveDataTag(RegistryAccess registryManager, WorldData saveProperties, @Nullable CompoundTag nbt);

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
        SaveManager.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).ifPresent(SaveManager::wait);
    }

    @Inject(method = "renameLevel", at = @At("TAIL"))
    private void fastquit$editSavingWorldName(String name, CallbackInfo ci) {
        SaveManager.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).ifPresent(server -> ((LevelInfoAccessor) (Object) ((LevelPropertiesAccessor) server.getWorldData()).fastquit$getLevelInfo()).fastquit$setName(name));
    }

    @Inject(method = "deleteLevel", at = @At("TAIL"))
    private void fastquit$deleteSavingWorld(CallbackInfo ci) {
        SaveManager.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).map(SaveManager.savingWorlds::get).ifPresent(info -> info.deleted = true);
    }

    @WrapWithCondition(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/DirectoryLock;close()V"))
    private boolean fastquit$checkSessionClose(DirectoryLock lock) {
        return !SaveManager.occupiedSessions.remove((LevelStorageSource.LevelStorageAccess) (Object) this);
    }

    @Inject(method = "checkLock", at = @At("HEAD"))
    private void fastquit$warnIfUnSynchronizedSessionAccess(CallbackInfo ci) {
        if (!Thread.holdsLock(this)) {
            SaveManager.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).ifPresent(server -> {
                ModLogger.warn("Un-synchronized access to \"" + this.levelId + "\" session!");
                if (!server.isSameThread()) {
                    SaveManager.wait(server);
                }
            });
        }
    }
}