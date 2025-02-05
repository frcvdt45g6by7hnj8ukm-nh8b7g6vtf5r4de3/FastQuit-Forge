package com.kingcontaria.fastquit;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.util.ModLogger;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("fastquit")
public class FastQuit {
    public FastQuit(IEventBus modEventBus, ModContainer modContainer) {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ModConfigManager::getConfigScreen);
        ModLogger.log("FastQuit Initialized!");
    }
}