package io.tofpu.response.handler;

import io.tofpu.response.Response;
import io.tofpu.response.provider.AbstractEventProvider;
import io.tofpu.response.provider.AbstractUserProvider;
import io.tofpu.response.repository.ResponseRepository;

import java.util.Arrays;
import java.util.Optional;

public class ResponseHandler {
    private final static String PERMISSION_NODE = "response.";
    private final static String REGISTRATION_INVALID_FORMAT = "&cInvalid " + "format. Please follow the format: &4#identifier:response!";
    private final static String REGISTRATION_FAILURE = "&cAn attempt to " + "register \"%s\" response has failed. Check your console for further details.";
    private final static String REGISTRATION_SUCCESSFUL = "&eYou have " + "successfully registered &6\"%s\" &eresponse!";

    private final static String MODIFICATION_SUCCESSFUL = "&eYou have " + "successfully modified &6\"%s\" &eresponse!";
    private final static String MODIFICATION_INVALID_FORMAT = "&cInvalid " + "format. Please follow the format: &4$identifier:newResponse";

    private final static String DELETION_SUCCESSFUL = "&eYou have " + "successfully deleted &6\"%s\" &eresponse";

    private final ResponseRepository responseRepository;

    public ResponseHandler(final ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    public void response(final ResponseOperation operation) {
        final ResponseOperationType type = operation.getType();
        final AbstractEventProvider event = operation.getEvent();
        final String content = event.content();

        final AbstractUserProvider player = event.getUserProvider();
        // splitting the content to our appropriate format
        final String[] args = content.split(":");
        // if the operationType does not equal to Register or Modify, include
        // more checks
        if (type == ResponseOperationType.REGISTER || type == ResponseOperationType.MODIFY) {
            event.setCancelled(true);
            // if the args length is lower than the required args (1), return
            System.out.println(Arrays.toString(args));
            if (args.length < 1) {
                player.sendMessage(type == ResponseOperationType.REGISTER ? REGISTRATION_INVALID_FORMAT : MODIFICATION_INVALID_FORMAT);
                return;
            }
        }

        switch (type) {
            case REGISTER:
                createResponse(event, args, content);
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

    private void retrieveResponse(final AbstractEventProvider event,
            final String[] args) {
        final String identifier = args[0];
        // trying to retrieve a response out of the message's content
        final Optional<Response> response = responseRepository.findResponseBy(identifier);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!response.isPresent()) {
            return;
        }

        // replacing the ?identifier with our given response associated with
        // the identifier
        event.getUserProvider().sendMessage(response.get().getResponse());
//        event.setMessage(ChatUtility.colorize(event.getUserProvider(),
//                response.get().getResponse()));
    }

    private void createResponse(final AbstractEventProvider event,
            final String[] args, final String content) {
        // format: identifier:response -- array: 0:1
        // example: discord:&eYou can join our discord at https:://www.discord.com

        final AbstractUserProvider player = event.getUserProvider();
        final String identifier = args[0];
        final String response = content.replace(identifier + ":", "");

        // attempting to register the response
        if (this.responseRepository.register(identifier, response) == null) {
            player.sendMessage(String.format(REGISTRATION_FAILURE, identifier));
        } else {
            player.sendMessage(String.format(REGISTRATION_SUCCESSFUL, identifier));
        }
    }

    private void modifyResponse(final AbstractEventProvider event,
            final String[] args) {
        final AbstractUserProvider player = event.getUserProvider();
        final String identifier = args[0];
        final String response = args[1];

        // attempting to retrieve a response out of the message's content
        final Optional<Response> value =
                responseRepository.findResponseBy(identifier);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!value.isPresent()) {
            return;
        } else {
            value.get().setResponse(response);
        }

        player.sendMessage(String.format(MODIFICATION_SUCCESSFUL, identifier));
    }

    private void deleteResponse(final AbstractEventProvider event,
            final String[] args) {
        final AbstractUserProvider player = event.getUserProvider();
        final String identifier = args[0];

        // attempting to retrieve a response out of the message's content
        final Optional<Response> value =
                responseRepository.findResponseBy(identifier);

        // if the message's content identifier isn't listed on the
        // repository, return
        if (!value.isPresent()) {
            return;
        } else {
            event.setCancelled(true);
        }
        // attempt to delete the response from our map & file
        responseRepository.delete(value.get());
        player.sendMessage(String.format(DELETION_SUCCESSFUL, identifier));
    }

    public ResponseRepository getResponseRepository() {
        return responseRepository;
    }

    public static class ResponseOperation {
        private final ResponseOperationType type;
        private final AbstractEventProvider event;

        public static ResponseOperation of(final ResponseOperationType type,
                final AbstractEventProvider event) {
            return new ResponseOperation(type, event);
        }

        private ResponseOperation(final ResponseOperationType type,
                final AbstractEventProvider event) {
            this.type = type;
            this.event = event;
        }

        public AbstractEventProvider getEvent() {
            return event;
        }

        private ResponseOperationType getType() {
            return type;
        }
    }

    public enum ResponseOperationType {
        REGISTER, RETRIEVE, MODIFY, DELETE;

        public boolean hasPermission(final AbstractUserProvider player) {
            switch (this) {
                case REGISTER:
                case MODIFY:
                case DELETE:
                    return true;
//                    return player.hasPermission(PERMISSION_NODE + this.name());
                default:
                    return true;
            }
        }
    }
}
