package com.earth2me.essentials.chat;

import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;


public class EssentialsChatPlayerListenerHighest extends EssentialsChatPlayer {
    EssentialsChatPlayerListenerHighest(final Server server, final IEssentials ess, final Map<AsyncPlayerChatEvent, ChatStore> chatStorage) {
        super(server, ess, chatStorage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Override
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final ChatStore chatStore = delChatStore(event);
        if (isAborted(event) || chatStore == null) {
            return;
        }

        // This file should handle charging the user for the action before returning control back
        charge(event, chatStore);
    }
}
