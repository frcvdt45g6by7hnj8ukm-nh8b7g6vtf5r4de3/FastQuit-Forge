package com.kingcontaria.fastquit.screen;

import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.TextHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

public class WaitingScreen extends GenericDirtMessageScreen {

    private final CallbackInfo callbackInfo;

    public WaitingScreen(Component text, @Nullable CallbackInfo callbackInfo) {
        super(text);
        if (callbackInfo != null && !callbackInfo.isCancellable()) {
            ModLogger.warn("Provided CallbackInfo for \"" + callbackInfo.getId() + "\" is not cancellable!");
            callbackInfo = null;
        }
        this.callbackInfo = callbackInfo;
    }

    @Override
    public void init() {
        if (this.callbackInfo != null) {
            this.addRenderableWidget(new Button(this.width - 100 - 5, this.height - 20 - 5, 100, 20, TextHelper.BACK, button -> this.onClose()));
        }
    }

    @Override
    public void render(@NotNull PoseStack context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        String loading = LoadingDotsText.get(Util.getMillis());
        this.minecraft.font.draw(context, loading, (float) (this.width - this.minecraft.font.width(loading)) / 2, 95, 0x808080);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.callbackInfo != null;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.callbackInfo != null) {
            this.callbackInfo.cancel();
        }
    }
}
