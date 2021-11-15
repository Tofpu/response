package io.tofpu.response.util;

import io.tofpu.response.util.config.MyConfiguration;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;

public class ConfigManager {
    private final static ConfigManager INSTANCE = new ConfigManager();
    public static ConfigManager getInstance() {
        return INSTANCE;
    }
    private HoconConfigurationLoader loader;
    private CommentedConfigurationNode node;
    private MyConfiguration configuration;

    private ConfigManager() {}

    public void load(final File directory) {
        this.loader = HoconConfigurationLoader.builder()
                .path(directory.toPath().resolve("config.conf"))
                .defaultOptions(configurationOptions -> configurationOptions.shouldCopyDefaults(true))
                .build();

        try {
            this.node = loader.load();
        } catch (ConfigurateException e) {
            Logger.warn("An error occurred while loading this configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.printStackTrace();
            }
            return;
        }

        try {
            this.configuration = node.get(MyConfiguration.class);
        } catch (SerializationException e) {
            Logger.warn("An error occurred while converting MyConfiguration: " + e.getMessage());
            if (e.getCause() != null) {
                e.printStackTrace();
            }
        }
    }

    public MyConfiguration getConfiguration() {
        return this.configuration;
    }

    public void save() {
        if (this.node == null) {
            Logger.warn("Attempted to save config.conf while the configuration is null");
            return;
        }
        try {
            this.loader.save(node);
        } catch (ConfigurateException e) {
            Logger.warn("An error occurred while saving this configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.printStackTrace();
            }
        }
    }
}
