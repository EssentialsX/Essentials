package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.chat.EssentialsChat;
import net.ess3.provider.AbstractChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;

public class ChatHandler extends AbstractChatHandler {
    public ChatHandler(Essentials ess, EssentialsChat essChat) {
        super(ess, essChat);
    }

    public void registerListeners() {
        final PluginManager pm = essChat.getServer().getPluginManager();
        pm.registerEvents(new ChatLowest(), essChat);
        pm.registerEvents(new ChatNormal(), essChat);
        pm.registerEvents(new ChatHighest(), essChat);
    }

    private AbstractChatEvent wrap(final AsyncPlayerChatEvent event) {
        return new SpigotChatEvent(event);
    }

    private final class ChatLowest implements ChatListener {
        @Override
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            handleChatFormat(wrap(event));
        }
    }

    private final class ChatNormal implements ChatListener {
        @Override
        @EventHandler(priority = EventPriority.NORMAL)
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            handleChatRecipients(wrap(event));
        }
    }

    private final class ChatHighest implements ChatListener {
        @Override
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            final AbstractChatEvent absEvent = wrap(event);
            handleChatPostFormat(absEvent);
            handleChatSubmit(absEvent);
        }
    }
}
