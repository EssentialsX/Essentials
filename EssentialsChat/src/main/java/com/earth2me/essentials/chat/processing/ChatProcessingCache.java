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

    public IntermediateChat getIntermediateChat(final Player player) {
        return intermediateChats.get(player);
    }

    public void setIntermediateChat(final Player player, final IntermediateChat intermediateChat) {
        intermediateChats.put(player, intermediateChat);
    }

    public IntermediateChat clearIntermediateChat(final Player player) {
        return intermediateChats.remove(player);
    }

    public ProcessedChat getProcessedChat(final Player player) {
        return processedChats.getIfPresent(player);
    }

    public void setProcessedChat(final Player player, final ProcessedChat chat) {
        processedChats.put(player, chat);
    }

    public ProcessedChat clearProcessedChat(final Player player) {
        final ProcessedChat chat = processedChats.getIfPresent(player);
        processedChats.invalidate(player);
        return chat;
    }

    public Chat getIntermediateOrElseProcessedChat(final Player player) {
        final IntermediateChat chat = getIntermediateChat(player);
        if (chat != null) {
            return chat;
        }
        return getProcessedChat(player);
    }

    public abstract static class Chat {
        private final User user;
        private final String type;
        private final String originalMessage;
        protected long radius;

        protected Chat(User user, String type, String originalMessage) {
            this.user = user;
            this.type = type;
            this.originalMessage = originalMessage;
        }

        public User getUser() {
            return user;
        }

        public String getType() {
            return type;
        }

        public String getOriginalMessage() {
            return originalMessage;
        }

        public long getRadius() {
            return radius;
        }

        public final String getLongType() {
            return type.length() == 0 ? "chat" : "chat-" + type;
        }
    }

    public static class ProcessedChat extends Chat {
        private final String message;
        private final String format;
        private final Trade charge;

        public ProcessedChat(final IEssentials ess, final IntermediateChat sourceChat) {
            super(sourceChat.getUser(), sourceChat.getType(), sourceChat.getOriginalMessage());
            this.message = sourceChat.messageResult;
            this.format = sourceChat.formatResult;
            this.radius = sourceChat.radius;
            this.charge = new Trade(getLongType(), ess);
        }

        public String getMessage() {
            return message;
        }

        public String getFormat() {
            return format;
        }

        public Trade getCharge() {
            return charge;
        }
    }

    public static class IntermediateChat extends Chat {
        private String messageResult;
        private String formatResult;

        public IntermediateChat(final User user, final String type, final String originalMessage) {
            super(user, type, originalMessage);
        }

        public void setRadius(final long radius) {
            this.radius = radius;
        }

        public String getMessageResult() {
            return messageResult;
        }

        public void setMessageResult(String messageResult) {
            this.messageResult = messageResult;
        }

        public String getFormatResult() {
            return formatResult;
        }

        public void setFormatResult(String formatResult) {
            this.formatResult = formatResult;
        }
    }

}
