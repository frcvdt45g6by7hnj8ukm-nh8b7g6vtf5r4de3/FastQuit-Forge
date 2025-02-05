package com.kingcontaria.fastquit.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.ConfigScreenHandler;

public class ModConfigManager {
    private static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    private static final ConfigScreenHandler.ConfigScreenFactory FACTORY = new ConfigScreenHandler.ConfigScreenFactory(ModConfigManager::getConfigScreen);
    public static Screen getConfigScreen(Minecraft minecraft, Screen parent){
        return AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
    public static ModConfig getConfig(){
        return CONFIG;
    }
    public static ConfigScreenHandler.ConfigScreenFactory getConfigFactory(){
        return FACTORY;
    }
}
