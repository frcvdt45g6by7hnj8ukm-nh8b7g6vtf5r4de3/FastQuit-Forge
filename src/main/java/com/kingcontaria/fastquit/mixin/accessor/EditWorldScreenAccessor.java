package com.kingcontaria.fastquit.mixin.accessor;

import net.minecraft.client.gui.screen.EditWorldScreen;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditWorldScreen.class)
public interface EditWorldScreenAccessor {
    @Mutable
    @Accessor("levelAccess")
    SaveFormat.LevelSave getLevelAccess();
}
