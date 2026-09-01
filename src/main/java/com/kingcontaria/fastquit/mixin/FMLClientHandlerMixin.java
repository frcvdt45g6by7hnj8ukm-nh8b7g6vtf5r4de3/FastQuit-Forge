package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Keeps Forge's server lookup valid only on the background integrated-server thread. */
@Mixin(value = FMLClientHandler.class, remap = false)
public abstract class FMLClientHandlerMixin {
    @Inject(method = "getServer", at = @At("HEAD"), cancellable = true, remap = false)
    private void fastquit$getBackgroundServer(CallbackInfoReturnable<MinecraftServer> cir) {
        SaveManager.getSavingServerForCurrentThread().ifPresent(cir::setReturnValue);
    }
}
