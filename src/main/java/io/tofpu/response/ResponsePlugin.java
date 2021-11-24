package io.tofpu.response;

import io.tofpu.response.listener.AsyncChatListener;
import io.tofpu.response.object.handler.ResponseHandler;
import io.tofpu.response.object.repository.ResponseRepository;
import io.tofpu.response.util.ChatUtility;
import io.tofpu.response.config.manager.ConfigManager;
import io.tofpu.response.util.Logger;
import io.tofpu.response.util.UpdateChecker;
import io.tofpu.response.config.category.GeneralCategory;
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

        // running the update checker async
        UpdateChecker.init(this, 97614).requestUpdateCheck().whenComplete((updateResult, throwable) -> {
            final java.util.logging.Logger logger = getLogger();

            if (updateResult.getReason() == UpdateChecker.UpdateReason.NEW_UPDATE) {
                logger.warning("You're not on the latest version of Response!");
                logger.warning("It's highly recommended downloading the " +
                        "latest version at https://www.spigotmc.org/resources/97614/!");
            } else if (updateResult.getReason() == UpdateChecker.UpdateReason.UP_TO_DATE) {
                logger.warning("You're using the latest version of Response!");
            }
        });

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
