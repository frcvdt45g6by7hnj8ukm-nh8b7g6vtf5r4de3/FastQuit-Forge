package com.kingcontaria.fastquit.config;

public class ModConfigManager {
    private static final ModConfig CONFIG = new ModConfig();
//    public static Screen getConfigScreen(Minecraft minecraft, Screen parent){
//        return AutoConfig.getConfigScreen(ModConfig.class, parent).get();
//    }
    public static ModConfig getConfig(){
        return CONFIG;
    }
}
