package io.tofpu.response.util;

import io.tofpu.response.ResponsePlugin;
import org.bukkit.Bukkit;

public class TaskUtility {
    public static void schedule(final Runnable runnable, final long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ResponsePlugin.getPlugin(ResponsePlugin.class), runnable, delay);
    }
}
