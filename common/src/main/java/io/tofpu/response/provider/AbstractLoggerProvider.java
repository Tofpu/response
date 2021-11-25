package io.tofpu.response.provider;

public abstract class AbstractLoggerProvider {
    public abstract void warn(final String content);
    public abstract void debug(final String content);
    public abstract void log(final String content);
}
