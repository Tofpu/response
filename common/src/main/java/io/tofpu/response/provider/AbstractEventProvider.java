package io.tofpu.response.provider;

public abstract class AbstractEventProvider {
    public abstract void setCancelled(final boolean cancel);

    public abstract String formattedContent();
    public abstract AbstractUserProvider getUserProvider();
}
