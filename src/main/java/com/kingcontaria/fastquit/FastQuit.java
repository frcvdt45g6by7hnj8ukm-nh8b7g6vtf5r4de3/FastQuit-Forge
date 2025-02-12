package com.kingcontaria.fastquit;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.util.ModLogger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class FastQuit {
    public static final String MODID = "fastquit";
    public FastQuit() {
        ModLogger.log("FastQuit Initialized!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(ModConfig.class);
    }
}