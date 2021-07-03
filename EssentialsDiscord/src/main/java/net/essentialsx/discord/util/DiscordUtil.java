package net.essentialsx.discord.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import com.earth2me.essentials.utils.DownsampleUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.ImmutableList;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.api.v2.services.discord.MessageType;
import net.essentialsx.discord.JDADiscordService;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;

import java.awt.Color;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class DiscordUtil {
    public final static List<Message.MentionType> NO_GROUP_MENTIONS;
    public final static AllowedMentions ALL_MENTIONS_WEBHOOK = AllowedMentions.all();
    public final static AllowedMentions NO_GROUP_MENTIONS_WEBHOOK = new AllowedMentions().withParseEveryone(false).withParseRoles(false).withParseUsers(true);
    public final static CopyOnWriteArrayList<String> ACTIVE_WEBHOOKS = new CopyOnWriteArrayList<>();

    static {
        final ImmutableList.Builder<Message.MentionType> types = new ImmutableList.Builder<>();
        types.add(Message.MentionType.USER);
        types.add(Message.MentionType.CHANNEL);
        types.add(Message.MentionType.EMOTE);
        NO_GROUP_MENTIONS = types.build();
    }

    private DiscordUtil() {
    }

    /**
     * Creates a {@link WebhookClient}.
     *
     * @param id     The id of the webhook.
     * @param token  The token of the webhook.
     * @param client The http client of the webhook.
     * @return The {@link WebhookClient}.
     */
    public static WebhookClient getWebhookClient(long id, String token, OkHttpClient client) {
        return new WebhookClientBuilder(id, token)
                .setAllowedMentions(AllowedMentions.none())
                .setHttpClient(client)
                .setDaemon(true)
                .build();
    }

    /**
     * Gets and cleans webhooks with the given name from channels other than the specified one.
     *
     * @param channel     The channel to search for webhooks in.
     * @param webhookName The name of the webhook to validate it.
     *
     * @return A future which completes with the webhook by the given name in the given channel, if present, otherwise null.
     */
    public static CompletableFuture<Webhook> getAndCleanWebhooks(final TextChannel channel, final String webhookName) {
        final Member self = channel.getGuild().getSelfMember();

        final CompletableFuture<Webhook> future = new CompletableFuture<>();
        final Consumer<List<Webhook>> consumer = webhooks -> {
            boolean foundWebhook = false;
            for (final Webhook webhook : webhooks) {
                if (webhook.getName().equalsIgnoreCase(webhookName)) {
                    if (foundWebhook || !webhook.getChannel().equals(channel)) {
                        ACTIVE_WEBHOOKS.remove(webhook.getId());
                        webhook.delete().reason("EssX Webhook Cleanup").queue();
                        continue;
                    }
                    ACTIVE_WEBHOOKS.addIfAbsent(webhook.getId());
                    future.complete(webhook);
                    foundWebhook = true;
                }
            }

            if (!foundWebhook) {
                future.complete(null);
            }
        };

        if (self.hasPermission(Permission.MANAGE_WEBHOOKS)) {
            channel.getGuild().retrieveWebhooks().queue(consumer);
        } else if (self.hasPermission(channel, Permission.MANAGE_WEBHOOKS)) {
            channel.retrieveWebhooks().queue(consumer);
        } else {
            return CompletableFuture.completedFuture(null);
        }

        return future;
    }

    /**
     * Creates a webhook with the given name in the given channel.
     *
     * @param channel        The channel to search for webhooks in.
     * @param webhookName    The name of the webhook to look for.
     * @return A future which completes with the webhook by the given name in the given channel or null if no permissions.
     */
    public static CompletableFuture<Webhook> createWebhook(TextChannel channel, String webhookName) {
        if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MANAGE_WEBHOOKS)) {
            return CompletableFuture.completedFuture(null);
        }

        final CompletableFuture<Webhook> future = new CompletableFuture<>();
        channel.createWebhook(webhookName).queue(webhook -> {
            future.complete(webhook);
            ACTIVE_WEBHOOKS.addIfAbsent(webhook.getId());
        });
        return future;
    }

    /**
     * Gets the uppermost bukkit color code of a given member or an empty string if the server version is &lt; 1.16.
     *
     * @param member The target member.
     * @return The bukkit color code or blank string.
     */
    public static String getRoleColorFormat(Member member) {
        final Color color = member.getColor();

        if (color == null) {
            return "";
        }

        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            // Essentials' FormatUtil allows us to not have to use bungee's chatcolor since bukkit's own one doesn't support rgb
            return FormatUtil.replaceFormat("&#" + Integer.toHexString(color.getRGB()).substring(2));
        }
        return FormatUtil.replaceFormat("&" + DownsampleUtil.nearestTo(color.getRGB()));
    }

    /**
     * Checks is the supplied user has at least one of the supplied roles.
     *
     * @param member          The member to check.
     * @param roleDefinitions A list with either the name or id of roles.
     * @return true if member has role.
     */
    public static boolean hasRoles(Member member, List<String> roleDefinitions) {
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }

        final List<Role> roles = member.getRoles();
        for (String roleDefinition : roleDefinitions) {
            roleDefinition = roleDefinition.trim();

            if (roleDefinition.equals("*") || member.getId().equals(roleDefinition)) {
                return true;
            }

            for (final Role role : roles) {
                if (role.getId().equals(roleDefinition) || role.getName().equalsIgnoreCase(roleDefinition)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void dispatchDiscordMessage(final JDADiscordService jda, final MessageType messageType, final String message, final boolean allowPing, final String avatarUrl, final String name, final UUID uuid) {
        if (jda.getPlugin().getSettings().getMessageChannel(messageType.getKey()).equalsIgnoreCase("none")) {
            return;
        }

        final DiscordMessageEvent event = new DiscordMessageEvent(messageType, FormatUtil.stripFormat(message), allowPing, avatarUrl, name, uuid);

        // If the server is stopping, we cannot dispatch events.
        if (messageType == MessageType.DefaultTypes.SERVER_STOP) {
            jda.sendMessage(event, event.getMessage(), event.isAllowGroupMentions());
            return;
        }

        if (Bukkit.getServer().isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            Bukkit.getScheduler().runTask(jda.getPlugin(), () -> Bukkit.getPluginManager().callEvent(event));
        }
    }
}
