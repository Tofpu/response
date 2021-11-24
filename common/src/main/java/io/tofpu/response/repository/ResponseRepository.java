package io.tofpu.response.repository;

import io.tofpu.response.Response;
import io.tofpu.response.provider.AbstractLoggerProvider;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class ResponseRepository {
    private final static int UPDATE_INTERVAL = 5;
    private final static String DIRECTORY = "response";
    private final static String[] OLD_RESPONSE_PATH = {"settings", "response"};
    private final static String[] RESPONSE_PATH = {"settings", "content"};

    private final static String EMPTY_IDENTIFIER = "Attempted to register a response with no given/unknown identifier!";
    private final static String EMPTY_RESPONSE = "Attempted to register an \"%s\" response with an empty response!";

    private final static String REGISTRATION_TWICE = "Attempted to register an \"%s\" response twice!";
    private final static String RESPONSE_LOADED = "Loaded \"%s\" response";
    private final static String FLUSH_FAILURE = "Failed attempt to save \"%s\" response file!";

    private final static String DELETION_UNKNOWN = "Attempted to delete a null response. Impossible!";
    private final static String DELETION_FAILURE = "Failed to delete %s response file!";

    private final static String OUTDATED_FILE_EXTENSION = "We discovered an old file in your response folder. Download the ResponseTransformer plugin from Spigot to get your outdated files working!";

    private final static Pattern PATTERN = Pattern.compile("^[A-Za-z0-9_-]*$");

    private final AbstractLoggerProvider logger;
    private final File parent, directory;
    private final Map<String, Response> responses;
    private final Timer timer;

    private boolean detectedYml = false;
    private boolean startup = true;

    public ResponseRepository(final AbstractLoggerProvider abstractLoggerProvider,
            final File parent) {
        this.logger = abstractLoggerProvider;
        this.parent = parent;
        this.directory = new File(parent, DIRECTORY);
        this.responses = new ConcurrentHashMap<>();
        this.timer = new Timer();

        final long toMillis = TimeUnit.MINUTES.toMillis(UPDATE_INTERVAL);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                flush(true);
            }
        }, toMillis, toMillis);
    }

    public void load() {
        // if the directory doesn't exist, mkdir & skip
        if (!this.directory.exists()) {
            this.directory.mkdirs();
            return;
        }

        for (final File file : this.directory.listFiles()) {
            final String identifier = file.getName();
            final String fileExtension = file.getName().split("\\.")[1].replace("//.", "");

            if (!this.detectedYml && fileExtension.equals("yml")) {
                this.detectedYml = true;
                this.logger.warn(OUTDATED_FILE_EXTENSION);
                continue;
            }

            // if the file name doesn't end with .yml, skip
            if (fileExtension.isEmpty() || !fileExtension.contains("conf")) {
                continue;
            }

            final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .file(file)
                    .build();
            final ConfigurationNode node;
            try {
                node = loader.load();
            } catch (ConfigurateException e) {
                this.logger.warn("Failed to load " + identifier + ": " + e.getMessage());
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }
                continue;
            }

            final String response = node.node(RESPONSE_PATH).getString();

            // if the response is null, skip
            if (response == null) {
                continue;
            }

            // attempt to register the response data
            register(identifier.replace("." + fileExtension, ""), response);
        }
        this.startup = false;
    }

    public Response register(final String identifier, final String content) {
        // if the identifier is null or empty, throw an error & return null
        if (identifier == null || identifier.isEmpty() || !PATTERN.matcher(identifier)
                .matches()) {
            this.logger.warn(EMPTY_IDENTIFIER);
//            Logger.warn(EMPTY_IDENTIFIER);
            return null;
        } else if (content == null || content.isEmpty()) { // if the provided
            // content is null or empty, throw an error & return null
            this.logger.warn(String.format(EMPTY_RESPONSE, identifier));
//            Logger.warn(String.format(EMPTY_RESPONSE, identifier));
            return null;
        }

        // if the response that associates with the identifier is already
        // present, pop out a warning and return null
        if (findResponseBy(identifier).isPresent()) {
            this.logger.warn(String.format(REGISTRATION_TWICE, identifier));
//            Logger.warn(String.format(REGISTRATION_TWICE, identifier));
            return null;
        }

        // create our class with the provided identifier & content response
        final Response response = new Response(identifier, content);

        // insert the response class to our map
        this.responses.put(identifier, response);

        // if we're starting up, we'll send a log regarding the registration
        // status
        if (this.startup) {
            // success attempt log
            this.logger.log(String.format(RESPONSE_LOADED, identifier));
//            Logger.log(String.format(RESPONSE_LOADED, identifier));
        }

        return response;
    }

    public Optional<Response> findResponseBy(final String identifier) {
        // if the identifier is null or empty, return an empty optional
        if (identifier == null || identifier.isEmpty()) {
            return Optional.empty();
        }
        // returning the response associated with the identifier from our map
        return Optional.ofNullable(this.responses.get(identifier));
    }

    public void flush(final boolean async) {
        if (async) {
            CompletableFuture.runAsync(this::flush);
        } else {
            this.flush();
            // empty our responses map
            this.responses.clear();
            // cancelling our timer due to shutting down
            this.timer.cancel();
        }
    }

    private void flush() {
        // TODO: flush the data to their own dedicated file code here...
        for (final Response response : this.responses.values()) {
            // create our decicated file for the response data
            final File file = new File(this.directory,
                    response.getIdentifier() + ".conf");
            // load an instance of YamlConfiguration to set our data
            final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .file(file)
                    .prettyPrinting(true)
                    .build();
            final ConfigurationNode node;
            try {
                node = loader.load();
            } catch (ConfigurateException e) {
                e.printStackTrace();
                continue;
            }

            // set the path with our given response
            try {
                node.node(RESPONSE_PATH).set(response.getResponse());
//                node.node("settings", "response").set(response.getResponse());
            } catch (SerializationException e) {
                e.printStackTrace();
            }
            try {
                // save the changes
                loader.save(node);
            } catch (IllegalArgumentException | IOException e) {
                this.logger.warn(String.format(FLUSH_FAILURE, response
                        .getIdentifier()));
//                Logger.warn(String.format(FLUSH_FAILURE, response
//                        .getIdentifier()));
                e.printStackTrace();
            }
        }
    }

    public void delete(final Response response) {
        if (response == null) {
            this.logger.debug(DELETION_UNKNOWN);
//            Logger.debug(DELETION_UNKNOWN);
            return;
        }
        final String identifier = response.getIdentifier();
        final File file = new File(directory, identifier + ".yml");
        if (file.exists() && !file.delete()) {
            this.logger.debug(String.format(DELETION_FAILURE, identifier));
//            Logger.debug(String.format(DELETION_FAILURE, identifier));
        }

        this.responses.remove(identifier, response);
    }

    public File getParent() {
        return this.parent;
    }

    public File getDirectory() {
        return this.directory;
    }
}
