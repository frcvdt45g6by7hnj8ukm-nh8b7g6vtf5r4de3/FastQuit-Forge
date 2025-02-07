package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.util.DirectoryLock;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public interface LevelStorageSessionAccessor {
    @Accessor("lock")
    DirectoryLock fastquit$getLock();

    @Accessor("levelPath")
    Path fastquit$getDirectory();
}