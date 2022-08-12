package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;
import org.bukkit.plugin.PluginManager;

public class SignedChatHandler extends AbstractChatHandler {

    public SignedChatHandler(Essentials ess, EssentialsChat essChat) {
        super(ess, essChat);
    }

    public boolean tryRegisterListeners() {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_19_2_R01)) {
            return false;
        }

        try {
            final Class<?> previewClass = Class.forName("org.bukkit.event.player.AsyncPlayerChatPreviewEvent");
            if (!AsyncPlayerChatEvent.class.isAssignableFrom(previewClass)) {
                essChat.getLogger().severe(I18n.tl("essChatNoSecureMsg", essChat.getDescription().getVersion()));
                return false;
            }
        } catch (ClassNotFoundException e) {
            essChat.getLogger().severe(I18n.tl("essChatNoSecureMsg", essChat.getDescription().getVersion()));
            return false;
        }

        final PluginManager pm = essChat.getServer().getPluginManager();
        pm.registerEvents(new PreviewLowest(), essChat);
        pm.registerEvents(new PreviewHighest(), essChat);
        pm.registerEvents(new ChatLowest(), essChat);
        pm.registerEvents(new ChatNormal(), essChat);
        pm.registerEvents(new ChatHighest(), essChat);
        return true;
    }

    private void handleChatApplyPreview(AsyncPlayerChatEvent event) {
        final ChatProcessingCache.ProcessedChat chat = cache.getProcessedChat(event.getPlayer());
        if (chat == null) {
            handleChatFormat(event);
            handleChatPostFormat(event);
        } else {
            event.setFormat(chat.getFormat());
            event.setMessage(chat.getMessage());
        }
    }

    private void handleChatConfirmPreview(AsyncPlayerChatEvent event) {
        if (!ess.getSettings().isDebug()) return;

        final ChatProcessingCache.ProcessedChat chat = cache.getProcessedChat(event.getPlayer());
        if (chat == null) {
            // Can't confirm preview for some reason
            essChat.getLogger().info("Processed chat missing for " + event.getPlayer());
        } else {
            if (!event.getFormat().equals(chat.getFormat())) {
                // Chat format modified by another plugin
                essChat.getLogger().info("Chat format has been modified for " + event.getPlayer());
                essChat.getLogger().info("Expected '" + chat.getFormat() + "', got '" + event.getFormat());
            }
            if (!event.getMessage().equals(chat.getMessage())) {
                // Chat message modified by another plugin
                essChat.getLogger().info("Chat message has been modified for " + event.getPlayer());
                essChat.getLogger().info("Expected '" + chat.getMessage() + "', got '" + event.getMessage());
            }
        }
    }

    private interface PreviewListener extends Listener {
        void onPlayerChatPreview(AsyncPlayerChatPreviewEvent event);
    }

    private class PreviewLowest implements PreviewListener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerChatPreview(AsyncPlayerChatPreviewEvent event) {
            handleChatFormat(event);
        }
    }

    private class PreviewHighest implements PreviewListener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerChatPreview(AsyncPlayerChatPreviewEvent event) {
            handleChatPostFormat(event);
        }
    }

    private class ChatLowest implements ChatListener {
        @Override
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            handleChatApplyPreview(event);
        }
    }

    private class ChatNormal implements ChatListener {
        @Override
        @EventHandler(priority = EventPriority.NORMAL)
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            handleChatRecipients(event);
        }
    }

    private class ChatHighest implements ChatListener {
        @Override
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            handleChatConfirmPreview(event);
            handleChatSubmit(event);
        }
    }

}
