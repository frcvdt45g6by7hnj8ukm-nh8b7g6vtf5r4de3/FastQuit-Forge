package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.WorldInfo;
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

        if (ModConfigManager.getConfig().backgroundPriority != 0) {
            server.getServerThread().setPriority(ModConfigManager.getConfig().backgroundPriority);
        }

        ModLogger.log("Disconnected \"" + server.getWorldName() + "\" from the client.");
        return true;
    }

//    @WrapWithCondition(method = "updateScreenAndTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTick(Z)V"))
//    private boolean fastquit$doNotOpenSaveScreen(Minecraft client, boolean tick, Screen screen) {
//        return ModConfigManager.getConfig().renderSavingScreen || !(screen instanceof DirtMessageScreen && screen.getTitle().equals(TextHelper.translatable("menu.savingLevel")));
//    }

    @Inject(method = "shutdownMinecraftApplet", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;)V", shift = At.Shift.AFTER))
    private void fastquit$waitForSaveOnShutdown(CallbackInfo ci) {
        SaveManager.exit();
    }

    @Inject(method = "displayCrashReport", at = @At("HEAD"))
    private static void fastquit$waitForSaveOnCrash(CallbackInfo ci) {
        SaveManager.exit();
    }
}