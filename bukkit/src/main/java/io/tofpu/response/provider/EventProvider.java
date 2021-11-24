package io.tofpu.response.provider;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EventProvider extends AbstractEventProvider {
    private final AsyncPlayerChatEvent event;

    public static EventProvider of(final AsyncPlayerChatEvent event) {
        return new EventProvider(event);
    }

    private EventProvider(final AsyncPlayerChatEvent event) {
        this.event = event;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.event.setCancelled(cancel);
    }

    @Override
    public String content() {
        return this.event.getMessage();
    }

    @Override
    public AbstractUserProvider getUserProvider() {
        return UserProvider.of(this.event.getPlayer());
    }
}
