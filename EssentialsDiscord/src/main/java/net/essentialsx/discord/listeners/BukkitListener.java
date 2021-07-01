package net.essentialsx.discord.listeners;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.MuteStatusChangeEvent;
import net.essentialsx.api.v2.events.AsyncUserDataLoadEvent;
import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.api.v2.services.discord.MessageType;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.util.DiscordUtil;
import net.essentialsx.discord.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.MessageFormat;
import java.util.UUID;

public class BukkitListener implements Listener {
    private final static String AVATAR_URL = "https://crafthead.net/helm/{uuid}";
    private final JDADiscordService jda;

    public BukkitListener(JDADiscordService jda) {
        this.jda = jda;
    }

    /**
     * Processes messages from all other events.
     * This way it allows other plugins to modify route/message or just cancel it.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDiscordMessage(DiscordMessageEvent event) {
        jda.sendMessage(event, event.getMessage(), event.isAllowGroupMentions());
    }

    // Bukkit Events

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMute(MuteStatusChangeEvent event) {
        if (!event.getValue()) {
            sendDiscordMessage(MessageType.DefaultTypes.MUTE,
                    MessageUtil.formatMessage(jda.getSettings().getUnmuteFormat(),
                            MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getDisplayName())));
        } else if (event.getTimestamp().isPresent()) {
            final boolean console = event.getController() == null;
            final MessageFormat msg = event.getReason() == null ? jda.getSettings().getTempMuteFormat() : jda.getSettings().getTempMuteReasonFormat();
            sendDiscordMessage(MessageType.DefaultTypes.MUTE,
                    MessageUtil.formatMessage(msg,
                            MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getDisplayName()),
                            MessageUtil.sanitizeDiscordMarkdown(console ? Console.NAME : event.getController().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(console ? Console.DISPLAY_NAME : event.getController().getDisplayName()),
                            DateUtil.formatDateDiff(event.getTimestamp().get()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getReason())));
        } else {
            final boolean console = event.getController() == null;
            final MessageFormat msg = event.getReason() == null ? jda.getSettings().getPermMuteFormat() : jda.getSettings().getPermMuteReasonFormat();
            sendDiscordMessage(MessageType.DefaultTypes.MUTE,
                    MessageUtil.formatMessage(msg,
                            MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getDisplayName()),
                            MessageUtil.sanitizeDiscordMarkdown(console ? Console.NAME : event.getController().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(console ? Console.DISPLAY_NAME : event.getController().getDisplayName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getReason())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(jda.getPlugin(), () -> {
            final DiscordChatMessageEvent chatEvent = new DiscordChatMessageEvent(event.getPlayer(), event.getMessage());
            chatEvent.setCancelled(!jda.getSettings().isShowAllChat() && !event.getRecipients().containsAll(Bukkit.getOnlinePlayers()));
            Bukkit.getPluginManager().callEvent(chatEvent);
            if (chatEvent.isCancelled()) {
                return;
            }

            sendDiscordMessage(MessageType.DefaultTypes.CHAT,
                    MessageUtil.formatMessage(jda.getSettings().getMcToDiscordFormat(player),
                            MessageUtil.sanitizeDiscordMarkdown(player.getName()),
                            MessageUtil.sanitizeDiscordMarkdown(player.getDisplayName()),
                            player.hasPermission("essentials.discord.markdown") ? chatEvent.getMessage() : MessageUtil.sanitizeDiscordMarkdown(chatEvent.getMessage()),
                            MessageUtil.sanitizeDiscordMarkdown(player.getWorld().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripEssentialsFormat(jda.getPlugin().getEss().getPermissionsHandler().getPrefix(player))),
                            MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripEssentialsFormat(jda.getPlugin().getEss().getPermissionsHandler().getSuffix(player)))),
                    player.hasPermission("essentials.discord.ping"),
                    jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", player.getUniqueId().toString()) : null,
                    jda.getSettings().isShowName() ? player.getName() : null,
                    player.getUniqueId());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncUserDataLoadEvent event) {
        // Delay join to let nickname load
        if (event.getJoinMessage() != null) {
            sendDiscordMessage(MessageType.DefaultTypes.JOIN,
                    MessageUtil.formatMessage(jda.getSettings().getJoinFormat(event.getUser().getBase()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getUser().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getUser().getDisplayName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getJoinMessage())),
                    false,
                    jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", event.getUser().getBase().getUniqueId().toString()) : null,
                    jda.getSettings().isShowName() ? event.getUser().getName() : null,
                    event.getUser().getBase().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        if (event.getQuitMessage() != null) {
            sendDiscordMessage(MessageType.DefaultTypes.LEAVE,
                    MessageUtil.formatMessage(jda.getSettings().getQuitFormat(event.getPlayer()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getDisplayName()),
                            MessageUtil.sanitizeDiscordMarkdown(event.getQuitMessage())),
                    false,
                    jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", event.getPlayer().getUniqueId().toString()) : null,
                    jda.getSettings().isShowName() ? event.getPlayer().getName() : null,
                    event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        sendDiscordMessage(MessageType.DefaultTypes.DEATH,
                MessageUtil.formatMessage(jda.getSettings().getDeathFormat(event.getEntity()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getEntity().getName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getEntity().getDisplayName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getDeathMessage())),
                false,
                jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", event.getEntity().getUniqueId().toString()) : null,
                jda.getSettings().isShowName() ? event.getEntity().getName() : null,
                event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfk(AfkStatusChangeEvent event) {
        final MessageFormat format;
        if (event.getValue()) {
            format = jda.getSettings().getAfkFormat(event.getAffected().getBase());
        } else {
            format = jda.getSettings().getUnAfkFormat(event.getAffected().getBase());
        }

        sendDiscordMessage(MessageType.DefaultTypes.AFK,
                MessageUtil.formatMessage(format,
                        MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getAffected().getDisplayName())),
                false,
                jda.getSettings().isShowAvatar() ? AVATAR_URL.replace("{uuid}", event.getAffected().getBase().getUniqueId().toString()) : null,
                jda.getSettings().isShowName() ? event.getAffected().getName() : null,
                event.getAffected().getBase().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        sendDiscordMessage(MessageType.DefaultTypes.KICK,
                MessageUtil.formatMessage(jda.getSettings().getKickFormat(),
                        MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getDisplayName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getReason())));
    }

    private void sendDiscordMessage(final MessageType messageType, final String message) {
        sendDiscordMessage(messageType, message, false, null, null, null);
    }

    private void sendDiscordMessage(final MessageType messageType, final String message, final boolean allowPing, final String avatarUrl, final String name, final UUID uuid) {
        DiscordUtil.dispatchDiscordMessage(jda, messageType, message, allowPing, avatarUrl, name, uuid);
    }
}
