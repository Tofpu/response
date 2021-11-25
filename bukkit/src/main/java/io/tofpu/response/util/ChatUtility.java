package io.tofpu.response.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtility {
    private static boolean supportPlaceholderAPI;
    public static void setSupportPlaceholderAPI(final boolean supportPlaceholderAPI) {
        ChatUtility.supportPlaceholderAPI = supportPlaceholderAPI;
    }

    public static String colorize(final String content) {
        return ChatColor.translateAlternateColorCodes('&', content);
    }

    public static String colorize(final Player player, final String content) {
        if (!supportPlaceholderAPI || player == null) {
            return colorize(content);
        }
        return colorize(PlaceholderAPI.setPlaceholders(player, content));
    }
}
