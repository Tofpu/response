package io.tofpu.response.util;

import io.tofpu.response.config.manager.ConfigManager;

public final class Logger {
    private static java.util.logging.Logger logger;

    public static void setLogger(final java.util.logging.Logger logger) {
        Logger.logger = logger;
    }

    public static void warn(final String message) {
        logger.warning(message);
    }

    public static void log(final String message) {
        logger.info(message);
    }

    public static void debug(final String message) {
        if (!ConfigManager.getInstance()
                .getConfiguration()
                .getGeneralCategory()
                .isDebugMessagesEnabled()) {
            return;
        }
        log(message);
    }
}
