package com.kingcontaria.fastquit.screen;

import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.TextHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

public class WaitingScreen extends DirtMessageScreen {
    private static final String[] FRAMES = new String[]{"O o o", "o O o", "o o O", "o O o"};
    private final CallbackInfo callbackInfo;

    public WaitingScreen(ITextComponent text, @Nullable CallbackInfo callbackInfo) {
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
            this.addButton(new Button(this.width - 100 - 5, this.height - 20 - 5, 100, 20, TextHelper.BACK, button -> this.onClose()));
        }
    }

    @Override
    public void render(MatrixStack pMatrixStack, int mouseX, int mouseY, float delta) {
        super.render(pMatrixStack, mouseX, mouseY, delta);
        int num = (int)(Util.getMillis() / 300L % (long)FRAMES.length);
        String loading = FRAMES[num];
        this.minecraft.font.draw(pMatrixStack, loading, (float) (this.width - this.minecraft.font.width(loading)) / 2, 95, 0x808080);
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