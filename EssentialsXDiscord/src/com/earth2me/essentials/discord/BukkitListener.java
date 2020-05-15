package com.earth2me.essentials.discord;

import com.earth2me.essentials.discord.utils.DiscordUtils;
import com.earth2me.essentials.discord.utils.MessageType;
import net.dv8tion.jda.api.JDA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Logger;

public class BukkitListener implements Listener {
    private static final Logger logger = Logger.getLogger("EssentialsDiscord");

    private final EssentialsDiscord plugin;
    private final JDA jda;

    public BukkitListener(EssentialsDiscord plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        DiscordUtils.sendMessage(jda, MessageType.GLOBAL_CHAT, event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }
}
