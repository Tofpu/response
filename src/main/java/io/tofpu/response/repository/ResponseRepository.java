package io.tofpu.response.repository;

import io.tofpu.response.object.Response;
import io.tofpu.response.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ResponseRepository {
    private static final String DIRECTORY = "response";
    private final static String RESPONSE_PATH = "settings.response";
    private final static String EMPTY_IDENTIFIER = "Attempted to register a " +
            "response with no given identifier!";
    private final static String EMPTY_RESPONSE = "Attempted to register an " +
            "\"%s\" response with an empty response!";
    private final static String REGISTRATION_TWICE = "Attempted to register " +
            "an \"%s\" response twice!";
    private final static String RESPONSE_LOADED = "Loaded \"%s\" response!";
    private final static String FAILURE_FLUSH = "Failed attempt to save \"%s\" response file!";

    private final File parent, directory;
    private final Map<String, Response> responses = new HashMap<>();

    public ResponseRepository(final File parent) {
        this.parent = parent;
        this.directory = new File(parent, DIRECTORY);
    }

    public void load() {
        // if the directory doesn't exist, mkdir & skip
        if (!directory.exists()) {
            directory.mkdirs();
            return;
        }

        for (final File file : directory.listFiles()) {
            final String identifier = file.getName();
            // if the file name doesn't end with .yml, skip
            if (!identifier.endsWith(".yml")) {
                continue;
            }

            final FileConfiguration configuration =
                    YamlConfiguration.loadConfiguration(file);
            final String response = configuration.getString(RESPONSE_PATH);

            // if the response is empty, skip
            if (response.isEmpty()) {
                continue;
            }

            // attempt to register the response data
            register(identifier.replace(".yml", ""), response);
        }
    }

    public Response register(final String identifier, final String content) {
        // if the identifier is null or empty, throw an error & return null
        if (identifier == null || identifier.isEmpty()) {
            Logger.warn(EMPTY_IDENTIFIER);
            return null;
        } else if (content == null || content.isEmpty()) { // if the provided
            // content is null or empty, throw an error & return null
            Logger.warn(String.format(EMPTY_RESPONSE, identifier));
            return null;
        }

        // if the response that associates with the identifier is already
        // present, pop out a warning and return null
        if (findResponseBy(identifier).isPresent()) {
            Logger.warn(String.format(REGISTRATION_TWICE, identifier));
            return null;
        }

        // create our class with the provided identifier & content response
        final Response response = new Response(identifier, content);

        // synchronise the responses map
        synchronized (this.responses) {
            // insert the response class to our map
            responses.put(identifier, response);
        }
        // success attempt log
        Logger.debug(String.format(RESPONSE_LOADED, identifier));

        return response;
    }

    public Optional<Response> findResponseBy(final String identifier) {
        // if the identifier is null or empty, return an empty optional
        if (identifier == null || identifier.isEmpty()) {
            return Optional.empty();
        }
        final Response response;
        // synchronise our responses map
        synchronized (this.responses) {
            // retrieve the response associated with the identifier from our map
            response = this.responses.get(identifier);
        }
        return Optional.ofNullable(response);
    }

    public void flush() {
        // synchronise our responses map
        synchronized (this.responses) {
            // TODO: flush the data to their own dedicated file code here...
            for (final Response response : this.responses.values()) {
                // create our decicated file for the response data
                final File file = new File(this.directory,
                        response.getIdentifier() + ".yml");
                // load an instance of YamlConfiguration to set our data
                final FileConfiguration configuration =
                        YamlConfiguration.loadConfiguration(file);

                // set the path with our given response
                configuration.set(RESPONSE_PATH, response.getResponse());
                try {
                    // save the changes
                    configuration.save(file);
            } catch (IllegalArgumentException | IOException e) {
                Logger.warn(String.format(FAILURE_FLUSH, response
                        .getIdentifier()));
                e.printStackTrace();
            }
            // empty our responses map
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
