package com.kingcontaria.fastquit;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;

public class FastQuitConfigScreen {
    public static final ConfigScreenHandler.ConfigScreenFactory FACTORY = new ConfigScreenHandler.ConfigScreenFactory(FastQuitConfigScreen::getConfigScreen);

    public static Screen getConfigScreen(Screen parent){
        return AutoConfig.getConfigScreen(FastQuitConfig.class, parent).get();
    }
}
