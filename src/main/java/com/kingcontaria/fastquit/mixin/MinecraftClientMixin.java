package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.mixin.accessor.MinecraftServerAccessor;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.WorldInfo;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isServerStopped()Z"))
    private boolean fastquit(IntegratedServer server) {
        SaveManager.savingWorlds.put(server, new WorldInfo());

        if (ModConfig.backgroundPriority != 0) {
            ((MinecraftServerAccessor) server).fastquit$getThread().setPriority(ModConfig.backgroundPriority);
        }

        ModLogger.log("Disconnected \"" + server.getWorldName() + "\" from the client.");
        return true;
    }

    @WrapWithCondition(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/LoadingScreenRenderer;displayLoadingString(Ljava/lang/String;)V"), @At(value="INVOKE", target = "Lnet/minecraft/client/LoadingScreenRenderer;resetProgressAndMessage(Ljava/lang/String;)V")})
    private boolean fastquit$doNotOpenSaveScreen(LoadingScreenRenderer instance, String message) {
        return ModConfig.renderSavingScreen;
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;)V", shift = At.Shift.AFTER))
    private void fastquit$waitForSaveOnShutdown(CallbackInfo ci) {
        SaveManager.exit();
    }

    @Inject(method = "displayCrashReport", at = @At("HEAD"))
    private static void fastquit$waitForSaveOnCrash(CallbackInfo ci) {
        SaveManager.exit();
    }
}