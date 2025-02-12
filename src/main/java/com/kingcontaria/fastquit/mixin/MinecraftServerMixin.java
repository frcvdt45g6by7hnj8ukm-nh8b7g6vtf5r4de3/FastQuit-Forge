package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.TextHelper;
import com.kingcontaria.fastquit.util.WorldInfo;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "systemExitNow", at = @At("RETURN"))
    private void fastquit$finishSaving(CallbackInfo ci) {
        //noinspection ConstantConditions
        if ((Object) this instanceof IntegratedServer) {
            IntegratedServer server = (IntegratedServer) (Object) this;
            WorldInfo info = SaveManager.savingWorlds.remove(server);

            if (info == null) {
                ModLogger.warn("\"" + server.getWorldName() + "\" was not registered in currently saving worlds!");
                return;
            }

            ITextComponent description = TextHelper.translatable("fastquit.toast." + (info.deleted ? "deleted" : "description"), server.getWorldName());
            if (ModConfig.showSavingTime && !info.deleted) {
                description.appendText(" (" + info.getTimeSaving() + ")");
            }
            if (ModConfig.showToasts) {
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().getToastGui().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, TextHelper.translatable("fastquit.toast.title"), description)));
            }
            ModLogger.log(description.getUnformattedText());
        }
    }

    @WrapWithCondition(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;saveAllPlayerData()V"))
    private boolean fastquit$cancelPlayerSavingIfDeleted(PlayerList playerManager) {
        if (this.fastQuit_Forge$isDeleted()) {
            LOGGER.info("Cancelled saving players because level was deleted");
            return false;
        }
        return true;
    }

    @Inject(method = "saveAllWorlds", at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;saveAllChunks(ZLnet/minecraft/util/IProgressUpdate;)V")}, cancellable = true)
    private void fastquit$cancelSavingIfDeleted(boolean isSilent, CallbackInfo cir) {
        if (this.fastQuit_Forge$isDeleted()) {
            LOGGER.info("Cancelled saving worlds because level was deleted");
            cir.cancel();
        }
    }

    @Unique
    private boolean fastQuit_Forge$isDeleted() {
        WorldInfo info = SaveManager.savingWorlds.get(this);
        return info != null && info.deleted;
    }
}