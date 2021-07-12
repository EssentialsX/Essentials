package net.essentialsx.discord.listeners;

import com.earth2me.essentials.utils.FormatUtil;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ess3.api.IUser;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.util.DiscordUtil;
import net.essentialsx.discord.util.MessageUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscordListener extends ListenerAdapter {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");

    private final JDADiscordService plugin;

    public DiscordListener(JDADiscordService plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() && !event.isWebhookMessage() && (!plugin.getSettings().isShowBotMessages() || event.getAuthor().getId().equals(plugin.getJda().getSelfUser().getId()))) {
            return;
        }

        if (event.isWebhookMessage() && (!plugin.getSettings().isShowWebhookMessages() || DiscordUtil.ACTIVE_WEBHOOKS.contains(event.getAuthor().getId()))) {
            return;
        }

        // Get list of channel names that have this channel id mapped
        final List<String> keys = plugin.getPlugin().getSettings().getKeysFromChannelId(event.getChannel().getIdLong());
        if (keys == null || keys.size() == 0) {
            if (plugin.isDebug()) {
                logger.log(Level.INFO, "Skipping message due to no channel keys for id " + event.getChannel().getIdLong() + "!");
            }
            return;
        }

        final User user = event.getAuthor();
        final Member member = event.getMember();
        final Message message = event.getMessage();

        assert member != null; // Member will never be null

        if (plugin.getSettings().getDiscordFilter() != null && plugin.getSettings().getDiscordFilter().matcher(message.getContentDisplay()).find()) {
            if (plugin.isDebug()) {
                logger.log(Level.INFO, "Skipping message " + message.getId() + " with content, \"" + message.getContentDisplay() + "\" as it matched the filter!");
            }
            return;
        }

        final StringBuilder messageBuilder = new StringBuilder(message.getContentDisplay());
        if (plugin.getPlugin().getSettings().isShowDiscordAttachments()) {
            for (final Message.Attachment attachment : message.getAttachments()) {
                messageBuilder.append(" ").append(attachment.getUrl());
            }
        }

        // Strip message
        final String strippedMessage = StringUtils.abbreviate(
                messageBuilder.toString()
                        .replace(plugin.getSettings().isChatFilterNewlines() ? '\n' : ' ', ' ')
                        .trim(), plugin.getSettings().getChatDiscordMaxLength());

        // Apply or strip color formatting
        final String finalMessage = DiscordUtil.hasRoles(member, plugin.getPlugin().getSettings().getPermittedFormattingRoles()) ?
                FormatUtil.replaceFormat(strippedMessage) : FormatUtil.stripFormat(strippedMessage);

        // Don't send blank messages
        if (finalMessage.trim().length() == 0) {
            if (plugin.isDebug()) {
                logger.log(Level.INFO, "Skipping finalized empty message " + message.getId());
            }
            return;
        }

        final String formattedMessage = EmojiParser.parseToAliases(MessageUtil.formatMessage(plugin.getPlugin().getSettings().getDiscordToMcFormat(),
                event.getChannel().getName(), user.getName(), user.getDiscriminator(), user.getAsTag(),
                member.getEffectiveName(), DiscordUtil.getRoleColorFormat(member), finalMessage, DiscordUtil.getRoleFormat(member)), EmojiParser.FitzpatrickAction.REMOVE);

        for (final String group : keys) {
            if (plugin.getSettings().getRelayToConsoleList().contains(group)) {
                logger.info(formattedMessage);
                break;
            }
        }

        for (IUser essUser : plugin.getPlugin().getEss().getOnlineUsers()) {
            for (String group : keys) {
                final String perm = "essentials.discord.receive." + group;
                final boolean primaryOverride = plugin.getSettings().isAlwaysReceivePrimary() && group.equalsIgnoreCase("primary");
                if (primaryOverride || (essUser.isPermissionSet(perm) && essUser.isAuthorized(perm))) {
                    essUser.sendMessage(formattedMessage);
                    break;
                }
            }
        }
    }
}
