package io.tofpu.response.listener;

import io.tofpu.response.object.Response;
import io.tofpu.response.repository.ResponseRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class AsyncChatListener implements Listener {
    private final static String MISSING_IDENTIFIER = "Missing Identifier, " +
            "Please ensure to follow this format: ?identifier";
    private final static String INVALID_FORMAT = "Invalid Format, Please " +
            "ensure to follow this format: #identifier:response!";
    private final static String FAILURE_ATTEMPT = "An attempt to register %s " +
            "response has failed. Check your console for further details.";
    private static final String SUCCESS_ATTEMPT = "You have successfully " +
            "registered %s response!";
    private final ResponseRepository repository;

    public AsyncChatListener(final ResponseRepository repository) {
        this.repository = repository;
    }

    @EventHandler(ignoreCancelled = true)
    private void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final String message = event.getMessage();
        final String content = message.isEmpty() ? "" : message.substring(1,
                message.length() - 1);

        System.out.println(message);
        System.out.println(content);

        if (content.isEmpty()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(MISSING_IDENTIFIER);
            return;
        }

        switch (message.substring(0, 1)) {
            case "?": // trying to retrieve a response
                retrieveResponse(event, content);
                break;
            case "#": // trying to create a response
                createResponse(event, content);
                break;
        }
    }

    private void retrieveResponse(final AsyncPlayerChatEvent event,
            final String content) {
        // trying to retrieve a response out of the message's content
        final Optional<Response> response = repository.findResponseBy(content);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!response.isPresent()) {
            return;
        }

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
        if (args.length < 1) {
            player.sendMessage(INVALID_FORMAT);
            return;
        }

        // attempting to register the response
        if (this.repository.register(args[0], args[1]) == null) {
            player.sendMessage(String.format(FAILURE_ATTEMPT, args[0]));
        } else {
            player.sendMessage(String.format(SUCCESS_ATTEMPT, args[0]));
        }
    }

    public ResponseRepository getRepository() {
        return repository;
    }
}
