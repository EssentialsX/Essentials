package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatProcessingCache {

    private final Map<Player, IntermediateChat> intermediateChats = Collections.synchronizedMap(new HashMap<>());

    private final Cache<Player, ProcessedChat> processedChats = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public IntermediateChat getIntermediateChat(Player player) {
        return intermediateChats.get(player);
    }

    public void setIntermediateChat(Player player, IntermediateChat intermediateChat) {
        intermediateChats.put(player, intermediateChat);
    }

    public IntermediateChat clearIntermediateChat(Player player) {
        return intermediateChats.remove(player);
    }

    public ProcessedChat getProcessedChat(Player player) {
        return processedChats.getIfPresent(player);
    }

    public void setProcessedChat(Player player, ProcessedChat chat) {
        processedChats.put(player, chat);
    }

    public static abstract class Chat {
        private final User user;
        private final String type;
        private final String originalMessage;

        protected Chat(User user, String type, String originalMessage) {
            this.user = user;
            this.type = type;
            this.originalMessage = originalMessage;
        }

        User getUser() {
            return user;
        }

        String getType() {
            return type;
        }

        public String getOriginalMessage() {
            return originalMessage;
        }

        final String getLongType() {
            return type.length() == 0 ? "chat" : "chat-" + type;
        }
    }

    public static class ProcessedChat extends Chat {
        private final String message;
        private final Trade charge;

        public ProcessedChat(final IEssentials ess, final IntermediateChat sourceChat) {
            super(sourceChat.getUser(), sourceChat.getType(), sourceChat.getOriginalMessage());
            this.charge = new Trade(getLongType(), ess);
            this.message = sourceChat.modifiedMessage;
        }

        public String getMessage() {
            return message;
        }

        public Trade getCharge() {
            return charge;
        }
    }

    public static class IntermediateChat extends Chat {
        private String modifiedMessage;
        private long radius;

        public IntermediateChat(final User user, final String type, final String originalMessage) {
            super(user, type, originalMessage);
        }

        long getRadius() {
            return radius;
        }

        void setRadius(final long radius) {
            this.radius = radius;
        }

        public String getModifiedMessage() {
            return modifiedMessage;
        }

        public void setModifiedMessage(String modifiedMessage) {
            this.modifiedMessage = modifiedMessage;
        }
    }

}
