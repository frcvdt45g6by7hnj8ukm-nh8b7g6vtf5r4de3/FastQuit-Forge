package com.kingcontaria.fastquit.util;

import com.kingcontaria.fastquit.config.ModConfigManager;
import com.kingcontaria.fastquit.mixin.accessor.LevelStorageSessionAccessor;
import com.kingcontaria.fastquit.mixin.accessor.MinecraftClientAccessor;
import com.kingcontaria.fastquit.mixin.accessor.MinecraftServerAccessor;
import com.kingcontaria.fastquit.screen.WaitingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SaveManager {
    /**
     * Synchronized {@link Map} containing all currently saving {@link IntegratedServer}'s, with a {@link WorldInfo} with more information about the world.
     * <p>
     * 包含所有当前正在保存的 {@link IntegratedServer} 的同步 {@link Map}，其中包含带有更多世界信息的 {@link WorldInfo}。
     */
    public static final Map<IntegratedServer, WorldInfo> savingWorlds = Collections.synchronizedMap(new HashMap<>());

    /**
     * Stores {@link SaveFormat.LevelSave}'s used by FastQuit as to only close them if no other process is currently using them.
     * <p>
     * 存储由 FastQuit 使用的 {@link SaveFormat.LevelSave}，以便仅在没有其他进程正在使用它们时关闭。
     */
    public static final List<SaveFormat.LevelSave> occupiedSessions = Collections.synchronizedList(new ArrayList<>());

    /**
     * Waits for all {@link IntegratedServer}'s to finish saving, gets called when Minecraft is closed.
     * <p>
     * 等待所有 {@link IntegratedServer} 完成保存操作，在 Minecraft 关闭时调用。
     *
     * @implNote Catches everything to avoid any issues in the areas where it's called.
     * <p>
     * 捕获所有异常，以避免其被调用的区域出现任何问题。
     */
    public static void exit() {
        try {
            ModLogger.log("Exiting FastQuit.");
            wait(savingWorlds.keySet());
        } catch (Throwable throwable) {
            ModLogger.error("Something went horribly wrong when exiting FastQuit!", throwable);
            savingWorlds.forEach((server, info) -> {
                try {
                    server.getRunningThread().join();
                } catch (Throwable throwable2) {
                    ModLogger.error("Failed to wait for \"" + server.getWorldData().getLevelName() + "\"", throwable2);
                }
            });
        }
    }

    /**
     * @see #wait(Collection, CallbackInfo)
     */
    public static void wait(IntegratedServer server) {
        wait(Collections.singleton(server), null);
    }

    /**
     * @see #wait(Collection, CallbackInfo)
     */
    public static void wait(IntegratedServer server, @Nullable CallbackInfo cancellable) {
        wait(Collections.singleton(server), cancellable);
    }

    /**
     * @see #wait(Collection, CallbackInfo)
     */
    public static void wait(Collection<IntegratedServer> servers) {
        wait(servers, null);
    }

    /**
     * Waits for all the {@link IntegratedServer}'s in the given {@link Collection} to finish saving and in the meantime renders a {@link WaitingScreen}.
     * <p>
     * 等待给定 {@link Collection} 中的所有 {@link IntegratedServer} 完成保存，同时渲染一个 {@link WaitingScreen}。
     * <p>
     * If a {@link CallbackInfo} is given, the waiting can be cancelled by the user.
     * <p>
     * 如果提供了 {@link CallbackInfo}，用户可以取消等待。
     *
     * @throws IllegalStateException if called on one of the given {@link IntegratedServer}'s threads, would cause a deadlock otherwise
     * <p>
     * IllegalStateException 如果在给定的 {@link IntegratedServer} 线程上调用，将会导致死锁
     */
    public static void wait(Collection<IntegratedServer> servers, @Nullable CallbackInfo cancellable) {
        if (servers == null || servers.isEmpty()) {
            return;
        }

        Minecraft client = Minecraft.getMinecraft();

        if (!client.isCallingFromMinecraftThread()) {
            if (servers.stream().anyMatch(server -> Thread.currentThread() == server.getRunningThread())) {
                throw new IllegalStateException("Tried to call FastQuit.wait(...) from one of the servers it's supposed to wait for.");
            }

//            client.submit(() -> wait(servers)).join();
            return;
        }

        GuiScreen oldScreen = client.currentScreen;

        ITextComponent stillSaving = TextHelper.translatable(
                "fastquit.screen.waiting",
                servers.stream().map(server -> server.getWorldData().getLevelName()).collect(Collectors.joining("\" & \""))
        );
        ModLogger.log(stillSaving.getFormattedText());

        servers.forEach(server -> server.getRunningThread().setPriority(Thread.NORM_PRIORITY));

        try {
            client.displayGuiScreen(new WaitingScreen(stillSaving, cancellable));

            while (servers.stream().anyMatch(server -> !server.isShutdown())) {
                if (cancellable != null && cancellable.isCancelled()) {
                    if (ModConfigManager.getConfig().backgroundPriority != 0) {
                        servers.forEach(server -> server.getRunningThread().setPriority(ModConfigManager.getConfig().backgroundPriority));
                    }
                    ModLogger.log("Cancelled waiting for currently saving worlds.");
                    break;
                }
                ((MinecraftClientAccessor) client).fastquit$render(false);
            }
        } finally {
            // compatibility with "WorldGen" mod
            if (oldScreen != null && oldScreen.getClass().getName().equals("caeruleusTait.WorldGen.gui.screens.WGConfigScreen")) {
                client.currentScreen = oldScreen;
            } else {
                client.displayGuiScreen(oldScreen);
            }
        }
    }

    /**
     * @return optionally returns the currently {@link IntegratedServer} matching the given {@link Path}
     * <p>
     * 可选地返回与给定 {@link Path} 匹配的当前 {@link IntegratedServer}。
     */
    public static Optional<IntegratedServer> getSavingWorld(Path path) {
        return savingWorlds.keySet().stream().filter(server -> ((LevelStorageSessionAccessor) ((MinecraftServerAccessor) server).fastquit$getSession()).fastquit$getDirectory().equals(path)).findFirst();
    }

    /**
     * @return optionally returns the currently saving {@link IntegratedServer} matching the given {@link SaveFormat.LevelSave}
     * <p>
     * 可选地返回当前正在保存的 {@link IntegratedServer}，该服务器与给定的 {@link SaveFormat.LevelSave} 匹配。
     */
    public static Optional<IntegratedServer> getSavingWorld(SaveFormat.LevelSave session) {
        return savingWorlds.keySet().stream().filter(server -> ((MinecraftServerAccessor) server).fastquit$getSession() == session).findFirst();
    }

    /**
     * @return optionally returns the {@link SaveFormat.LevelSave} of the currently saving {@link IntegratedServer} matching the given {@link Path}
     * <p>
     * 可选地返回与给定 {@link Path} 匹配的当前正在保存的 {@link IntegratedServer} 的 {@link SaveFormat.LevelSave}。
     * @apiNote Remember to {@link SaveFormat.LevelSave#close() close} the session after using it!
     * <p>
     * 使用后记得 {@link SaveFormat.LevelSave#close() 关闭} 会话！
     */
    public static Optional<SaveFormat.LevelSave> getSession(Path path) {
        return getSavingWorld(path).flatMap(server -> {
            SaveFormat.LevelSave session;
            synchronized (session = ((MinecraftServerAccessor) server).fastquit$getSession()) {
                if (((LevelStorageSessionAccessor) session).fastquit$getLock().isValid()) {
                    occupiedSessions.add(session);
                    return Optional.of(session);
                }
            }
            return Optional.empty();
        });
    }
}
