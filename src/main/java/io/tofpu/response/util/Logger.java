package io.tofpu.response.util;

public class Logger {
    private static java.util.logging.Logger logger;

    public static void setLogger(final java.util.logging.Logger logger) {
        Logger.logger = logger;
    }

    public static void warn(final String message) {
        logger.warning(message);
    }

    public static void debug(final String message) {
        logger.info(message);
    }
}
