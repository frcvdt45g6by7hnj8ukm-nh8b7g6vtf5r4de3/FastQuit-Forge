package com.kingcontaria.fastquit.mixin;

import net.minecraft.world.level.LevelSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelSettings.class)
public interface LevelInfoAccessor {
    @Mutable
    @Accessor("levelName")
    void fastquit$setName(String name);
}