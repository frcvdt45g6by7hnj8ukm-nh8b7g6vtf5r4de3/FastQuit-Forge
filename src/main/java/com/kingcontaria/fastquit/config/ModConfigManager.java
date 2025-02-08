package com.kingcontaria.fastquit.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.client.ConfigGuiHandler;

public class ModConfigManager {
    private static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
//    private static final ConfigGuiHandler.ConfigGuiFactory FACTORY = new ConfigGuiHandler.ConfigGuiFactory(ModConfigManager::getConfigScreen);
    public static Screen getConfigScreen(Minecraft minecraft, Screen parent){
        return AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
    public static ModConfig getConfig(){
        return CONFIG;
    }
//    public static ConfigGuiHandler.ConfigGuiFactory getConfigFactory(){
//        return FACTORY;
//    }
}
