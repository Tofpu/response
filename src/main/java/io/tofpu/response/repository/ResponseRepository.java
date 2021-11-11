package io.tofpu.response.repository;

import io.tofpu.response.object.Response;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResponseRepository {
    private static final String DIRECTORY = "response";
    private final static String RESPONSE_PATH = "settings.response";
    private final static String EMPTY_IDENTIFIER = "Attempted to register a " +
            "response with no given identifier!";
    private final static String EMPTY_RESPONSE = "Attempted to register an " +
            "\"%s\" response with an empty response!";
    private final static String REGISTRATION_TWICE = "Attempted to register " +
            "an \"%s\" response twice!";

    private final File parent, directory;
    private final Map<String, Response> responses = new HashMap<>();

    public ResponseRepository(final File parent) {
        this.parent = parent;
        this.directory = new File(parent, DIRECTORY);
    }

    public void load() {
        if (!directory.exists()) {
            directory.mkdirs();
            return;
        }

        for (final File file : directory.listFiles()) {
            final String identifier = file.getName();
            if (!identifier.endsWith(".yml")) {
                continue;
            }

            // TODO: TEMPORALLY CODE?
            final FileConfiguration configuration =
                    YamlConfiguration.loadConfiguration(file);
            final String response = configuration.getString(RESPONSE_PATH);
            if (response.isEmpty()) {
                continue;
            }

            register(identifier.replace(".yml", ""), response);
        }
    }

    public Response register(final String identifier, final String content) {
        if (identifier == null || identifier.isEmpty()) {
            Bukkit.getLogger().warning(EMPTY_IDENTIFIER);
            return null;
        } else if (content == null || content.isEmpty()) {
            Bukkit.getLogger().warning(String.format(EMPTY_RESPONSE, identifier));
            return null;
        }

        if (findResponseBy(identifier).isPresent()) {
            Bukkit.getLogger().warning(String.format(REGISTRATION_TWICE, identifier));
            return null;
        }
        final Response response = new Response(identifier, content);
        synchronized (this.responses) {
            responses.put(identifier, response);
        }

        return response;
    }

    public Optional<Response> findResponseBy(final String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return Optional.empty();
        }
        final Response response;
        synchronized (this.responses) {
            response = this.responses.get(identifier);
        }
        return Optional.ofNullable(response);
    }

    public void flush() {
        synchronized (this.responses) {
            // TODO: flush the data to their own dedicated file code here...
            for (final Response response : this.responses.values()) {
                final File file = new File(this.directory,
                        response.getIdentifier() + ".yml");
                final FileConfiguration configuration =
                        YamlConfiguration.loadConfiguration(file);

                configuration.set(RESPONSE_PATH, response.getResponse());
                try {
                    configuration.save(file);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            this.responses.clear();
        }
    }

    public File getParent() {
        return parent;
    }

    public File getDirectory() {
        return directory;
    }
}
