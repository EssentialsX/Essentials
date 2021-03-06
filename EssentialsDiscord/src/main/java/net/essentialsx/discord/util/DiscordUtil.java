package net.essentialsx.discord.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
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

public final class DiscordUtil {
    public final static Gson GSON = new Gson();
    public final static List<Message.MentionType> NO_GROUP_MENTIONS;
    public final static JsonObject RAW_NO_GROUP_MENTIONS;
    public final static MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public final static int EPHEMERAL_FLAG = 1 << 6;
    public final static RequestBody ACK_DEFER = RequestBody.create(JSON_TYPE, "{\"type\": 5}");
    public final static RequestBody ACK_DEFER_EPHEMERAL = RequestBody.create(JSON_TYPE, "{\"type\": 5, \"data\":{\"flags\": " + EPHEMERAL_FLAG + "}}");

    private final static String WEBHOOK_NAME = "EssX Console Relay";

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
     * Gets the webhook to be used for console relay or null if permissions are not available.
     *
     * @param consoleChannel The channel to search for webhooks in.
     * @return The webhook to be used for console relay or null if unavailable.
     */
    public static Webhook getAndCleanWebhook(TextChannel consoleChannel) {
        final Member self = consoleChannel.getGuild().getSelfMember();

        final List<Webhook> webhookList;
        if (self.hasPermission(Permission.MANAGE_WEBHOOKS)) {
            webhookList = consoleChannel.getGuild().retrieveWebhooks().complete();
        } else if (self.hasPermission(consoleChannel, Permission.MANAGE_WEBHOOKS)) {
            webhookList = consoleChannel.retrieveWebhooks().complete();
        } else {
            return null;
        }

        Webhook consoleWebhook = null;
        for (Webhook webhook : webhookList) {
            if (webhook.getName().equals(WEBHOOK_NAME)) {
                if (!webhook.getChannel().equals(consoleChannel) || consoleWebhook != null) {
                    webhook.delete().reason("EssX Webhook Cleanup").queue();
                    continue;
                }
                consoleWebhook = webhook;
            }
        }
        if (consoleWebhook == null) {
            consoleWebhook = consoleChannel.createWebhook(WEBHOOK_NAME).complete();
        }
        return consoleWebhook;
    }

    /**
     * Gets the uppermost bukkit color code of a given member or an empty string if the server version is &lt; 1.16.
     *
     * @param member The target member.
     * @return The bukkit color code or blank string.
     */
    public static String getRoleColorFormat(Member member) {
        final Color color = member.getColor();
        if (color != null && VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            // Essentials' FormatUtil allows us to not have to use bungee's chatcolor since bukkit's own one doesn't support rgb
            return FormatUtil.replaceFormat("&#" + Integer.toHexString(color.getRGB()).substring(2));
        }
        return "";
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
