package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor("serverThread")
    Thread fastquit$getThread();
    @Accessor("anvilConverterForAnvilFile")
    ISaveFormat fastquit$getSession();
    @Accessor("folderName")
    String fastquit$getFolderName();
}
