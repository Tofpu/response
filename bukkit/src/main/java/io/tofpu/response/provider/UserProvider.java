package io.tofpu.response.provider;

import io.tofpu.response.util.ChatUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UserProvider extends AbstractUserProvider {
    private final Player player;
    private final AsyncPlayerChatEvent event;

    public static UserProvider of(final Player player, final AsyncPlayerChatEvent event) {
        return new UserProvider(player, event);
    }

    private UserProvider(final Player player, final AsyncPlayerChatEvent event) {
        this.player = player;
        this.event = event;
    }

    @Override
    public void sendMessage(final String message) {
        this.event.setMessage(ChatUtility.colorize(player, message));
    }

    @Override
    public boolean hasPermission(final String node) {
        return this.player.hasPermission(node);
    }
}
