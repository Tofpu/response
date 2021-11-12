package io.tofpu.response.listener;

import io.tofpu.response.handler.ResponseHandler;
import io.tofpu.response.util.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class AsyncChatListener implements Listener {
    private final ResponseHandler handler;

    public AsyncChatListener(final ResponseHandler handler) {
        this.handler = handler;
    }

    @EventHandler(ignoreCancelled = true)
    private void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        // our event message
        final String message = event.getMessage();
        // if the message is empty or the message's length is lower than 2,
        // set it to empty, otherwise; ignore the first given character
        final String content = message.isEmpty() || message.length() < 2 ? "" : message
                .substring(1);

        Logger.debug(message);
        Logger.debug(content);

        // our operation type
        final ResponseHandler.ResponseOperationType operationType;
        // retrieving our first given character from the message variable
        switch (message.substring(0, Math.min(message.length(), 1))) {
            // if our first given character is #
            case "#": // trying to create a response
                operationType = ResponseHandler.ResponseOperationType.REGISTER;
                break;
            // if our first given character is ?
            case "?": // trying to retrieve a response
                operationType = ResponseHandler.ResponseOperationType.RETRIEVE;
                break;
            // if our first given character is $
            case "$":
                operationType = ResponseHandler.ResponseOperationType.MODIFY;
                break;
            default:
                return;
        }

        // if the given player doesn't have the appropriate permission, return
        if (!operationType.hasPermission(event.getPlayer())) {
            return;
        }

        handler.response(ResponseHandler.ResponseOperation.of(operationType, event, content));
    }
}
