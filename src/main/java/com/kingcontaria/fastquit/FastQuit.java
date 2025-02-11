package com.kingcontaria.fastquit;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = FastQuit.MODID, name = "FastQuit")
public class FastQuit {
    public static final String MODID = "fastquit";
    public FastQuit() {
//        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
//        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ModConfigManager::getConfigScreen);
        ModLogger.log("FastQuit Initialized!");
    }
}