package com.kingcontaria.fastquit;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.ModContainer;

/**
 * Provides support for configuring options through Catalogue.
 */
@SuppressWarnings("unused")
public final class CatalogueIntegration {

    public static Screen createConfigScreen(Screen parent, ModContainer mod) {
        return FastQuit.CONFIG.createConfigScreen(parent);
    }
}