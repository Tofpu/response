package io.tofpu.response.provider;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EventProvider extends AbstractEventProvider {
    private final AsyncPlayerChatEvent event;
    private final String formattedContent;

    public static EventProvider of(final AsyncPlayerChatEvent event, final String formattedContent) {
        return new EventProvider(event, formattedContent);
    }

    private EventProvider(final AsyncPlayerChatEvent event, final String formattedContent) {
        this.event = event;
        this.formattedContent = formattedContent;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.event.setCancelled(cancel);
    }

    @Override
    public String formattedContent() {
        return this.formattedContent;
    }

    @Override
    public AbstractUserProvider getUserProvider() {
        return UserProvider.of(this.event.getPlayer(), this.event);
    }
}
