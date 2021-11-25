package io.tofpu.response;

import io.tofpu.response.task.MigrationTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MigrationHoconPlugin extends JavaPlugin {
    private static final String DISABLING_MESSAGE = "%s - disabling this plugin now!";

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("response")) {
            disableMessage(this, "Response Plugin cannot be found");
            return;
        }

        // getting the response directory; basically our target
        final File targetDirectory = MigrationTask.getTargetDirectory();
        // getting an instance of task, providing the
        // class this plugin instance & target directory
        final MigrationTask task = MigrationTask.of(this, targetDirectory);

        final CompletableFuture<Boolean> migrationFuture = task.couldMigrate();
        migrationFuture.whenComplete((status, throwable) -> {
            // if the MigrationTask#couldMigrate returned false
            // it means there's nothing to migrate
            if (!status) {
                migrationFuture.cancel(true);
                disableMessage(this, "There was nothing to migrate");
            }
        }).whenComplete((unused, throwable) -> {
            // if the task were cancelled, returning
            if (throwable instanceof CompletionException) {
                return;
            }
            // starting the migrate process
            task.startMigrate()
                    .thenRun(task::shutdown);
        });
    }

    public static void disableMessage(final Plugin plugin, final String context) {
        plugin.getLogger().info(String.format(DISABLING_MESSAGE, context));
        Bukkit.getPluginManager().disablePlugin(plugin);
    }
}
