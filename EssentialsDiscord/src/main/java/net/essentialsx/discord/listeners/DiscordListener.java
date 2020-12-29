package net.essentialsx.discord.listeners;

import com.earth2me.essentials.utils.FormatUtil;
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

        assert member != null; // Member will never be null

        final StringBuilder messageBuilder = new StringBuilder(message.getContentDisplay());
        if (plugin.getPlugin().getSettings().isShowDiscordAttachments()) {
            for (final Message.Attachment attachment : message.getAttachments()) {
                messageBuilder.append(" ").append(attachment.getUrl());
            }
        }

        // Don't send blank messages
        if (messageBuilder.toString().trim().length() == 0) {
            return;
        }

        // Apply or strip formatting
        final String finalMessage = DiscordUtil.hasRoles(member, plugin.getPlugin().getSettings().getPermittedFormattingRoles()) ?
                FormatUtil.replaceFormat(messageBuilder.toString().trim()) : FormatUtil.stripFormat(messageBuilder.toString().trim());

        final String formattedMessage = EmojiParser.parseToAliases(MessageUtil.formatMessage(plugin.getPlugin().getSettings().getDiscordToMcFormat(),
                event.getChannel().getName(), user.getName(), user.getDiscriminator(), user.getAsTag(),
                member.getEffectiveName(), DiscordUtil.getRoleColorFormat(member), finalMessage), EmojiParser.FitzpatrickAction.REMOVE);

        for (String group : keys) {
            Bukkit.broadcast(formattedMessage, "essentials.discord.receive." + group);
        }
    }
}
