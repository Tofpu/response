package io.tofpu.response.provider;

public class LoggerProvider extends AbstractLoggerProvider {
    public void warn(final String content) {
        System.out.println("Logged a warning message: " + content);
    }

    public void debug(final String content) {
        System.out.println("Logged a debug message: " + content);
    }

    public void log(final String content) {
        System.out.println("Logged a message: " + content);
    }
}
