package com.kingcontaria.fastquit.mixin;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.SaveManager;
import com.kingcontaria.fastquit.util.WorldInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListWidgetWorldEntryMixin {

    @Shadow @Final private SelectWorldScreen screen;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private LevelSummary summary;

    @WrapOperation(method = {"editWorld", "recreateWorld"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource;createAccess(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;"))
    private LevelStorageSource.LevelStorageAccess fastquit$editSavingWorld(LevelStorageSource storage, String directoryName, Operation<LevelStorageSource.LevelStorageAccess> original) {
        return SaveManager.getSession(storage.getBaseDir().resolve(directoryName)).orElseGet(() -> original.call(storage, directoryName));
    }

    @WrapOperation(method = "doDeleteWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource;createAccess(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;"))
    private LevelStorageSource.LevelStorageAccess fastquit$deleteSavingWorld(LevelStorageSource storage, String directoryName, Operation<LevelStorageSource.LevelStorageAccess> original) {
        return SaveManager.getSession(storage.getBaseDir().resolve(directoryName)).orElseGet(() -> original.call(storage, directoryName));
    }

    // While this should not be needed anymore, I'll leave it in just in case something goes wrong.
    // 虽然现在不再需要它，但我会保留它以防万一出现问题。
    @Inject(method = "editWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/SystemToast;onWorldAccessFailure(Lnet/minecraft/client/Minecraft;Ljava/lang/String;)V"))
    private void fastquit$openWorldListWhenFailed(CallbackInfo ci) {
        this.minecraft.setScreen(this.screen);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I", ordinal = 0, shift = At.Shift.AFTER))
    private void fastquit$renderSavingTimeOnWorldList(PoseStack context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (ModConfigManager.getConfig().showSavingTime == ModConfig.ShowSavingTime.TRUE) {
            SaveManager.getSavingWorld(this.minecraft.getLevelSource().getBaseDir().resolve(this.summary.getLevelId())).ifPresent(server -> {
                WorldInfo info = SaveManager.savingWorlds.get(server);
                if (info != null) {
                    String time = info.getTimeSaving() + " ⌛";
                    this.minecraft.font.draw(context, time, x + entryWidth - this.minecraft.font.width(time) - 4, y + 1, -6939106);
                }
            });
        }
    }
}