package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatOld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;
import java.nio.file.Path;

@Mixin(SaveFormatOld.class)
public interface LevelStorageSessionAccessor {
//    @Accessor("lock")
//    SessionLockManager fastquit$getLock();

    @Accessor("savesDirectory")
    File fastquit$getDirectory();
}