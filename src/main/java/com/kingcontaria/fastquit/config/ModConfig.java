package com.kingcontaria.fastquit.config;

import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.Loader;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ModConfig {

    /**
     * Determines whether the "Saving world" screen gets rendered.
     * <p>
     * 确定是否渲染“保存世界”界面。
     */
    
    public boolean renderSavingScreen = false;

    /**
     * Determines whether a toast gets shown when a world finishes saving.
     * <p>
     * 确定当世界保存完成时是否显示提示框（toast）。
     */
    
    public boolean showToasts = true;

    /**
     * Determines whether the time it took to save the world gets displayed on toasts and the world list.
     * <p>
     * 确定是否在提示框（toast）和世界列表中显示保存世界所花费的时间。
     */
    
    public boolean showSavingTime = true;

    /**
     * Determines the Thread priority used for {@link IntegratedServer}'s saving in the background.
     * Value needs to be between 0 and 10, with Thread priority staying unchanged if the value is 0.
     * <p>
     * 确定 {@link IntegratedServer} 后台保存时使用的线程优先级。
     * 值需要在 0 和 10 之间，当值为 0 时，线程优先级保持不变。
     */
    public int backgroundPriority = 2;

    /**
     * Determines whether multiple {@link IntegratedServer}'s can be running at the same time.
     * This is safe in vanilla minecraft but some mods can have issues with it.
     * <p>
     * 确定是否允许多个 {@link IntegratedServer} 同时运行。
     * 在原版 Minecraft 中这是安全的，但某些模组可能会因此出现问题。
     *
     * @apiNote Access through {@link ModConfig#allowMultipleServers()} to avoid known mod conflicts!
     * <p>
     * 通过 {@link ModConfig#allowMultipleServers()} 访问，以避免已知的模组冲突！
     */
    private boolean allowMultipleServers = true;

    /**
     * This {@link Set} holds the names of all currently active mods that conflict with {@link ModConfig#allowMultipleServers}.
     * <p>
     * 此 {@link Set} 保存所有与 {@link ModConfig#allowMultipleServers} 冲突的当前活动模组的名称。
     * @see ModConfig#allowMultipleServers()
     */
    private static final Set<String> MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS = new HashSet<>();

    static {
        // Put all conflicting Mod ID's in this set
        // 将所有冲突的 Mod ID 放入此集合中
        Set<String> incompatibleModIDs = new HashSet<>();
        incompatibleModIDs.add("quilt_biome");

        for (String modID : incompatibleModIDs) {
            Optional.ofNullable(Loader.instance().getIndexedModList().get(modID)).ifPresent(modContainer -> MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS.add(modContainer.getName()));
//            FabricLoader.getInstance().getModContainer(modID).ifPresent(modContainer -> MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS.add(modContainer.getMetadata().getName()));
        }
    }

    /**
     * @return Returns {@code false} when Quilt Biome API is loaded, returns {@link ModConfig#allowMultipleServers} otherwise.
     * <p>
     * 如果加载了 Quilt Biome API，则返回 {@code false}，否则返回 {@link ModConfig#allowMultipleServers}。
     */
    public boolean allowMultipleServers() {
        if (!MODS_THAT_CONFLICT_WITH_MULTIPLE_SERVERS.isEmpty()) {
            return false;
        }
        return this.allowMultipleServers;
    }
}
