package io.tofpu.response;

import io.tofpu.response.listener.AsyncChatListener;
import io.tofpu.response.repository.ResponseHandler;
import io.tofpu.response.repository.ResponseRepository;
import io.tofpu.response.util.Logger;
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

        // loading our responses
        this.repository.load();

        // registering the chat event
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
