package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditWorldScreen.class)
public interface EditWorldScreenAccessor {
    @Mutable
    @Accessor("levelAccess")
    LevelStorageSource.LevelStorageAccess getLevelAccess();
}
