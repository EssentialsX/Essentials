package net.essentialsx.discord.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import com.earth2me.essentials.utils.DownsampleUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class DiscordUtil {
    public final static Gson GSON = new Gson();
    public final static List<Message.MentionType> NO_GROUP_MENTIONS;
    public final static AllowedMentions ALL_MENTIONS_WEBHOOK = AllowedMentions.all();
    public final static AllowedMentions NO_GROUP_MENTIONS_WEBHOOK = new AllowedMentions().withParseEveryone(false).withParseRoles(false).withParseUsers(true);
    public final static JsonObject RAW_NO_GROUP_MENTIONS;
    public final static MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public final static int EPHEMERAL_FLAG = 1 << 6;
    public final static RequestBody ACK_DEFER = RequestBody.create(JSON_TYPE, "{\"type\": 5}");
    public final static RequestBody ACK_DEFER_EPHEMERAL = RequestBody.create(JSON_TYPE, "{\"type\": 5, \"data\":{\"flags\": " + EPHEMERAL_FLAG + "}}");

    static {
        final ImmutableList.Builder<Message.MentionType> types = new ImmutableList.Builder<>();
        types.add(Message.MentionType.USER);
        types.add(Message.MentionType.CHANNEL);
        types.add(Message.MentionType.EMOTE);
        NO_GROUP_MENTIONS = types.build();

        final JsonObject allowMentions = new JsonObject();
        allowMentions.add("parse", GSON.toJsonTree(ImmutableList.of("users")).getAsJsonArray());
        allowMentions.add("users", GSON.toJsonTree(ImmutableList.of()).getAsJsonArray());
        RAW_NO_GROUP_MENTIONS = allowMentions;
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
                        webhook.delete().reason("EssX Webhook Cleanup").queue();
                        continue;
                    }
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
        channel.createWebhook(webhookName).queue(future::complete);
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
}
