package com.kingcontaria.fastquit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("fastquit");
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
