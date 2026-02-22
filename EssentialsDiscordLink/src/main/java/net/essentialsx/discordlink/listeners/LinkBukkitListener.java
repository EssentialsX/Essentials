package net.essentialsx.discordlink.listeners;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.IUser;
import net.ess3.api.events.NickChangeEvent;
import net.essentialsx.api.v2.events.AsyncUserDataLoadEvent;
import net.essentialsx.api.v2.events.UserMailEvent;
import net.essentialsx.api.v2.events.discord.DiscordMemberUpdateEvent;
import net.essentialsx.api.v2.events.discord.DiscordMessageEvent;
import net.essentialsx.api.v2.events.discordlink.DiscordLinkStatusChangeEvent;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import net.essentialsx.api.v2.services.discord.MessageType;
import net.essentialsx.discord.util.MessageUtil;
import net.essentialsx.discordlink.DiscordLinkSettings;
import net.essentialsx.discordlink.EssentialsDiscordLink;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

import static com.earth2me.essentials.I18n.tlLiteral;

public class LinkBukkitListener implements Listener {
    private final EssentialsDiscordLink ess;

    public LinkBukkitListener(EssentialsDiscordLink ess) {
        this.ess = ess;
    }

    /**
     * Sets the Minecraft nickname for a user based on their Discord member info.
     * Must be called from the main thread.
     *
     * @param user     the Essentials user
     * @param nickname the nickname to set, or null to clear
     */
    private void syncNickname(final IUser user, final String nickname) {
        final NickChangeEvent nickEvent = new NickChangeEvent(user, user, nickname);
        ess.getServer().getPluginManager().callEvent(nickEvent);
        if (!nickEvent.isCancelled()) {
            final User essUser = ess.getEss().getUser(user.getUUID());
            essUser.setNickname(nickname);
            essUser.setDisplayNick();
        }
    }

    /**
     * Gets the effective nickname from a Discord member (nickname or username).
     */
    private String getEffectiveNickname(final InteractionMember member) {
        final String nickname = member.getNickname();
        return nickname != null ? nickname : member.getName();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMail(final UserMailEvent event) {
        if (!ess.getSettings().isRelayMail()) {
            return;
        }

        final String discordId = ess.getLinkManager().getDiscordId(event.getRecipient().getBase().getUniqueId());
        if (discordId == null) {
            return;
        }

        final String sanitizedName = MessageUtil.sanitizeDiscordMarkdown(event.getMessage().getSenderUsername());
        final String sanitizedMessage = MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripFormat(event.getMessage().getMessage()));

        ess.getApi().getMemberById(discordId).thenAccept(member -> {
            if (member != null) {
                member.sendPrivateMessage(tlLiteral("discordMailLine", sanitizedName, sanitizedMessage));
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onConnect(final AsyncPlayerPreLoginEvent event) {
        if (ess.getSettings().getLinkPolicy() != DiscordLinkSettings.LinkPolicy.KICK) {
            return;
        }

        if (!ess.getLinkManager().isLinked(event.getUniqueId())) {
            String code;
            try {
                code = ess.getLinkManager().createCode(event.getUniqueId());
            } catch (IllegalArgumentException e) {
                code = e.getMessage();
            }
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, AdventureUtil.miniToLegacy(tlLiteral("discordLinkLoginKick", "/link " + code, ess.getApi().getInviteUrl())));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(final PlayerInteractEvent event) {
        if (ess.getSettings().getLinkPolicy() != DiscordLinkSettings.LinkPolicy.FREEZE) {
            return;
        }

        if (!ess.getLinkManager().isLinked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (ess.getSettings().getLinkPolicy() != DiscordLinkSettings.LinkPolicy.FREEZE) {
            return;
        }

        //todo maybe allowed commands
        if (!ess.getLinkManager().isLinked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            String code;
            try {
                code = ess.getLinkManager().createCode(event.getPlayer().getUniqueId());
            } catch (IllegalArgumentException e) {
                code = e.getMessage();
            }
            ess.getEss().getUser(event.getPlayer()).sendTl("discordLinkLoginPrompt", "/link " + code, ess.getApi().getInviteUrl());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (ess.getSettings().getLinkPolicy() != DiscordLinkSettings.LinkPolicy.FREEZE) {
            return;
        }

        if (!ess.getLinkManager().isLinked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            String code;
            try {
                code = ess.getLinkManager().createCode(event.getPlayer().getUniqueId());
            } catch (IllegalArgumentException e) {
                code = e.getMessage();
            }
            ess.getEss().getUser(event.getPlayer()).sendTl("discordLinkLoginPrompt", "/link " + code, ess.getApi().getInviteUrl());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserDataLoad(final AsyncUserDataLoadEvent event) {
        final UUID uuid = event.getUser().getBase().getUniqueId();
        final String discordId = ess.getLinkManager().getDiscordId(uuid);
        final boolean isLinked = discordId != null;

        // Sync nickname on join for linked players
        if (ess.getSettings().isNicknameLinked() && isLinked) {
            ess.getApi().getMemberById(discordId).thenAccept(member -> {
                if (member == null) {
                    return;
                }

                final String nickname = getEffectiveNickname(member);
                ess.getEss().scheduleSyncDelayedTask(() -> syncNickname(event.getUser(), nickname));
            });
        }

        // Handle freeze policy for unlinked players
        if (ess.getSettings().getLinkPolicy() == DiscordLinkSettings.LinkPolicy.FREEZE && !isLinked) {
            event.getUser().setFreeze(true);
            String code;
            try {
                code = ess.getLinkManager().createCode(uuid);
            } catch (IllegalArgumentException e) {
                code = e.getMessage();
            }
            event.getUser().sendTl("discordLinkLoginPrompt", "/link " + code, ess.getApi().getInviteUrl());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDiscordMessage(final DiscordMessageEvent event) {
        if (ess.getSettings().isBlockUnlinkedChat() && event.getType() == MessageType.DefaultTypes.CHAT && !ess.getLinkManager().isLinked(event.getUUID())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDiscordMemberUpdate(final DiscordMemberUpdateEvent event) {
        if (!ess.getSettings().isNicknameLinked()) {
            return;
        }

        final UUID uuid = ess.getLinkManager().getUUID(event.getMember().getId());
        if (uuid == null) {
            return;
        }

        // Only sync for online players - offline players will sync on join via onUserDataLoad
        final User user = ess.getEss().getUser(uuid);
        if (user == null || !user.getBase().isOnline()) {
            return;
        }

        final String nickname = getEffectiveNickname(event.getMember());
        ess.getEss().scheduleSyncDelayedTask(() -> syncNickname(user, nickname));
    }

    @EventHandler
    public void onUserLinkStatusChange(final DiscordLinkStatusChangeEvent event) {
        // Handle nickname sync on link/unlink
        if (ess.getSettings().isNicknameLinked() && event.getUser() != null) {
            final String nickname;
            if (event.isLinked() && event.getMember() != null) {
                nickname = getEffectiveNickname(event.getMember());
            } else {
                nickname = null;
            }

            if (Bukkit.isPrimaryThread()) {
                syncNickname(event.getUser(), nickname);
            } else {
                ess.getEss().scheduleSyncDelayedTask(() -> syncNickname(event.getUser(), nickname));
            }
        }

        // Handle freeze/unfreeze based on link status
        if (event.isLinked() || ess.getSettings().getLinkPolicy() == DiscordLinkSettings.LinkPolicy.NONE) {
            if (event.getUser() != null) {
                event.getUser().setFreeze(false);
            }
            return;
        }

        if (event.getUser() == null || !event.getUser().getBase().isOnline()) {
            return;
        }

        String code;
        try {
            code = ess.getLinkManager().createCode(event.getUser().getBase().getUniqueId());
        } catch (IllegalArgumentException e) {
            code = e.getMessage();
        }
        final String finalCode = code;

        switch (ess.getSettings().getLinkPolicy()) {
            case KICK: {
                final Runnable kickTask = () -> event.getUser().getBase().kickPlayer(AdventureUtil.miniToLegacy(event.getUser().playerTl("discordLinkLoginKick", "/link " + finalCode, ess.getApi().getInviteUrl())));
                if (Bukkit.isPrimaryThread()) {
                    kickTask.run();
                } else {
                    ess.getEss().scheduleSyncDelayedTask(kickTask);
                }
                break;
            }
            case FREEZE: {
                event.getUser().sendTl("discordLinkLoginPrompt", "/link " + code, ess.getApi().getInviteUrl());
                event.getUser().setFreeze(true);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
}
