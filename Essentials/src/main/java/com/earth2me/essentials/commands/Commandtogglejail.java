package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.ISettings;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.EnumUtil;
import com.google.common.collect.Iterables;
import net.ess3.api.events.JailStatusChangeEvent;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandtogglejail extends EssentialsCommand {
    private static final Statistic PLAY_ONE_TICK = EnumUtil.getStatistic("PLAY_ONE_MINUTE", "PLAY_ONE_TICK");

    public Commandtogglejail() {
        super("togglejail");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final User player = getPlayer(server, args, 0, true, true);

        mainCommand:
        if (!player.isJailed()) {
            if (!player.getBase().isOnline()) {
                if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.togglejail.offline")) {
                    sender.sendTl("mayNotJailOffline");
                    return;
                }
            }

            if (player.isAuthorized("essentials.jail.exempt")) {
                sender.sendTl("mayNotJail");
                return;
            }

            final String jailName;
            if (args.length > 1) {
                jailName = args[1];
            } else if (ess.getJails().getCount() == 1) {
                jailName = Iterables.get(ess.getJails().getList(), 0);
            } else {
                break mainCommand;
            }
            // Check if jail exists
            ess.getJails().getJail(jailName);

            final JailStatusChangeEvent event = new JailStatusChangeEvent(player, sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, true);
            ess.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                long displayTime = 0;
                long preTimeDiff = 0;
                if (args.length > 2) {
                    final String time = getFinalArg(args, 2);
                    displayTime = DateUtil.parseDateDiff(time, true);
                    preTimeDiff = DateUtil.parseDateDiff(time, true, ess.getSettings().isJailOnlineTime());
                }
                final long timeDiff = preTimeDiff;
                final long finalDisplayTime = displayTime;
                final CompletableFuture<Boolean> future = getNewExceptionFuture(sender, commandLabel);
                future.thenAccept(success -> {
                    if (success) {
                        player.setJailed(true);
                        player.sendTl("userJailed");
                        player.setJail(null);
                        player.setJail(jailName);
                        if (args.length > 2) {
                            player.setJailTimeout(timeDiff);
                            // 50 MSPT (milliseconds per tick)
                            player.setOnlineJailedTime(ess.getSettings().isJailOnlineTime() ? ((player.getBase().getStatistic(PLAY_ONE_TICK)) + (timeDiff / 50)) : 0);
                        }

                        final String tlKey;
                        final Object[] objects;
                        if (timeDiff > 0) {
                            tlKey = "jailNotifyJailedFor";
                            objects = new Object[]{player.getName(), DateUtil.formatDateDiff(finalDisplayTime)};
                            sender.sendTl("playerJailedFor", player.getName(), DateUtil.formatDateDiff(finalDisplayTime));
                        } else {
                            tlKey = "jailNotifyJailed";
                            objects = new Object[]{player.getName(), sender.getSender().getName()};
                            sender.sendTl("playerJailed", player.getName());
                        }

                        ess.getLogger().log(Level.INFO, tlLiteral(tlKey, objects));
                        ess.broadcastTl(null, "essentials.jail.notify", tlKey, objects);
                    }
                });
                if (player.getBase().isOnline()) {
                    ess.getJails().sendToJail(player, jailName, future);
                } else {
                    future.complete(true);
                }
            }
            return;
        }

        if (args.length >= 2 && player.isJailed() && !args[1].equalsIgnoreCase(player.getJail())) {
            sender.sendTl("jailAlreadyIncarcerated", player.getJail());
            return;
        }

        if (args.length >= 2 && player.isJailed() && args[1].equalsIgnoreCase(player.getJail())) {
            final String unparsedTime = getFinalArg(args, 2);
            final long displayTimeDiff = DateUtil.parseDateDiff(unparsedTime, true);
            final long timeDiff = DateUtil.parseDateDiff(unparsedTime, true, ess.getSettings().isJailOnlineTime());
            player.setJailTimeout(timeDiff);
            player.setOnlineJailedTime(ess.getSettings().isJailOnlineTime() ? ((player.getBase().getStatistic(PLAY_ONE_TICK)) + (timeDiff / 50)) : 0);
            sender.sendTl("jailSentenceExtended", DateUtil.formatDateDiff(displayTimeDiff));

            final String tlKey = "jailNotifySentenceExtended";
            final Object[] objects = new Object[]{player.getName(), DateUtil.formatDateDiff(displayTimeDiff), sender.getSender().getName()};
            ess.getLogger().log(Level.INFO, tlLiteral(tlKey, objects));
            ess.broadcastTl(null, "essentials.jail.notify", tlKey, objects);
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
                player.sendTl("jailReleasedPlayerNotify");
                player.setJail(null);
                if (player.getBase().isOnline()) {
                    final CompletableFuture<Boolean> future = getNewExceptionFuture(sender, commandLabel);
                    future.thenAccept(success -> {
                        if (success) {
                            sender.sendTl("jailReleased", player.getName());
                        }
                    });
                    if (ess.getSettings().getTeleportWhenFreePolicy() == ISettings.TeleportWhenFreePolicy.BACK) {
                        player.getAsyncTeleport().back(future);
                        future.exceptionally(e -> {
                            player.getAsyncTeleport().respawn(null, PlayerTeleportEvent.TeleportCause.PLUGIN, new CompletableFuture<>());
                            sender.sendTl("jailReleased", player.getName());
                            return false;
                        });
                    } else if (ess.getSettings().getTeleportWhenFreePolicy() == ISettings.TeleportWhenFreePolicy.SPAWN) {
                        player.getAsyncTeleport().respawn(null, PlayerTeleportEvent.TeleportCause.PLUGIN, future);
                    }
                    return;
                }
                sender.sendTl("jailReleased", player.getName());
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
