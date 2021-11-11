package io.tofpu.response.listener;

import io.tofpu.response.object.Response;
import io.tofpu.response.repository.ResponseRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public final class AsyncChatListener implements Listener {
    private final static String INVALID_FORMAT = "Invalid format. Please follow the format: #identifier:response!";
    private final static String REGISTRATION_FAILURE = "An attempt to register \"%s\" " + "response has failed. Check your console for further details.";
    private static final String REGISTRATION_SUCCESSFUL = "You have successfully " + "registered \"%s\" response!";
    private static final String MODIFICATION_SUCCESSFUL = "You have " + "successfully modified \"%s\" response!";
    private final static String MODIFICATION_INVALID_FORMAT = "Invalid format" + ". Please follow the format: $identifier:newResponse";

    private final ResponseRepository repository;
    public AsyncChatListener(final ResponseRepository repository) {
        this.repository = repository;
    }

    @EventHandler(ignoreCancelled = true)
    private void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        // our event message
        final String message = event.getMessage();
        // if the message is empty or the message's length is lower than 2,
        // set it to empty, otherwise; ignore the first given character
        final String content = message.isEmpty() || message.length() < 2 ? "" :
                message.substring(1);

        // debug purposes, you can ignore this
        System.out.println(message);
        System.out.println(content);

        // retrieving our first given character from the message variable
        switch (message.substring(0, 1)) {
            // if our first given character is ?
            case "?": // trying to retrieve a response
                retrieveResponse(event, content);
                break;
            // if our first given character is #
            case "#": // trying to create a response
                createResponse(event, content);
                break;
            case "$":
                modifyResponse(event, content);
                break;
        }
    }

    private void retrieveResponse(final AsyncPlayerChatEvent event,
            final String content) {
        // splitting the content to our appropriate format
        final String[] args = content.split(":");
        // trying to retrieve a response out of the message's content
        final Optional<Response> response = repository.findResponseBy(args[0]);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!response.isPresent()) {
            return;
        }

        // replace the ?identifier with our given response associated with
        // the identifier
        event.setMessage(response.get().getResponse());
    }

    private void createResponse(final AsyncPlayerChatEvent event,
            final String content) {
        // format: identifier:response -- array: 0:1
        // example: discord:&eYou can join our discord at https:://www.discord.com

        final Player player = event.getPlayer();
        // splitting the content to our appropriate format
        final String[] args = content.split(":");

        event.setCancelled(true);

        // if the args length is lower than the required args (1), return
        if (args.length <= 1) {
            player.sendMessage(INVALID_FORMAT);
            return;
        }

        // attempting to register the response
        if (this.repository.register(args[0], args[1]) == null) {
            player.sendMessage(String.format(REGISTRATION_FAILURE, args[0]));
        } else {
            player.sendMessage(String.format(REGISTRATION_SUCCESSFUL, args[0]));
        }
    }

    public void modifyResponse(final AsyncPlayerChatEvent event,
            final String content) {
        final Player player = event.getPlayer();
        final String[] args = content.split(":");

        event.setCancelled(true);

        // if the args length is lower than the required args (1), return
        if (args.length <= 1) {
            player.sendMessage(MODIFICATION_INVALID_FORMAT);
            return;
        }

        // trying to retrieve a response out of the message's content
        final Optional<Response> optionalResponse =
                repository.findResponseBy(args[0]);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!optionalResponse.isPresent()) {
            return;
        }

        final Response response = optionalResponse.get();
        // TODO: I apparently couldn't use our local variable to synchronize
        //  with...
        synchronized (optionalResponse.get()) {
            response.setResponse(args[1]);
        }

        player.sendMessage(String.format(MODIFICATION_SUCCESSFUL, content));
    }

    public ResponseRepository getRepository() {
        return repository;
    }
}
