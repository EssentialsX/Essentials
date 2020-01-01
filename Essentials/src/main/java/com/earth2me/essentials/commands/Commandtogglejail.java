package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import net.ess3.api.events.JailStatusChangeEvent;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


public class Commandtogglejail extends EssentialsCommand {
    public Commandtogglejail() {
        super("togglejail");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User player = getPlayer(server, args, 0, true, true);

        if (args.length >= 2 && !player.isJailed()) {
            if (!player.getBase().isOnline()) {
                if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.togglejail.offline")) {
                    sender.sendMessage(tl("mayNotJailOffline"));
                    return;
                }
            } else {
                if (player.isAuthorized("essentials.jail.exempt")) {
                    sender.sendMessage(tl("mayNotJail"));
                    return;
                }
            }
            final User controller = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
            final JailStatusChangeEvent event = new JailStatusChangeEvent(player, controller, true);
            ess.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (player.getBase().isOnline()) {
                    ess.getJails().sendToJail(player, args[1]);
                } else {
                    // Check if jail exists
                    ess.getJails().getJail(args[1]);
                }
                player.setJailed(true);
                player.sendMessage(tl("userJailed"));
                player.setJail(null);
                player.setJail(args[1]);
                long timeDiff = 0;
                if (args.length > 2) {
                    final String time = getFinalArg(args, 2);
                    timeDiff = DateUtil.parseDateDiff(time, true);
                    player.setJailTimeout(timeDiff);
                }
                sender.sendMessage((timeDiff > 0 ? tl("playerJailedFor", player.getName(), DateUtil.formatDateDiff(timeDiff)) : tl("playerJailed", player.getName())));
            }
            return;
        }

        if (args.length >= 2 && player.isJailed() && !args[1].equalsIgnoreCase(player.getJail())) {
            sender.sendMessage(tl("jailAlreadyIncarcerated", player.getJail()));
            return;
        }

        if (args.length >= 2 && player.isJailed() && args[1].equalsIgnoreCase(player.getJail())) {
            final String time = getFinalArg(args, 2);
            final long timeDiff = DateUtil.parseDateDiff(time, true);
            player.setJailTimeout(timeDiff);
            sender.sendMessage(tl("jailSentenceExtended", DateUtil.formatDateDiff(timeDiff)));
            return;
        }

        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase(player.getJail()))) {
            if (!player.isJailed()) {
                throw new NotEnoughArgumentsException();
            }
            final User controller = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
            final JailStatusChangeEvent event = new JailStatusChangeEvent(player, controller, false);
            ess.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                player.setJailed(false);
                player.setJailTimeout(0);
                player.sendMessage(tl("jailReleasedPlayerNotify"));
                player.setJail(null);
                if (player.getBase().isOnline()) {
                    player.getTeleport().back();
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
            } catch (Exception e) {
                return Collections.emptyList();
            }
        } else {
            return COMMON_DATE_DIFFS;
        }
    }
}
