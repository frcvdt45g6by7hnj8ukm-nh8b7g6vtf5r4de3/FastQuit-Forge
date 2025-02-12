package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.screen.WaitingScreen;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.TextHelper;
import com.kingcontaria.fastquit.util.WorldInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiListWorldSelectionEntry.class)
public abstract class WorldListWidgetWorldEntryMixin {

    @Shadow @Final private Minecraft client;
    @Shadow @Final private WorldSummary worldSummary;

    @WrapOperation(method = "recreateWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSaveLoader()Lnet/minecraft/world/storage/ISaveFormat;"))
    private ISaveFormat fastquit$editSavingWorld(Minecraft instance, Operation<ISaveFormat> original) {
        return SaveManager.getSession(instance.getSaveLoader().getSaveLoader(this.worldSummary.getFileName(), false).getWorldDirectory().toPath()).orElseGet(() -> original.call(instance));
    }

    @Inject(method = "deleteWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    private void fastquit$deleteSavingWorld(CallbackInfo ci) {
        SaveManager.getSavingWorld(client.getSaveLoader().getSaveLoader(this.worldSummary.getFileName(), false).getWorldDirectory().toPath()).ifPresent(SaveManager::wait);
    }

    // While this should not be needed anymore, I'll leave it in just in case something goes wrong.
    // 虽然现在不再需要它，但我会保留它以防万一出现问题。
//    @Inject(method = "editWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/toasts/SystemToast;onWorldAccessFailure(Lnet/minecraft/client/Minecraft;Ljava/lang/String;)V"))
//    private void fastquit$openWorldListWhenFailed(CallbackInfo ci) {
//        this.client.displayGuiScreen(containingListSel);
//    }

    @ModifyArgs(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", ordinal = 0))
    private void fastquit$renderSavingTimeOnWorldList(Args args) {
        if (ModConfig.showSavingTime) {
            SaveManager.getSavingWorld(this.client.getSaveLoader().getSaveLoader(this.worldSummary.getFileName(), false).getWorldDirectory().toPath()).ifPresent(server -> {
                WorldInfo info = SaveManager.savingWorlds.get(server);
                String time = info.getTimeSaving() + " ⌛ " + args.get(0);
                args.set(0, time);
            });
        }
    }
}