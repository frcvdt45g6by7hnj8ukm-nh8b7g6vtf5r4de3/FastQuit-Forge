package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor("storageSource")
    SaveFormat.LevelSave fastquit$getSession();
}