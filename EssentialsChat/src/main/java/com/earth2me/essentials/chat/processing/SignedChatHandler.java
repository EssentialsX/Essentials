package com.earth2me.essentials.chat.processing;

import com.earth2me.essentials.Essentials;
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

    boolean tryRegisterListeners() {
        try {
            final Class<?> previewClass = Class.forName("org.bukkit.event.player.AsyncPlayerChatPreviewEvent");
            if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_19_1_R01) || !AsyncPlayerChatEvent.class.isAssignableFrom(previewClass)) {
                return false;
            }
        } catch (ClassNotFoundException e) {
            return false;
        }

        final PluginManager pm = essChat.getServer().getPluginManager();
        pm.registerEvents(new Lowest(), essChat);
        // TODO Normal
        // TODO Highest (or Monitor?)
        return true;
    }

    private interface PreviewListener extends Listener {
        void onPlayerChatPreview(AsyncPlayerChatPreviewEvent event);
    }

    private class Lowest implements PreviewListener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerChatPreview(AsyncPlayerChatPreviewEvent event) {
            // TODO
        }
    }

    // TODO preview on Normal, Highest
    // TODO chat on ???? priority, how do we do this without exploding all over other plugins?

}
