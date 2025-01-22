package com.kingcontaria.fastquit.util;

/**
 * Saves additional information about a world.
 * <p>
 * 保存关于一个世界的附加信息。
 */
public class WorldInfo {

    /**
     * Whether the corresponding world has been deleted.
     * <p>
     * 是否已删除对应的世界。
     */
    public boolean deleted = false;

    /**
     * Saves the time of instantiation which is the same as the start of saving of the corresponding world.
     * <p>
     * 保存实例化的时间，该时间与对应世界开始保存的时间相同。
     */
    private final long startedSaving;

    public WorldInfo() {
        this.startedSaving = System.currentTimeMillis();
    }

    /**
     * @return a {@link String} representing the time passed since the start of saving the corresponding world
     * <p>
     * 一个 {@link String}，表示自对应世界开始保存以来经过的时间
     */
    public String getTimeSaving() {
        long sec = Math.round((System.currentTimeMillis() - startedSaving) / 1000.0);
        return sec < 60 ? (sec + "s") : (sec / 60 + "min " + sec % 60 + "s");
    }
}