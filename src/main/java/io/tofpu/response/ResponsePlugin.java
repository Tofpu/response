package io.tofpu.response;

import io.tofpu.response.listener.AsyncChatListener;
import io.tofpu.response.handler.ResponseHandler;
import io.tofpu.response.repository.ResponseRepository;
import io.tofpu.response.util.ChatUtility;
import io.tofpu.response.util.ConfigManager;
import io.tofpu.response.util.Logger;
import io.tofpu.response.util.config.GeneralCategory;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ResponsePlugin extends JavaPlugin {
    private final ResponseHandler handler;
    private final ResponseRepository repository;

    public ResponsePlugin() {
        this.repository = new ResponseRepository(getDataFolder());
        this.handler = new ResponseHandler(this.repository);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // setting the bukkit's logger
        Logger.setLogger(getLogger());

        // loading metrics for our plugin
        new Metrics(this, 13310);

        // loading our responses
        this.repository.load();

        final ConfigManager configManager = ConfigManager.getInstance();
        // loading our config
        configManager.load(getDataFolder());

        // general category of our configuration
        final GeneralCategory generalCategory = configManager.getConfiguration()
                .getGeneralCategory();
        // if isEnabledPapiSupport returns true, we'll attempt to enable
        // support for said plugin
        if (generalCategory.isPAPISupportEnabled()) {
            final boolean papiEnabled =
                    Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

            if (papiEnabled) {
                Logger.log("Enabled PlaceholderAPI Support");
            }

            ChatUtility.setSupportPlaceholderAPI(papiEnabled);
        }

        // start listening to chat event
        Bukkit.getPluginManager()
                .registerEvents(new AsyncChatListener(this.handler), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.repository.flush(false);
    }

    public ResponseRepository getRepository() {
        return this.repository;
    }
}
