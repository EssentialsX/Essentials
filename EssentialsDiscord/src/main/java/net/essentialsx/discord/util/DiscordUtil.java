package net.essentialsx.discord.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import com.earth2me.essentials.utils.DownsampleUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.ImmutableList;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.api.v2.services.discord.MessageType;
import net.essentialsx.discord.JDADiscordService;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DiscordUtil {
    public final static String ADVANCED_RELAY_NAME = "EssX Advanced Relay";
    public final static String CONSOLE_RELAY_NAME = "EssX Console Relay";
    public final static List<Message.MentionType> NO_GROUP_MENTIONS;
    public final static AllowedMentions ALL_MENTIONS_WEBHOOK = AllowedMentions.all();
    public final static AllowedMentions NO_GROUP_MENTIONS_WEBHOOK = new AllowedMentions().withParseEveryone(false).withParseRoles(false).withParseUsers(true);
    public final static CopyOnWriteArrayList<String> ACTIVE_WEBHOOKS = new CopyOnWriteArrayList<>();

    static {
        final ImmutableList.Builder<Message.MentionType> types = new ImmutableList.Builder<>();
        types.add(Message.MentionType.USER);
        types.add(Message.MentionType.CHANNEL);
        types.add(Message.MentionType.EMOJI);
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
    public static WrappedWebhookClient getWebhookClient(long id, String token, OkHttpClient client) {
        return new WrappedWebhookClient(id, token, client);
    }

    /**
     * Gets or creates a webhook with the given name in the given channel.
     *
     * @param channel     The channel to search for/create webhooks in.
     * @param webhookName The name of the webhook to search for/create.
     *
     * @return A future which completes with the webhook by the given name in the given channel, or null
     * if the bot lacks the proper permissions.
     */
    public static CompletableFuture<Webhook> getOrCreateWebhook(final TextChannel channel, final String webhookName) {
        if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MANAGE_WEBHOOKS)) {
            return CompletableFuture.completedFuture(null);
        }

        final CompletableFuture<Webhook> future = new CompletableFuture<>();
        channel.retrieveWebhooks().queue(webhooks -> {
            for (final Webhook webhook : webhooks) {
                if (webhook.getName().equals(webhookName) && webhook.getToken() != null) {
                    ACTIVE_WEBHOOKS.addIfAbsent(webhook.getId());
                    future.complete(webhook);
                    return;
                }
            }
            createWebhook(channel, webhookName).thenAccept(future::complete);
        });
        return future;
    }

    /**
     * Cleans up unused webhooks from channels that no longer require an advanced relay.
     *
     * @param guild       The guild which to preform the cleanup in.
     * @param webhookName The name of the webhook to scan for.
     */
    @SuppressWarnings("unused") // :balloon:
    private static void cleanWebhooks(final Guild guild, String webhookName) {
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            return;
        }

        guild.retrieveWebhooks().queue(webhooks -> {
            for (final Webhook webhook : webhooks) {
                if (webhook.getName().equalsIgnoreCase(webhookName) && !ACTIVE_WEBHOOKS.contains(webhook.getId())) {
                    webhook.delete().reason("EssentialsX Discord: webhook cleanup").queue();
                }
            }
        });
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
     * Gets the highest role of a given member or an empty string if the member has no roles.
     *
     * @param member The target member.
     * @return The highest role or blank string.
     */
    public static String getRoleFormat(final JDADiscordService jda, Member member) {
        final List<Role> roles = member == null ? null : member.getRoles();

        for (final Role role : roles) {
            final Boolean contains = jda.getPlugin().getSettings().getDiscordRolesBlacklist().contains(role.getName()) ||
                    jda.getPlugin().getSettings().getDiscordRolesBlacklist().contains(role.getId());
            if ((!jda.getPlugin().getSettings().getInvertDiscordRoleBlacklist() && !contains)
                    || (jda.getPlugin().getSettings().getInvertDiscordRoleBlacklist() && contains)) {
                String alias = jda.getPlugin().getSettings().getDiscordRoleAlias(role.getName());
                if (alias == "") {
                    alias = jda.getPlugin().getSettings().getDiscordRoleAlias(role.getId());
                }
                if (alias != "") {
                    return alias;
                } else {
                    return role.getName();
                }
            }
        }

        return "";

    }

    /**
     * Gets the uppermost bukkit color code of a given member or an empty string if the server version is &lt; 1.16.
     *
     * @param member The target member.
     * @return The bukkit color code or blank string.
     */
    public static String getRoleColorFormat(final JDADiscordService jda, Member member) {
        if (member == null || member.getColorRaw() == Role.DEFAULT_COLOR_RAW) {
            return "";
        }

        final List<Role> roles = member == null ? null : member.getRoles();

        int color = Role.DEFAULT_COLOR_RAW;
        for (final Role role : roles) {
            final Boolean contains = jda.getPlugin().getSettings().getDiscordRolesBlacklist().contains(role.getName()) ||
                    jda.getPlugin().getSettings().getDiscordRolesBlacklist().contains(role.getId());
            if ((!jda.getPlugin().getSettings().getInvertDiscordRoleBlacklist() && !contains)
                    || (jda.getPlugin().getSettings().getInvertDiscordRoleBlacklist() && contains)
                    && role.getColorRaw() != Role.DEFAULT_COLOR_RAW) {

                color = role.getColorRaw();
                break;

            }
        }

        if (jda.getPlugin().getSettings().getInvertDiscordRoleBlacklist() && jda.getPlugin().getSettings().getDiscordRolesBlacklist().isEmpty()) {
            color = member.getColorRaw();
        }

        if (color == Role.DEFAULT_COLOR_RAW) {
            return "";
        }

        final int rawColor = 0xff000000 | color;

        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            // Essentials' FormatUtil allows us to not have to use bungee's chatcolor since bukkit's own one doesn't support rgb
            return FormatUtil.replaceFormat("&#" + Integer.toHexString(rawColor).substring(2));
        }
        return FormatUtil.replaceFormat("&" + DownsampleUtil.nearestTo(rawColor));
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
            final boolean id = NumberUtil.isNumeric(roleDefinition);

            if (roleDefinition.equals("*") || member.getId().equals(roleDefinition)) {
                return true;
            }

            for (final Role role : roles) {
                if (role.getId().equals(roleDefinition) || (!id && role.getName().equalsIgnoreCase(roleDefinition))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getAvatarUrl(final JDADiscordService jda, final Player player) {
        return jda.getSettings().getAvatarURL().replace("{uuid}", player.getUniqueId().toString()).replace("{name}", player.getName());
    }

    public static void dispatchDiscordMessage(final JDADiscordService jda, final MessageType messageType, final String message, final boolean allowPing, final String avatarUrl, final String name, final UUID uuid) {
        if (jda.getPlugin().getSettings().getMessageChannel(messageType.getKey()).equalsIgnoreCase("none")) {
            return;
        }

        final DiscordMessageEvent event = new DiscordMessageEvent(messageType, FormatUtil.stripFormat(message), allowPing, avatarUrl, FormatUtil.stripFormat(name), uuid);

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
