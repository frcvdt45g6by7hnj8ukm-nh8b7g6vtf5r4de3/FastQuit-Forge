package com.kingcontaria.fastquit.screen;

import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.TextHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

public class WaitingScreen extends GuiScreen {
    private static final String[] FRAMES = new String[]{"O o o", "o O o", "o o O", "o O o"};
    private final CallbackInfo callbackInfo;
    private final ITextComponent text;

    public WaitingScreen(ITextComponent text, @Nullable CallbackInfo callbackInfo) {
        if (callbackInfo != null && !callbackInfo.isCancellable()) {
            ModLogger.warn("Provided CallbackInfo for \"" + callbackInfo.getId() + "\" is not cancellable!");
            callbackInfo = null;
        }
        this.callbackInfo = callbackInfo;
        this.text = text;
    }

    @Override
    public void initGui() {
        if (this.callbackInfo != null) {
            this.buttonList.add(new GuiButton(0,this.width - 100 - 5, this.height - 20 - 5, 100, 20, TextHelper.BACK));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        this.drawDefaultBackground();
        int num = (int)(System.currentTimeMillis() / 300L % (long)FRAMES.length);
        String loading = FRAMES[num];
        this.drawCenteredString(this.fontRenderer, this.text.getUnformattedText(), this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRenderer, loading, this.width / 2, 110, 0x808080);
        super.drawScreen(mouseX, mouseY, delta);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (this.callbackInfo != null) {
            this.callbackInfo.cancel();
        }
        this.mc.displayGuiScreen(null);
    }
}
