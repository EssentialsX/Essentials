package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import net.ess3.api.events.MuteStatusChangeEvent;
import org.bukkit.Server;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;


public class Commandmute extends EssentialsCommand {
    public Commandmute() {
        super("mute");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        boolean nomatch = false;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        User user;
        try {
            user = getPlayer(server, args, 0, true, true);
        } catch (PlayerNotFoundException e) {
            nomatch = true;
            user = ess.getUser(new OfflinePlayer(args[0], ess.getServer()));
        }
        if (!user.getBase().isOnline()) {
            if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.mute.offline")) {
                throw new Exception(tl("muteExemptOffline"));
            }
        } else {
            if (user.isAuthorized("essentials.mute.exempt") && sender.isPlayer()) {
                throw new Exception(tl("muteExempt"));
            }
        }

        long muteTimestamp = 0;
        String time;
        String muteReason = "";

        if (args.length > 1) {
            time = args[1];
            try {
                muteTimestamp = DateUtil.parseDateDiff(time, true);
                muteReason = getFinalArg(args, 2);
            } catch (Exception e) {
                muteReason = getFinalArg(args, 1);
            }
            final long maxMuteLength = ess.getSettings().getMaxMute() * 1000;
            if (maxMuteLength > 0 && ((muteTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxMuteLength) && sender.isPlayer() && !(ess.getUser(sender.getPlayer()).isAuthorized("essentials.mute.unlimited"))) {
                sender.sendMessage(tl("oversizedMute"));
                throw new NoChargeException();
            }
        }
        
        final boolean willMute = (args.length > 1) || !user.getMuted();
        final User controller = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
        final MuteStatusChangeEvent event = new MuteStatusChangeEvent(user, controller, willMute);
        ess.getServer().getPluginManager().callEvent(event);
        
        if (!event.isCancelled()) {
            if (args.length > 1) {
                user.setMuteReason(muteReason.isEmpty() ? null : muteReason);
                user.setMuted(true);
            } else {
                user.setMuted(!user.getMuted());
                if (!user.getMuted()) {
                    user.setMuteReason(null);
                }
            }
            user.setMuteTimeout(muteTimestamp);
            final boolean muted = user.getMuted();
            String muteTime = DateUtil.formatDateDiff(muteTimestamp);

            if (nomatch) {
                sender.sendMessage(tl("userUnknown", user.getName()));
            }

            if (muted) {
                if (muteTimestamp > 0) {
                    if (!user.hasMuteReason()) {
                        sender.sendMessage(tl("mutedPlayerFor", user.getDisplayName(), muteTime));
                        user.sendMessage(tl("playerMutedFor", muteTime));
                    } else {
                        sender.sendMessage(tl("mutedPlayerForReason", user.getDisplayName(), muteTime, user.getMuteReason()));
                        user.sendMessage(tl("playerMutedForReason", muteTime, user.getMuteReason()));
                    }
                } else {
                    if (!user.hasMuteReason()) {
                        sender.sendMessage(tl("mutedPlayer", user.getDisplayName()));
                        user.sendMessage(tl("playerMuted"));
                    } else {
                        sender.sendMessage(tl("mutedPlayerReason", user.getDisplayName(), user.getMuteReason()));
                        user.sendMessage(tl("playerMutedReason", user.getMuteReason()));
                    }
                }
                final String message;
                if (muteTimestamp > 0) {
                    if (!user.hasMuteReason()) {
                        message = tl("muteNotifyFor", sender.getSender().getName(), user.getName(), muteTime);
                    } else {
                        message = tl("muteNotifyForReason", sender.getSender().getName(), user.getName(), muteTime, user.getMuteReason());
                    }
                } else {
                    if (!user.hasMuteReason()) {
                        message = tl("muteNotify", sender.getSender().getName(), user.getName());
                    } else {
                        message = tl("muteNotifyReason", sender.getSender().getName(), user.getName(), user.getMuteReason());
                    }
                }
                server.getLogger().log(Level.INFO, message);
                ess.broadcastMessage("essentials.mute.notify", message);
            } else {
                sender.sendMessage(tl("unmutedPlayer", user.getDisplayName()));
                user.sendMessage(tl("playerUnmuted"));
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return COMMON_DATE_DIFFS; // Date diff can span multiple words
        }
    }
}
