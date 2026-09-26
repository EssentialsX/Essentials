package net.essentialsx.discord.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.util.DiscordCommandSender;
import net.essentialsx.discord.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.jetbrains.annotations.NotNull;

public class DiscordCommandDispatcher extends ListenerAdapter {
    private final JDADiscordService jda;
    private String channelId = null;

    public DiscordCommandDispatcher(JDADiscordService jda) {
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getChannelType() != ChannelType.TEXT) {
            return;
        }

        if (jda.getConsoleWebhook() != null && event.getChannel().getId().equals(channelId)) {
            if ((event.isWebhookMessage() || event.getAuthor().isBot()) && (!jda.getSettings().isConsoleBotCommandRelay() || DiscordUtil.ACTIVE_WEBHOOKS.contains(event.getAuthor().getId()) || event.getAuthor().getId().equals(event.getGuild().getSelfMember().getId()))) {
                return;
            }

            final String command = event.getMessage().getContentRaw();
            jda.getPlugin().getEss().scheduleGlobalDelayedTask(() -> {
                try {
                    Bukkit.dispatchCommand(new DiscordCommandSender(jda, Bukkit.getConsoleSender(), message ->
                            event.getMessage().reply(message).queue()).getSender(), command);
                } catch (CommandException e) {
                    // Check if this is a vanilla command, in which case we have to use a vanilla command sender :(
                    if (e.getMessage().contains("a vanilla command listener") || (e.getCause() != null && e.getCause().getMessage().contains("a vanilla command listener"))) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        return;
                    }
                    // Something unrelated, should error out here
                    throw e;
                }
            });
        }
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
