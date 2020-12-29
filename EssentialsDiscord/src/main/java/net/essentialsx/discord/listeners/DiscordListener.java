package net.essentialsx.discord.listeners;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.util.DiscordUtil;
import net.essentialsx.discord.util.MessageUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiscordListener extends ListenerAdapter {
    private final EssentialsJDA plugin;

    public DiscordListener(EssentialsJDA plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }

        // Get list of channel names that have this channel id mapped
        final List<String> keys = plugin.getPlugin().getSettings().getKeysFromChannelId(event.getChannel().getIdLong());
        if (keys == null || keys.size() == 0) {
            return;
        }

        final User user = event.getAuthor();
        final Member member = event.getMember();
        final Message message = event.getMessage();

        final StringBuilder messageStr = new StringBuilder(message.getContentDisplay());
        if (plugin.getPlugin().getSettings().isShowDiscordAttachments()) {
            for (final Message.Attachment attachment : message.getAttachments()) {
                messageStr.append(" ").append(attachment.getUrl());
            }
        }

        if (messageStr.toString().trim().length() == 0) {
            return;
        }

        assert member != null; // Member will never be null
        final String formattedMessage = EmojiParser.parseToAliases(MessageUtil.formatMessage(plugin.getPlugin().getSettings().getDiscordToMcFormat(),
                event.getChannel().getName(), user.getName(), user.getDiscriminator(), user.getAsTag(),
                member.getEffectiveName(), DiscordUtil.getRoleColorFormat(member), messageStr.toString()), EmojiParser.FitzpatrickAction.REMOVE);

        for (String group : keys) {
            Bukkit.broadcast(formattedMessage, "essentials.discord.receive." + group);
        }
    }
}
