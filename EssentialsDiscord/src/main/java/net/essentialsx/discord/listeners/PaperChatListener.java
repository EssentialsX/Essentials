package net.essentialsx.discord.listeners;

import net.ess3.provider.AbstractChatEvent;
import net.ess3.provider.providers.PaperChatListenerProvider;
import net.essentialsx.api.v2.ChatType;
import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import net.essentialsx.discord.JDADiscordService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PaperChatListener extends PaperChatListenerProvider {
    private final JDADiscordService jda;

    public PaperChatListener(JDADiscordService jda) {
        this.jda = jda;
    }

    @Override
    public void onChatMonitor(AbstractChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(jda.getPlugin(), () -> {
            final DiscordChatMessageEvent chatEvent = new DiscordChatMessageEvent(event.getPlayer(), event.getMessage(), ChatType.UNKNOWN);
            chatEvent.setCancelled(!jda.getSettings().isShowAllChat() && !event.recipients().containsAll(Bukkit.getOnlinePlayers()));
            Bukkit.getPluginManager().callEvent(chatEvent);
            if (chatEvent.isCancelled()) {
                return;
            }

            jda.sendChatMessage(player, chatEvent.getMessage());
        });
    }
}
