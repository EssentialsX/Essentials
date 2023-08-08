package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.essentialsx.api.v2.ChatType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatProcessingCache {

    private final Map<Player, ProcessedChat> chats = Collections.synchronizedMap(new HashMap<>());

    public ProcessedChat getProcessedChat(final Player player) {
        return chats.get(player);
    }

    public void setProcessedChat(final Player player, final ProcessedChat chat) {
        chats.put(player, chat);
    }

    public void clearProcessedChat(final Player player) {
        chats.remove(player);
    }

    public abstract static class Chat {
        private final User user;
        private final ChatType type;
        private final String originalMessage;
        protected long radius;

        protected Chat(User user, ChatType type, String originalMessage) {
            this.user = user;
            this.type = type;
            this.originalMessage = originalMessage;
        }

        public User getUser() {
            return user;
        }

        public ChatType getType() {
            return type;
        }

        public String getOriginalMessage() {
            return originalMessage;
        }

        public long getRadius() {
            return radius;
        }

        public final String getLongType() {
            return type == ChatType.UNKNOWN ? "chat" : "chat-" + type.key();
        }
    }

    public static class ProcessedChat extends Chat {
        private final Trade charge;

        public ProcessedChat(final User user, final ChatType type, final String originalMessage) {
            super(user, type, originalMessage);
            this.charge = new Trade(getLongType(), user.getEssentials());
        }

        public void setRadius(final long radius) {
            this.radius = radius;
        }

        public Trade getCharge() {
            return charge;
        }
    }
}
