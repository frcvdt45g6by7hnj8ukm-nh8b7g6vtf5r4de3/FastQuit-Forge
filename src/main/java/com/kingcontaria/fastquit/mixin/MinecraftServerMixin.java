package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.TextHelper;
import com.kingcontaria.fastquit.util.WorldInfo;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "onServerExit", at = @At("RETURN"))
    private void fastquit$finishSaving(CallbackInfo ci) {
        //noinspection ConstantConditions
        if ((Object) this instanceof IntegratedServer) {
            IntegratedServer server = (IntegratedServer) (Object) this;
            WorldInfo info = SaveManager.savingWorlds.remove(server);

            if (info == null) {
                ModLogger.warn("\"" + server.getWorldData().getLevelName() + "\" was not registered in currently saving worlds!");
                return;
            }

            ITextComponent description = TextHelper.translatable("fastquit.toast." + (info.deleted ? "deleted" : "description"), server.getWorldData().getLevelName());
            if (ModConfigManager.getConfig().showSavingTime != ModConfig.ShowSavingTime.FALSE && !info.deleted) {
                description.copy().append(" (" + info.getTimeSaving() + ")");
            }
            if (ModConfigManager.getConfig().showToasts) {
                Minecraft.getInstance().submit(() -> Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.Type.WORLD_BACKUP, TextHelper.translatable("fastquit.toast.title"), description)));
            }
            ModLogger.log(description.getString());
        }
    }

    @Redirect(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;saveAll()V"))
    private void fastquit$cancelPlayerSavingIfDeleted(PlayerList playerManager) {
        if (this.fastQuit_Forge$isDeleted()) {
            LOGGER.info("Cancelled saving players because level was deleted");
            return;
        }
        playerManager.saveAll();
    }

    @Inject(method = "saveAllChunks", at = {@At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"), @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/SaveFormat$LevelSave;saveDataTag(Lnet/minecraft/util/registry/DynamicRegistries;Lnet/minecraft/world/storage/IServerConfiguration;Lnet/minecraft/nbt/CompoundNBT;)V")}, cancellable = true)
    private void fastquit$cancelSavingIfDeleted(CallbackInfoReturnable<Boolean> cir) {
        if (this.fastQuit_Forge$isDeleted()) {
            LOGGER.info("Cancelled saving worlds because level was deleted");
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean fastQuit_Forge$isDeleted() {
        WorldInfo info = SaveManager.savingWorlds.get(this);
        return info != null && info.deleted;
    }
}