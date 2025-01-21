package com.kingcontaria.fastquit.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Config(name = "fastquit")
public class FastQuitConfig implements ConfigData {

    /**
     * Determines whether the "Saving world" screen gets rendered.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean renderSavingScreen = false;

    /**
     * Determines whether a toast gets shown when a world finishes saving.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean showToasts = true;

    /**
     * Determines whether the time it took to save the world gets displayed on toasts and the world list.
     */
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ShowSavingTime showSavingTime = ShowSavingTime.TRUE;

    /**
     * Determines the Thread priority used for {@link IntegratedServer}'s saving in the background.
     * Value needs to be between 0 and 10, with Thread priority staying unchanged if the value is 0.
     */
    @ConfigEntry.Category("performance")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(max = Thread.MAX_PRIORITY)
    public int backgroundPriority = 2;

    /**
     * Determines whether multiple {@link IntegratedServer}'s can be running at the same time.
     * This is safe in vanilla minecraft but some mods can have issues with it.
     *
     * @apiNote Access through {@link FastQuitConfig#allowMultipleServers()} to avoid known mod conflicts!
     */
    @ConfigEntry.Category("compat")
    @ConfigEntry.Gui.Tooltip
    private boolean allowMultipleServers = true;

    /**
     * This {@link Set} holds the names of all currently active mods that conflict with {@link FastQuitConfig#allowMultipleServers}.
     * @see FastQuitConfig#allowMultipleServers()
     */
    private static final Set<String> MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS = new HashSet<>();

    static {
        // Put all conflicting Mod ID's in this set
        Set<String> incompatibleModIDs = Set.of("quilt_biome");

        for (String modID : incompatibleModIDs) {
            ModList.get().getModContainerById(modID).ifPresent(modContainer -> MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS.add(modContainer.getModInfo().getDisplayName()));
//            FabricLoader.getInstance().getModContainer(modID).ifPresent(modContainer -> MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS.add(modContainer.getMetadata().getName()));
        }
    }

    /**
     * @return Returns {@code false} when Quilt Biome API is loaded, returns {@link FastQuitConfig#allowMultipleServers} otherwise.
     */
    public boolean allowMultipleServers() {
        if (!MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS.isEmpty()) {
            return false;
        }
        return this.allowMultipleServers;
    }

    public static Screen getConfigScreen(Screen parent){
        return AutoConfig.getConfigScreen(FastQuitConfig.class, parent).get();
    }

    public enum ShowSavingTime implements SelectionListEntry.Translatable {
        FALSE,
        TOAST_ONLY,
        TRUE;

        @Override
        public @NotNull String getKey() {
            if (this == ShowSavingTime.TOAST_ONLY) {
                return "fastquit.config.general.showSavingTime.toastsOnly";
            }
            return "text.cloth-config.boolean.value." + (this == ShowSavingTime.TRUE);
        }
    }
    
    private enum ModCompat {
        DISABLED
    }
}
