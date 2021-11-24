package io.tofpu.response.provider;

public class EventProvider extends AbstractEventProvider {
    private final String content;

    public EventProvider(final String content) {
        this.content = content;
    }

    public void setCancelled(final boolean cancel) {
        System.out.println("Event cancellation status: " + cancel);
    }

    public String content() {
        return this.content;
    }

    public AbstractUserProvider getUserProvider() {
        return new UserProvider();
    }
}
