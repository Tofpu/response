package io.tofpu.response.provider;

public abstract class AbstractUserProvider {
    public abstract void sendMessage(final String message);
    public abstract boolean hasPermission(final String node);
}
