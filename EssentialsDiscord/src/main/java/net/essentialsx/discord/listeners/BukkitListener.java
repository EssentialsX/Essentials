package net.essentialsx.discord.listeners;

import com.earth2me.essentials.Console;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.VersionUtil;
import net.ess3.api.events.PrivateMessageSentEvent;
import net.ess3.api.IUser;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.MuteStatusChangeEvent;
import net.ess3.api.events.VanishStatusChangeEvent;
import net.ess3.provider.AbstractAchievementEvent;
import net.essentialsx.api.v2.events.AsyncUserDataLoadEvent;
import net.essentialsx.api.v2.events.UserActionEvent;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.api.v2.services.discord.MessageType;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.util.DiscordUtil;
import net.essentialsx.discord.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.MessageFormat;
import java.util.UUID;

public class BukkitListener implements Listener {
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
    public void onPrivateMessage(PrivateMessageSentEvent event) {
        final Player sender = Bukkit.getPlayer(event.getSender().getUUID());
        final Player recipient = Bukkit.getPlayer(event.getRecipient().getUUID());

        if (sender.hasPermission("essentials.chat.spy.exempt")) {
            return;
        }

        sendDiscordMessage(MessageType.DefaultTypes.PRIVATE_CHAT,
                MessageUtil.formatMessage(jda.getSettings().getPmToDiscordFormat(),
                        MessageUtil.sanitizeDiscordMarkdown(sender.getName()),
                        MessageUtil.sanitizeDiscordMarkdown(sender.getDisplayName()),
                        MessageUtil.sanitizeDiscordMarkdown(recipient.getName()),
                        MessageUtil.sanitizeDiscordMarkdown(recipient.getDisplayName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getMessage())),
                sender);
    }

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncUserDataLoadEvent event) {
        // Delay join to let nickname load
        if (!isSilentJoinQuit(event.getUser(), "join") && !isVanishHide(event.getUser())) {
            // Check if this is the first time the player has joined
            if (!event.getUser().getBase().hasPlayedBefore()) {
                sendJoinQuitMessage(event.getUser().getBase(), event.getJoinMessage(), MessageType.DefaultTypes.FIRST_JOIN);
            } else {
                sendJoinQuitMessage(event.getUser().getBase(), event.getJoinMessage(), MessageType.DefaultTypes.JOIN);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        if (!isSilentJoinQuit(event.getPlayer(), "quit") && !isVanishHide(event.getPlayer())) {
            sendJoinQuitMessage(event.getPlayer(), event.getQuitMessage(), MessageType.DefaultTypes.LEAVE);
        }
    }

    public boolean isSilentJoinQuit(final Player player, final String type) {
        return isSilentJoinQuit(jda.getPlugin().getEss().getUser(player), type);
    }

    public boolean isSilentJoinQuit(final IUser user, final String type) {
        return jda.getPlugin().getEss().getSettings().allowSilentJoinQuit() && user.isAuthorized("essentials.silent" + type);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanishStatusChange(VanishStatusChangeEvent event) {
        if (!jda.getSettings().isVanishFakeJoinLeave() || event.getAffected().isLeavingHidden()) {
            return;
        }
        if (event.getValue()) {
            sendJoinQuitMessage(event.getAffected().getBase(), ChatColor.YELLOW + event.getAffected().getName() + " left the game", MessageType.DefaultTypes.LEAVE);
            return;
        }
        sendJoinQuitMessage(event.getAffected().getBase(), ChatColor.YELLOW + event.getAffected().getName() + " joined the game", MessageType.DefaultTypes.JOIN);
    }

    public void sendJoinQuitMessage(final Player player, final String message, MessageType type) {
        int onlineCount = jda.getPlugin().getEss().getOnlinePlayers().size();
        final MessageFormat format;
        switch (type.getKey()) {
            case "join":
                format = jda.getSettings().getJoinFormat(player);
                break;
            case "first-join":
                format = jda.getSettings().getFirstJoinFormat(player);
                break;
            default: // So that it will always be initialised. Other options shouldn't be possible.
                format = jda.getSettings().getQuitFormat(player);
                onlineCount = onlineCount - 1;
                break;

        }
        sendDiscordMessage(type,
                MessageUtil.formatMessage(format,
                        MessageUtil.sanitizeDiscordMarkdown(player.getName()),
                        MessageUtil.sanitizeDiscordMarkdown(player.getDisplayName()),
                        MessageUtil.sanitizeDiscordMarkdown(message),
                        onlineCount,
                        jda.getPlugin().getEss().getUsers().getUserCount()),
                        player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (isVanishHide(event.getEntity())) {
            return;
        }

        final Boolean showDeathMessages;
        if (VersionUtil.getServerBukkitVersion().isHigherThan(VersionUtil.v1_12_2_R01)) {
            showDeathMessages = event.getEntity().getWorld().getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES);
        } else {
            if (!event.getEntity().getWorld().isGameRule("showDeathMessages")) {
                showDeathMessages = null;
            } else {
                //noinspection deprecation
                showDeathMessages = event.getEntity().getWorld().getGameRuleValue("showDeathMessages").equals("true");
            }
        }
        if ((showDeathMessages != null && !showDeathMessages) || event.getDeathMessage() == null || event.getDeathMessage().trim().isEmpty()) {
            return;
        }

        sendDiscordMessage(MessageType.DefaultTypes.DEATH,
                MessageUtil.formatMessage(jda.getSettings().getDeathFormat(event.getEntity()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getEntity().getName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getEntity().getDisplayName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getDeathMessage())),
                event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfk(AfkStatusChangeEvent event) {
        if (isVanishHide(event.getAffected())) {
            return;
        }

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
                event.getAffected().getBase());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancement(AbstractAchievementEvent event) {
        if (isVanishHide(event.getPlayer())) {
            return;
        }

        sendDiscordMessage(MessageType.DefaultTypes.ADVANCEMENT,
                MessageUtil.formatMessage(jda.getSettings().getAdvancementFormat(event.getPlayer()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getDisplayName()),
                        event.getName()),
                event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAction(UserActionEvent event) {
        if (isVanishHide(event.getUser())) {
            return;
        }

        sendDiscordMessage(MessageType.DefaultTypes.ACTION,
                MessageUtil.formatMessage(jda.getSettings().getActionFormat(event.getUser().getBase()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getUser().getName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getUser().getDisplayName()),
                        event.getMessage()),
                event.getUser().getBase());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        if (isVanishHide(event.getPlayer())) {
            return;
        }
        sendDiscordMessage(MessageType.DefaultTypes.KICK,
                MessageUtil.formatMessage(jda.getSettings().getKickFormat(),
                        MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getPlayer().getDisplayName()),
                        MessageUtil.sanitizeDiscordMarkdown(event.getReason())));
    }

    private boolean isVanishHide(final Player player) {
        return isVanishHide(jda.getPlugin().getEss().getUser(player));
    }

    private boolean isVanishHide(final IUser user) {
        return jda.getSettings().isVanishHideMessages() && (user.isHidden() || user.isLeavingHidden());
    }

    private void sendDiscordMessage(final MessageType messageType, final String message) {
        sendDiscordMessage(messageType, message, null);
    }

    private void sendDiscordMessage(final MessageType messageType, final String message, final Player player) {
        String avatarUrl = null;
        String name = null;
        UUID uuid = null;
        if (player != null) {
            if (jda.getSettings().isShowAvatar()) {
                avatarUrl = DiscordUtil.getAvatarUrl(jda, player);
            }

            name = MessageUtil.formatMessage(jda.getSettings().getMcToDiscordNameFormat(player),
                player.getName(),
                player.getDisplayName(),
                jda.getPlugin().getEss().getSettings().getWorldAlias(player.getWorld().getName()),
                FormatUtil.stripEssentialsFormat(jda.getPlugin().getEss().getPermissionsHandler().getPrefix(player)),
                FormatUtil.stripEssentialsFormat(jda.getPlugin().getEss().getPermissionsHandler().getSuffix(player)),
                jda.getGuild().getMember(jda.getJda().getSelfUser()).getEffectiveName());

            uuid = player.getUniqueId();
        }

        DiscordUtil.dispatchDiscordMessage(jda, messageType, message, false, avatarUrl, name, uuid);
    }
}
