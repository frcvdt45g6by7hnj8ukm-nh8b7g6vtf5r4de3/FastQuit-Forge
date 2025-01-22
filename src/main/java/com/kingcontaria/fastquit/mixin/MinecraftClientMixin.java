package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.FastQuit;
import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.TextHelper;
import com.kingcontaria.fastquit.util.WorldInfo;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Redirect(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;isShutdown()Z"))
    private boolean fastquit(IntegratedServer server) {
        SaveManager.savingWorlds.put(server, new WorldInfo());

        if (ModConfigManager.getConfig().backgroundPriority != 0) {
            server.getRunningThread().setPriority(ModConfigManager.getConfig().backgroundPriority);
        }

        ModLogger.log("Disconnected \"" + server.getWorldData().getLevelName() + "\" from the client.");
        return true;
    }

    @WrapWithCondition(method = "updateScreenAndTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTick(Z)V"))
    private boolean fastquit$doNotOpenSaveScreen(Minecraft client, boolean tick, Screen screen) {
        return ModConfigManager.getConfig().renderSavingScreen || !(screen instanceof GenericDirtMessageScreen && screen.getTitle().equals(TextHelper.translatable("menu.savingLevel")));
    }

    @Inject(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;clearLevel()V", shift = At.Shift.AFTER))
    private void fastquit$waitForSaveOnShutdown(CallbackInfo ci) {
        SaveManager.exit();
    }

    @Inject(method = "crash", at = @At("HEAD"))
    private static void fastquit$waitForSaveOnCrash(CallbackInfo ci) {
        SaveManager.exit();
    }
}