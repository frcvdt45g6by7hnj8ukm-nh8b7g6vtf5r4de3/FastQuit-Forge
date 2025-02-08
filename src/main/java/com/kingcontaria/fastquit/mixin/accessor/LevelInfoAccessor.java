package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldSettings.class)
public interface LevelInfoAccessor {
    @Mutable
    @Accessor("levelName")
    void fastquit$setName(String name);
}