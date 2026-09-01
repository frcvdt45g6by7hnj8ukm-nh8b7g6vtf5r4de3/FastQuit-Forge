package com.kingcontaria.fastquit.util;

import com.kingcontaria.fastquit.config.ModConfig;
import com.kingcontaria.fastquit.mixin.accessor.LevelStorageSessionAccessor;
import com.kingcontaria.fastquit.mixin.accessor.MinecraftClientAccessor;
import com.kingcontaria.fastquit.mixin.accessor.MinecraftServerAccessor;
import com.kingcontaria.fastquit.screen.WaitingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SaveManager {
    /**
     * Synchronized {@link Map} containing all currently saving {@link IntegratedServer}'s, with a {@link WorldInfo} with more information about the world.
     * <p>
     * 包含所有当前正在保存的 {@link IntegratedServer} 的同步 {@link Map}，其中包含带有更多世界信息的 {@link WorldInfo}。
     */
    public static final Map<IntegratedServer, WorldInfo> savingWorlds = Collections.synchronizedMap(new HashMap<>());
    private static final AtomicBoolean REGION_CACHE_FLUSH_QUEUED = new AtomicBoolean();

    /** Returns a stable snapshot; callers never retain the synchronized map's live view. */
    public static List<IntegratedServer> snapshotSavingWorlds() {
        synchronized (savingWorlds) {
            return new ArrayList<>(savingWorlds.keySet());
        }
    }

    public static boolean hasSavingWorlds() {
        synchronized (savingWorlds) {
            return !savingWorlds.isEmpty();
        }
    }

    public static Thread getServerThread(IntegratedServer server) {
        return ((MinecraftServerAccessor) server).fastquit$getThread();
    }

    public static Optional<IntegratedServer> getSavingServerForCurrentThread() {
        return snapshotSavingWorlds().stream().filter(MinecraftServer::isCallingFromMinecraftThread).findFirst();
    }

    /**
     * The 1.12 region-file cache is global. Do not close it from one detached
     * server while another detached or foreground integrated server is alive.
     */
    public static boolean shouldDeferRegionFileCacheCloseForCurrentThread() {
        Optional<IntegratedServer> currentOptional = getSavingServerForCurrentThread();
        if (!currentOptional.isPresent()) return false;

        IntegratedServer current = currentOptional.get();
        for (IntegratedServer server : snapshotSavingWorlds()) {
            if (server != current && getServerThread(server).isAlive()) return true;
        }

        IntegratedServer foreground = Minecraft.getMinecraft().getIntegratedServer();
        return foreground != null && foreground != current && getServerThread(foreground).isAlive();
    }

    /** Close the global cache on the client thread once every server using it is idle. */
    public static void requestRegionFileCacheFlush() {
        if (!REGION_CACHE_FLUSH_QUEUED.compareAndSet(false, true)) return;

        Minecraft client = Minecraft.getMinecraft();
        try {
            client.addScheduledTask(() -> {
                try {
                    if (hasSavingWorlds()) return;
                    IntegratedServer foreground = client.getIntegratedServer();
                    if (foreground != null && getServerThread(foreground).isAlive()) return;
                    client.getSaveLoader().flushCache();
                } finally {
                    REGION_CACHE_FLUSH_QUEUED.set(false);
                }
            });
        } catch (Throwable throwable) {
            REGION_CACHE_FLUSH_QUEUED.set(false);
            ModLogger.error("Failed to schedule the region-file cache close.", throwable);
        }
    }

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
            wait(snapshotSavingWorlds());
        } catch (Throwable throwable) {
            ModLogger.error("Something went horribly wrong when exiting FastQuit!", throwable);
            for (IntegratedServer server : snapshotSavingWorlds()) {
                try {
                    getServerThread(server).join();
                } catch (Throwable throwable2) {
                    ModLogger.error("Failed to wait for \"" + server.getWorldName() + "\"", throwable2);
                }
            }
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
        // Never keep a live synchronized-map view across scheduling or rendering.
        servers = new ArrayList<>(servers);

        Minecraft client = Minecraft.getMinecraft();

        if (!client.isCallingFromMinecraftThread()) {
            if (servers.stream().anyMatch(MinecraftServer::isCallingFromMinecraftThread)) {
                throw new IllegalStateException("Tried to call FastQuit.wait(...) from one of the servers it's supposed to wait for.");
            }

            Collection<IntegratedServer> scheduledServers = new ArrayList<>(servers);
            client.addScheduledTask(() -> wait(scheduledServers));
            return;
        }

        GuiScreen oldScreen = client.currentScreen;

        ITextComponent stillSaving = TextHelper.translatable(
                "fastquit.screen.waiting",
                servers.stream().map(MinecraftServer::getWorldName).collect(Collectors.joining("\" & \""))
        );
        ModLogger.log(stillSaving.getFormattedText());

        servers.forEach(server -> getServerThread(server).setPriority(Thread.NORM_PRIORITY));

        try {
            client.displayGuiScreen(new WaitingScreen(stillSaving, cancellable));

            while (servers.stream().anyMatch(server -> getServerThread(server).isAlive())) {
                if (cancellable != null && cancellable.isCancelled()) {
                    if (ModConfig.backgroundPriority != 0) {
                        servers.forEach(server -> getServerThread(server).setPriority(ModConfig.backgroundPriority));
                    }
                    ModLogger.log("Cancelled waiting for currently saving worlds.");
                    break;
                }
                ((MinecraftClientAccessor) client).fastquit$render();
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
        Path normalized = path.toAbsolutePath().normalize();
        return snapshotSavingWorlds().stream().filter(server ->
                ((LevelStorageSessionAccessor) ((MinecraftServerAccessor) server).fastquit$getSession())
                        .fastquit$getDirectory().toPath().resolve(((MinecraftServerAccessor) server).fastquit$getFolderName())
                        .toAbsolutePath().normalize().equals(normalized))
                .findFirst();
    }

    /**
     * @return optionally returns the currently saving {@link IntegratedServer} matching the given {@link ISaveHandler}
     * <p>
     * 可选地返回当前正在保存的 {@link IntegratedServer}，该服务器与给定的 {@link ISaveFormat} 匹配。
     */
    public static Optional<IntegratedServer> getSavingWorld(ISaveFormat session) {
        return snapshotSavingWorlds().stream().filter(server -> ((MinecraftServerAccessor) server).fastquit$getSession() == session).findFirst();
    }

    /**
     * @return optionally returns the currently saving {@link IntegratedServer} matching the given {@link ISaveHandler}
     * <p>
     * 可选地返回当前正在保存的 {@link IntegratedServer}，该服务器与给定的 {@link ISaveHandler} 匹配。
     */
    public static Optional<IntegratedServer> getSavingWorld(ISaveHandler session, String saveName) {
        return snapshotSavingWorlds().stream().filter(server -> server.getWorld(0) != null
                && server.getWorld(0).getSaveHandler() == session).findFirst();
    }
    /**
     * @return optionally returns the {@link ISaveFormat} of the currently saving {@link IntegratedServer} matching the given {@link Path}
     * <p>
     * 可选地返回与给定 {@link Path} 匹配的当前正在保存的 {@link IntegratedServer} 的 {@link ISaveFormat}。
     */
    public static Optional<ISaveFormat> getSession(Path path) {
        return getSavingWorld(path).flatMap(server -> {
            ISaveFormat session = ((MinecraftServerAccessor) server).fastquit$getSession();
            return Optional.of(session);
        });
    }
}
