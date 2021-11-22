package io.tofpu.response.listener;

import io.tofpu.response.handler.ResponseHandler;
import io.tofpu.response.util.ConfigManager;
import io.tofpu.response.util.Logger;
import io.tofpu.response.util.config.GeneralCategory;
import io.tofpu.response.util.config.MyConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class AsyncChatListener implements Listener {
    private final static String[] PREFIXES = {"!", "#", "?"};
    private final ResponseHandler handler;

    public AsyncChatListener(final ResponseHandler handler) {
        this.handler = handler;
    }

    @EventHandler(ignoreCancelled = true)
    private void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        // our event message
        final String message = event.getMessage();
        // our message's prefix
        final String prefix = message.isEmpty() || message.length() <= 1 ? "" : message
                .substring(0, 1);
        // if the message is empty, or the message's length is lower than 2,
        // set it to empty, otherwise; format the message appropriately
        final String content = prefix.isEmpty() ? "" : formatMessage(message);

        Logger.debug(message);
        Logger.debug(content);

        // our operation type
        final ResponseHandler.ResponseOperationType operationType;
        // retrieving our first given character from the message variable
        switch (prefix) {
            // if our first given character is #
            case "#": // attempting to create a response
                operationType = ResponseHandler.ResponseOperationType.REGISTER;
                break;
            // if our first given character is ?
            case "?": // attempting to retrieve a response
                operationType = ResponseHandler.ResponseOperationType.RETRIEVE;
                break;
            // if our first given character is $
            case "$": // attempting to modify a response
                operationType = ResponseHandler.ResponseOperationType.MODIFY;
                break;
            // if our first given character is !
            case "!": // attempting to delete a response
                operationType = ResponseHandler.ResponseOperationType.DELETE;
                break;
            default: // unrelated, go on!
                final GeneralCategory generalCategory = ConfigManager.getInstance()
                        .getGeneralCategory();
                if (!generalCategory.isAutomaticReplyEnabled()) {
                    return;
                }
                operationType = ResponseHandler.ResponseOperationType.AUTOMATIC_RESPONSE;
                break;
        }

        // if the given player doesn't have the appropriate permission, return
        if (!operationType.hasPermission(event.getPlayer())) {
            return;
        }

        this.handler.response(ResponseHandler.ResponseOperation.of(operationType, event, content));
    }

    private String formatMessage(String message) {
        for (final String prefix : PREFIXES) {
            message = message.replaceFirst(prefix, "");
        }
        return message;
    }

    public ResponseHandler getHandler() {
        return this.handler;
    }
}
