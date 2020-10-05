package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import net.ess3.api.events.JailStatusChangeEvent;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;

public class Commandtogglejail extends EssentialsCommand {
    public Commandtogglejail() {
        super("togglejail");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final User player = getPlayer(server, args, 0, true, true);

        if (args.length >= 2 && !player.isJailed()) {
            if (!player.getBase().isOnline()) {
                if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.togglejail.offline")) {
                    sender.sendMessage(tl("mayNotJailOffline"));
                    return;
                }
            }

            if (player.isAuthorized("essentials.jail.exempt")) {
                sender.sendMessage(tl("mayNotJail"));
                return;
            }

            final JailStatusChangeEvent event = new JailStatusChangeEvent(player, sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, true);
            ess.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                long preTimeDiff = 0;
                if (args.length > 2) {
                    final String time = getFinalArg(args, 2);
                    preTimeDiff = DateUtil.parseDateDiff(time, true);

                }
                final long timeDiff = preTimeDiff;
                final CompletableFuture<Boolean> future = getNewExceptionFuture(sender, commandLabel);
                future.thenAccept(success -> {
                    if (success) {
                        player.setJailed(true);
                        player.sendMessage(tl("userJailed"));
                        player.setJail(null);
                        player.setJail(args[1]);
                        if (args.length > 2) {
                            player.setJailTimeout(timeDiff);
                        }
                        sender.sendMessage(timeDiff > 0 ? tl("playerJailedFor", player.getName(), DateUtil.formatDateDiff(timeDiff)) : tl("playerJailed", player.getName()));
                    }
                });
                if (player.getBase().isOnline()) {
                    ess.getJails().sendToJail(player, args[1], future);
                } else {
                    // Check if jail exists
                    ess.getJails().getJail(args[1]);
                    future.complete(true);
                }
            }
            return;
        }

        if (args.length >= 2 && player.isJailed() && !args[1].equalsIgnoreCase(player.getJail())) {
            sender.sendMessage(tl("jailAlreadyIncarcerated", player.getJail()));
            return;
        }

        if (args.length >= 2 && player.isJailed() && args[1].equalsIgnoreCase(player.getJail())) {
            final long timeDiff = DateUtil.parseDateDiff(getFinalArg(args, 2), true);
            player.setJailTimeout(timeDiff);
            sender.sendMessage(tl("jailSentenceExtended", DateUtil.formatDateDiff(timeDiff)));
            return;
        }

        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase(player.getJail()))) {
            if (!player.isJailed()) {
                throw new NotEnoughArgumentsException();
            }

            final JailStatusChangeEvent event = new JailStatusChangeEvent(player, sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, false);
            ess.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                player.setJailed(false);
                player.setJailTimeout(0);
                player.sendMessage(tl("jailReleasedPlayerNotify"));
                player.setJail(null);
                if (player.getBase().isOnline() && ess.getSettings().isTeleportBackWhenFreedFromJail()) {
                    final CompletableFuture<Boolean> future = getNewExceptionFuture(sender, commandLabel);
                    player.getAsyncTeleport().back(future);
                    future.thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(tl("jailReleased", player.getName()));
                        }
                    });
                    future.exceptionally(e -> {
                        player.getAsyncTeleport().respawn(null, PlayerTeleportEvent.TeleportCause.PLUGIN, new CompletableFuture<>());
                        sender.sendMessage(tl("jailReleased", player.getName()));
                        return false;
                    });
                    return;
                }
                sender.sendMessage(tl("jailReleased", player.getName()));
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else if (args.length == 2) {
            try {
                return new ArrayList<>(ess.getJails().getList());
            } catch (final Exception e) {
                return Collections.emptyList();
            }
        } else {
            return COMMON_DATE_DIFFS;
        }
    }
}
