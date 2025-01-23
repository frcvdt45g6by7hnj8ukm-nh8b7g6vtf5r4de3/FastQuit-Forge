package com.kingcontaria.fastquit;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(FastQuit.MODID)
public final class FastQuit {
    public static final String MODID = "fastquit";
    public FastQuit() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        ModLoadingContext.get().registerExtensionPoint(ModConfigManager.getConfigFactory().getClass(), ModConfigManager::getConfigFactory);
        ModLogger.log("FastQuit Initialized!");
    }
}