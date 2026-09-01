package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilSaveHandler.class)
public abstract class AnvilSaveHandlerMixin {
    @Redirect(method = "flush", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/RegionFileCache;clearRegionFileReferences()V"))
    private void fastquit$closeRegionFilesOnlyWhenIdle() {
        if (!SaveManager.shouldDeferRegionFileCacheCloseForCurrentThread()) {
            RegionFileCache.clearRegionFileReferences();
        }
    }
}
