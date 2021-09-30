package com.earth2me.essentials.chat;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.events.LocalChatSpyEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class EssentialsChatPlayerListenerNormal extends EssentialsChatPlayer {
    EssentialsChatPlayerListenerNormal(final Server server, final IEssentials ess, final Map<AsyncPlayerChatEvent, ChatStore> chatStorage) {
        super(server, ess, chatStorage);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    @Override
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        if (isAborted(event)) {
            return;
        }

        // This file should handle detection of the local chat features; if local chat is enabled, we need to handle it here
        long radius = ess.getSettings().getChatRadius();
        if (radius < 1) {
            return;
        }
        radius *= radius;

        final ChatStore chatStore = getChatStore(event);
        final User user = chatStore.getUser();
        chatStore.setRadius(radius);

        if (event.getMessage().length() > 1) {
            if (chatStore.getType().isEmpty()) {
                if (!user.isAuthorized("essentials.chat.local")) {
                    user.sendMessage(tl("notAllowedToLocal"));
                    event.setCancelled(true);
                    return;
                }

                if (user.isToggleShout() && event.getMessage().length() > 1 && event.getMessage().charAt(0) == ess.getSettings().getChatShout()) {
                    event.setMessage(event.getMessage().substring(1));
                }

                event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive.local"));
            } else {
                final String permission = "essentials.chat." + chatStore.getType();

                if (user.isAuthorized(permission)) {
                    if (event.getMessage().charAt(0) == ess.getSettings().getChatShout() || (event.getMessage().charAt(0) == ess.getSettings().getChatQuestion() && ess.getSettings().isChatQuestionEnabled())) {
                        event.setMessage(event.getMessage().substring(1));
                    }
                    event.setFormat(tl(chatStore.getType() + "Format", event.getFormat()));
                    event.getRecipients().removeIf(player -> !ess.getUser(player).isAuthorized("essentials.chat.receive." + chatStore.getType()));
                    return;
                }

                user.sendMessage(tl("notAllowedTo" + chatStore.getType().substring(0, 1).toUpperCase(Locale.ENGLISH) + chatStore.getType().substring(1)));
                event.setCancelled(true);
                return;
            }
        }

        final Location loc = user.getLocation();
        final World world = loc.getWorld();

        if (!charge(event, chatStore)) {
            return;
        }

        final Set<Player> outList = event.getRecipients();
        final Set<Player> spyList = new HashSet<>();

        try {
            outList.add(event.getPlayer());
        } catch (final UnsupportedOperationException ex) {
            if (ess.getSettings().isDebug()) {
                logger.log(Level.INFO, "Plugin triggered custom chat event, local chat handling aborted.", ex);
            }
            return;
        }

        final String format = event.getFormat();
        event.setFormat(tl("chatTypeLocal").concat(event.getFormat()));

        final Iterator<Player> it = outList.iterator();
        while (it.hasNext()) {
            final Player onlinePlayer = it.next();
            final User onlineUser = ess.getUser(onlinePlayer);
            if (!onlineUser.equals(user)) {
                boolean abort = false;
                final Location playerLoc = onlineUser.getLocation();
                if (playerLoc.getWorld() != world) {
                    abort = true;
                } else {
                    final double delta = playerLoc.distanceSquared(loc);
                    if (delta > chatStore.getRadius()) {
                        abort = true;
                    }
                }
                if (abort) {
                    if (onlineUser.isAuthorized("essentials.chat.spy")) {
                        spyList.add(onlinePlayer);
                    }
                    it.remove();
                }
            }
        }

        if (outList.size() < 2) {
            user.sendMessage(tl("localNoOne"));
        }

        final LocalChatSpyEvent spyEvent = new LocalChatSpyEvent(event.isAsynchronous(), event.getPlayer(), format, event.getMessage(), spyList);
        server.getPluginManager().callEvent(spyEvent);

        if (!spyEvent.isCancelled()) {
            for (final Player onlinePlayer : spyEvent.getRecipients()) {
                onlinePlayer.sendMessage(String.format(spyEvent.getFormat(), user.getDisplayName(), spyEvent.getMessage()));
            }
        }
    }
}
