package com.kingcontaria.fastquit.util;


import com.kingcontaria.fastquit.FastQuit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLogger {
    private static final Logger LOGGER = LogManager.getLogger(FastQuit.MODID);
    /**
     * Logs the given debug message.
     * <p>
     * 记录指定的调试信息。
     */
    public static void debug(String msg) {
        LOGGER.debug(msg);
    }
    /**
     * Logs the given message.
     * <p>
     * 记录指定的消息。
     */
    public static void log(String msg) {
        LOGGER.info(msg);
    }

    /**
     * Logs the given warning.
     * <p>
     * 记录指定的警告。
     */
    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    /**
     * Logs the given message and error.
     * <p>
     * 记录指定的消息和错误。
     */
    public static void error(String msg, Throwable throwable) {
        LOGGER.error(msg, throwable);
    }
}
