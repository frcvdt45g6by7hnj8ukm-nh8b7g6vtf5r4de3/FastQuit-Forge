package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.FastQuit;
import com.kingcontaria.fastquit.plugin.Synchronized;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
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
    @Shadow public abstract @Nullable LevelSummary getSummary();

    @Synchronized
    @Shadow public abstract @Nullable Pair<WorldData, WorldDimensions.Complete> getDataTag(DynamicOps<Tag> ops, WorldDataConfiguration dataConfiguration, Registry<LevelStem> dimensionOptionsRegistry, Lifecycle lifecycle);

    @Synchronized
    @Shadow public abstract @Nullable WorldDataConfiguration getDataConfiguration();

    @Synchronized
    @Shadow public abstract void saveDataTag(RegistryAccess registryManager, WorldData saveProperties, @Nullable CompoundTag nbt);

    @Synchronized
    @Shadow public abstract void deleteLevel() throws IOException;

    @Synchronized
    @Shadow public abstract void renameLevel(String name) throws IOException;

    @Synchronized
    @Shadow public abstract void close() throws IOException;


    // this now acts as a fallback in case the method gets called from somewhere else than EditWorldScreen
    @Inject(method = "makeWorldBackup", at = @At("HEAD"))
    private void fastquit$waitForSaveOnBackup(CallbackInfoReturnable<Long> cir) {
        FastQuit.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).ifPresent(FastQuit::wait);
    }

    @Inject(method = "renameLevel", at = @At("TAIL"))
    private void fastquit$editSavingWorldName(String name, CallbackInfo ci) {
        FastQuit.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).ifPresent(server -> ((LevelInfoAccessor) (Object) ((LevelPropertiesAccessor) server.getWorldData()).fastquit$getLevelInfo()).fastquit$setName(name));
    }

    @Inject(method = "deleteLevel", at = @At("TAIL"))
    private void fastquit$deleteSavingWorld(CallbackInfo ci) {
        FastQuit.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).map(FastQuit.savingWorlds::get).ifPresent(info -> info.deleted = true);
    }

    @WrapWithCondition(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/DirectoryLock;close()V"))
    private boolean fastquit$checkSessionClose(DirectoryLock lock) {
        return !FastQuit.occupiedSessions.remove((LevelStorageSource.LevelStorageAccess) (Object) this);
    }

    @Inject(method = "checkLock", at = @At("HEAD"))
    private void fastquit$warnIfUnSynchronizedSessionAccess(CallbackInfo ci) {
        if (!Thread.holdsLock(this)) {
            FastQuit.getSavingWorld((LevelStorageSource.LevelStorageAccess) (Object) this).ifPresent(server -> {
                FastQuit.warn("Un-synchronized access to \"" + this.levelId + "\" session!");
                if (!server.isSameThread()) {
                    FastQuit.wait(server);
                }
            });
        }
    }
}