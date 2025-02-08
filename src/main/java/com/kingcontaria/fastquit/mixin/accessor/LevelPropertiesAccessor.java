package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ServerWorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerWorldInfo.class)
public interface LevelPropertiesAccessor {
    @Accessor("settings")
    WorldSettings fastquit$getLevelInfo();
}