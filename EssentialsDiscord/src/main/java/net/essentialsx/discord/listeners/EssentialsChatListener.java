package net.essentialsx.discord.listeners;

import net.essentialsx.api.v2.events.chat.ChatEvent;
import net.essentialsx.api.v2.events.chat.GlobalChatEvent;
import net.essentialsx.api.v2.events.chat.LocalChatEvent;
import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EssentialsChatListener implements Listener {
    private final JDADiscordService jda;

    public EssentialsChatListener(JDADiscordService jda) {
        this.jda = jda;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLocalChat(LocalChatEvent event) {
        processChatEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGlobalChat(GlobalChatEvent event) {
        processChatEvent(event);
    }

    private void processChatEvent(ChatEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTask(jda.getPlugin(), () -> {
            final DiscordChatMessageEvent chatEvent = new DiscordChatMessageEvent(event.getPlayer(), event.getMessage(), event.getChatType());
            Bukkit.getPluginManager().callEvent(chatEvent);

            if (!chatEvent.isCancelled()) {
                jda.sendChatMessage(event.getChatType(), player, chatEvent.getMessage());
            }
        });
    }
}
