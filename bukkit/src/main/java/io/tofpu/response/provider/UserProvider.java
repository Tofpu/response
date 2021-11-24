package io.tofpu.response.provider;

import org.bukkit.entity.Player;

public class UserProvider extends AbstractUserProvider {
    private final Player player;

    public static UserProvider of(final Player player) {
        return new UserProvider(player);
    }

    private UserProvider(final Player player) {
        this.player = player;
    }

    @Override
    public void sendMessage(final String message) {
        this.player.sendMessage(message);
    }
}
