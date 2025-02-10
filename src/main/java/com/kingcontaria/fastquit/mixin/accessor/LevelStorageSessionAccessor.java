package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.server.SessionLockManager;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(ISaveFormat.class)
public interface LevelStorageSessionAccessor {
    @Accessor("lock")
    SessionLockManager fastquit$getLock();

    @Accessor("levelPath")
    Path fastquit$getDirectory();
}