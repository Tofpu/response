package io.tofpu.response.provider;

public class EventProvider extends AbstractEventProvider {
    private final String content;
    private final String formattedContent;

    public EventProvider(final String content, final String formattedContent) {
        this.content = content;
        this.formattedContent = formattedContent;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        System.out.println("Event cancellation status: " + cancel);
    }

    @Override
    public String rawContent() {
        return this.content;
    }

    @Override
    public String formattedContent() {
        return this.formattedContent;
    }

    @Override
    public AbstractUserProvider getUserProvider() {
        return new UserProvider();
    }
}
