package io.tofpu.response.util;

import org.bukkit.ChatColor;

public class ChatUtility {
    public static String colorize(final String content) {
        return ChatColor.translateAlternateColorCodes('&', content);
    }
}
