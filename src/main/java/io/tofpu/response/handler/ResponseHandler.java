package io.tofpu.response.handler;

import io.tofpu.response.object.Response;
import io.tofpu.response.repository.ResponseRepository;
import io.tofpu.response.util.ChatUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ResponseHandler {
    private final static String PERMISSION_NODE = "response.";
    private final static String REGISTRATION_INVALID_FORMAT = "Invalid format. Please follow the format: #identifier:response!";
    private final static String REGISTRATION_FAILURE = "An attempt to register \"%s\" response has failed. Check your console for further details.";
    private final static String REGISTRATION_SUCCESSFUL = "You have successfully registered \"%s\" response!";

    private final static String MODIFICATION_SUCCESSFUL = "You have successfully modified \"%s\" response!";
    private final static String MODIFICATION_INVALID_FORMAT = "Invalid format. Please follow the format: $identifier:newResponse";

    private final static String DELETION_SUCCESSFUL = "You have successfully deleted \"%s\" response";

    private final ResponseRepository repository;

    public ResponseHandler(final ResponseRepository repository) {
        this.repository = repository;
    }

    public void response(final ResponseOperation operation) {
        final ResponseOperationType type = operation.getType();
        final AsyncPlayerChatEvent event = operation.getEvent();
        final String content = operation.getContent();

        final Player player = event.getPlayer();
        // splitting the content to our appropriate format
        final String[] args = content.split(":");
        // if the operationType does not equal to Register or Modify, include
        // more checks
        if (type == ResponseOperationType.REGISTER || type == ResponseOperationType.MODIFY) {
            event.setCancelled(true);
            // if the args length is lower than the required args (1), return
            if (args.length <= 1) {
                player.sendMessage(ChatUtility.colorize(type == ResponseOperationType.REGISTER ? REGISTRATION_INVALID_FORMAT : MODIFICATION_INVALID_FORMAT));
                return;
            }
        }

        switch (type) {
            case REGISTER:
                createResponse(event, args);
                break;
            case RETRIEVE:
                retrieveResponse(event, args);
                break;
            case MODIFY:
                modifyResponse(event, args);
                break;
            case DELETE:
                deleteResponse(event, args);
                break;
        }
    }

    private void retrieveResponse(final AsyncPlayerChatEvent event,
            final String[] args) {
        final String identifier = args[0];
        // trying to retrieve a response out of the message's content
        final Optional<Response> response = repository.findResponseBy(identifier);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!response.isPresent()) {
            return;
        }

        // replacing the ?identifier with our given response associated with
        // the identifier
        event.setMessage(ChatUtility.colorize(response.get().getResponse()));
    }

    private void createResponse(final AsyncPlayerChatEvent event,
            final String[] args) {
        // format: identifier:response -- array: 0:1
        // example: discord:&eYou can join our discord at https:://www.discord.com

        final Player player = event.getPlayer();
        final String identifier = args[0];
        final String response = args[1];

        // attempting to register the response
        if (this.repository.register(identifier, response) == null) {
            player.sendMessage(ChatUtility.colorize(String.format(REGISTRATION_FAILURE, identifier)));
        } else {
            player.sendMessage(ChatUtility.colorize(String.format(REGISTRATION_SUCCESSFUL, identifier)));
        }
    }

    private void modifyResponse(final AsyncPlayerChatEvent event,
            final String[] args) {
        final Player player = event.getPlayer();
        final String identifier = args[0];
        final String response = args[1];

        // attempting to retrieve a response out of the message's content
        final Optional<Response> value =
                repository.findResponseBy(identifier);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!value.isPresent()) {
            return;
        } else {
            value.get().setResponse(response);
        }

        player.sendMessage(ChatUtility.colorize(String.format(MODIFICATION_SUCCESSFUL, identifier)));
    }

    private void deleteResponse(final AsyncPlayerChatEvent event,
            final String[] args) {
        final Player player = event.getPlayer();
        final String identifier = args[0];

        // attempting to retrieve a response out of the message's content
        final Optional<Response> value =
                repository.findResponseBy(identifier);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!value.isPresent()) {
            return;
        } else {
            event.setCancelled(true);
        }
        // attempt to delete the response from our map & file
        repository.delete(value.get());
        player.sendMessage(ChatUtility.colorize(String.format(DELETION_SUCCESSFUL, identifier)));
    }

    public ResponseRepository getRepository() {
        return repository;
    }

    public static class ResponseOperation {
        private final ResponseOperationType type;
        private final AsyncPlayerChatEvent event;
        private final String content;

        public static ResponseOperation of(final ResponseOperationType type,
                final AsyncPlayerChatEvent event, final String content) {
            return new ResponseOperation(type, event, content);
        }

        private ResponseOperation(final ResponseOperationType type, final AsyncPlayerChatEvent event, final String content) {
            this.type = type;
            this.event = event;
            this.content = content;
        }

        public AsyncPlayerChatEvent getEvent() {
            return event;
        }

        public String getContent() {
            return content;
        }

        private ResponseOperationType getType() {
            return type;
        }
    }

    public enum ResponseOperationType {
        REGISTER, RETRIEVE, MODIFY, DELETE;

        public boolean hasPermission(final Player player) {
            switch (this) {
                case REGISTER:
                case MODIFY:
                case DELETE:
                    return player.hasPermission(PERMISSION_NODE + this.name());
                default:
                    return true;
            }
        }
    }
}
