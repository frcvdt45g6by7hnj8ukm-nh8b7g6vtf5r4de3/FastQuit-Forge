package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.WorldInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiListWorldSelectionEntry.class)
public abstract class WorldListWidgetWorldEntryMixin {

    @Shadow @Final private GuiListWorldSelection containingListSel;
    @Shadow @Final private Minecraft client;
    @Shadow @Final private WorldSummary worldSummary;

    @WrapOperation(method = "recreateWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSaveLoader()Lnet/minecraft/world/storage/ISaveFormat;"))
    private ISaveFormat fastquit$editSavingWorld(Minecraft instance, Operation<ISaveFormat> original) {
        return SaveManager.getSession(storage.getBaseDir().resolve(directoryName)).orElseGet(() -> original.call(instance));
    }

    @WrapOperation(method = "deleteWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSaveLoader()Lnet/minecraft/world/storage/ISaveFormat;"))
    private ISaveFormat fastquit$deleteSavingWorld(Minecraft instance, Operation<ISaveFormat> original) {
        return SaveManager.getSession(storage.getBaseDir().resolve(directoryName)).orElseGet(() -> original.call(storage, directoryName));
    }

    // While this should not be needed anymore, I'll leave it in just in case something goes wrong.
    // 虽然现在不再需要它，但我会保留它以防万一出现问题。
//    @Inject(method = "editWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/toasts/SystemToast;onWorldAccessFailure(Lnet/minecraft/client/Minecraft;Ljava/lang/String;)V"))
//    private void fastquit$openWorldListWhenFailed(CallbackInfo ci) {
//        this.client.displayGuiScreen(containingListSel);
//    }

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", ordinal = 0, shift = At.Shift.AFTER))
    private void fastquit$renderSavingTimeOnWorldList(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (ModConfigManager.getConfig().showSavingTime) {
            SaveManager.getSavingWorld(this.client.getLevelSource().getBaseDir().resolve(this.worldSummary.getLevelId())).ifPresent(server -> {
                WorldInfo info = SaveManager.savingWorlds.get(server);
                if (info != null) {
                    String time = info.getTimeSaving() + " ⌛";
                    this.client.fontRenderer.drawString(time, x + entryWidth - this.client.fontRenderer.getStringWidth(time) - 4, y + 1, -6939106);
                }
            });
        }
    }
}