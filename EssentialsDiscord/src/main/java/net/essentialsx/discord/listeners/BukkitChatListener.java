package net.essentialsx.discord.listeners;

import net.essentialsx.api.v2.ChatType;
import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class BukkitChatListener implements Listener {
    private final JDADiscordService jda;

    public BukkitChatListener(JDADiscordService jda) {
        this.jda = jda;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(jda.getPlugin(), () -> {
            final DiscordChatMessageEvent chatEvent = new DiscordChatMessageEvent(event.getPlayer(), event.getMessage(), ChatType.UNKNOWN);
            chatEvent.setCancelled(!jda.getSettings().isShowAllChat() && !event.getRecipients().containsAll(Bukkit.getOnlinePlayers()));
            Bukkit.getPluginManager().callEvent(chatEvent);
            if (chatEvent.isCancelled()) {
                return;
            }

            jda.sendChatMessage(player, chatEvent.getMessage());
        });
    }
}
