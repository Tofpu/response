package io.tofpu.response.provider;

import io.tofpu.response.util.Logger;

public class LoggerProvider extends AbstractLoggerProvider {
    public void warn(final String content) {
        Logger.warn(content);
    }

    public void debug(final String content) {
        Logger.debug(content);
    }

    public void log(final String content) {
        Logger.log(content);
    }
}
