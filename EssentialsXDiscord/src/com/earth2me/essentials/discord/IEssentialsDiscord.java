package com.earth2me.essentials.discord;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IEssentialsDiscord {
    CompletableFuture<Void> sendChatMessage(MessageType type, Player player, String message);

    CompletableFuture<Void> sendPlayerMessage(MessageType type, Player player);

    CompletableFuture<Void> sendStatusMessage(MessageType type, Player controller, Player affected, String message);

    CompletableFuture<Void> sendFormattedMessage(MessageType type, Map<String, Object> tokenMap);

    CompletableFuture<Void> sendRawMessage(MessageType type, String message);

    CompletableFuture<Void> sendRawMessage(String type, String message);
}
