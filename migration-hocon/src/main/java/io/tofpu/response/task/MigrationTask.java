package io.tofpu.response.task;

import io.tofpu.response.MigrationHoconPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class MigrationTask {
    private final Plugin plugin;
    private final Logger logger;
    private final File targetDirectory;
    private boolean migrated;

    public static MigrationTask of(final Plugin plugin, final File targetDirectory) {
        return new MigrationTask(plugin, targetDirectory);
    }

    private MigrationTask(final Plugin plugin, final File targetDirectory) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.targetDirectory = targetDirectory;
        this.migrated = false;
    }

    public static File getTargetDirectory() {
        final Plugin targetPlugin = Bukkit.getPluginManager().getPlugin("response");

        // if the target plugin is null, return a null file
        if (targetPlugin == null) {
            return null;
        }

        // returning our target directory
        return new File(targetPlugin.getDataFolder(), "response");
    }

    public CompletableFuture<Boolean> couldMigrate() { ;
        return CompletableFuture.completedFuture(targetDirectory != null && targetDirectory.exists());
    }

    public CompletableFuture<Void> startMigrate() {
        for (final File file : this.targetDirectory.listFiles()) {
            if (!file.getName().endsWith(".yml")) {
                continue;
            }
            final YamlConfigurationLoader yamlLoader = YamlConfigurationLoader.builder()
                    .file(file)
                    .build();
            final HoconConfigurationLoader hoconLoader = HoconConfigurationLoader.builder()
                    .file(new File(this.targetDirectory, file.getName().replace(".yml", ".conf")))
                    .build();

            final ConfigurationNode yamlNode;
            final ConfigurationNode hoconNode;
            try {
                yamlNode = yamlLoader.load();
                hoconNode = hoconLoader.load();
            } catch (ConfigurateException e) {
                printException(e, "Failed to load " + file.getName() + " file");
                continue;
            }

            try {
                final ConfigurationNode responseNode = yamlNode.node("settings", "response");
                hoconNode.node("settings", "content").set(responseNode.getString());
            } catch (SerializationException e) {
                printException(e, "Failed to update " + file.getName() + "file");
                continue;
            }

            try {
                hoconLoader.save(hoconNode);
            } catch (ConfigurateException e) {
                printException(e, "Failed to save " + file.getName() + " file");
            }

            if (!file.delete()) {
                this.logger.warning("Failed to delete " + file.getName() + " file!");
            } else {
                this.migrated = true;
                this.logger.warning("Successfully migrated " + file.getName() + "!");
            }
        }
        return CompletableFuture.runAsync(() -> {});
    }

    public void shutdown() {
        if (this.migrated) {
            MigrationHoconPlugin.disableMessage(plugin, "The migration process has been successful");
        } else {
            MigrationHoconPlugin.disableMessage(plugin, "There was nothing to migrate");
        }
    }

    private void printException(final Exception exception, final String message) {
        this.logger.warning(message + ": " + exception.getMessage());

        final Throwable cause = exception.getCause();
        if (cause != null) {
            cause.printStackTrace();
        }
    }
}
