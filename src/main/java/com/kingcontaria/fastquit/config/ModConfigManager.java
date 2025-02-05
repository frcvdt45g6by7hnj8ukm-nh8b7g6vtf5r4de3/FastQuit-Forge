package com.kingcontaria.fastquit.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;

public class ModConfigManager {
    private static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    public static Screen getConfigScreen(ModContainer container, Screen parent){
        return AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
    public static ModConfig getConfig(){
        return CONFIG;
    }

}
