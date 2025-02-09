package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.TextHelper;
import com.kingcontaria.fastquit.util.WorldInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow protected abstract void runTick(boolean p_195542_1_);

    @Redirect(method = "clearLevel(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isShutdown()Z"))
    private boolean fastquit(IntegratedServer server) {
        SaveManager.savingWorlds.put(server, new WorldInfo());

        if (ModConfigManager.getConfig().backgroundPriority != 0) {
            server.getRunningThread().setPriority(ModConfigManager.getConfig().backgroundPriority);
        }

        ModLogger.log("Disconnected \"" + server.getWorldData().getLevelName() + "\" from the client.");
        return true;
    }

    @Redirect(method = "updateScreenAndTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTick(Z)V"))
    private void fastquit$doNotOpenSaveScreen(Minecraft client, boolean tick, Screen screen) {
        if (ModConfigManager.getConfig().renderSavingScreen || !(screen instanceof DirtMessageScreen && screen.getTitle().equals(TextHelper.translatable("menu.savingLevel")))){
            this.runTick(false);
        }
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